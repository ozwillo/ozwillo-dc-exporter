package org.ozwillo.dcexporter.service;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.oasis_eu.spring.datacore.DatacoreClient;
import org.oasis_eu.spring.datacore.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties(prefix = "datacore")
public class DatacoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatacoreService.class);

    @Autowired
    private DatacoreClient datacore;

    private String modifiedField;

    // Binded through @ConfigurationProperties on the class
    // Do not use @Value since it will broke the binding into a list
    // (because we need to use the DataBinder, which @ConfigurationProperties does, but not @Value)
    private List<String> exportExcludedFields = new ArrayList<>();

    public List<DCModel> getModels() {
        // TODO : iterate until we have all
        return datacore.findModels(50);
    }

    public boolean hasMoreRecentResources(String project, String type, DateTime fromDate) {
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        DCQueryParameters parameters = new DCQueryParameters(modifiedField, DCOperator.GTE, dateTimeFormatter.print(fromDate));

        List<DCResource> newResources = datacore.findResources(project, type, parameters, 0, 1);
        LOGGER.debug("Retrieved {} resources newer than {}", newResources.size(), dateTimeFormatter.print(fromDate));
        return !newResources.isEmpty();
    }

    Optional<String> exportResourceToCsv(String project, String type) {

        // Extract all possible columns from the type's model (filtering explicitely excluded ones)
        DCModel model = datacore.findModel(project, type);
        List<String> resourceKeys = model.getFields().stream()
                .filter(field -> !exportExcludedFields.contains(field.getName()))
                .map(DCModel.DcModelField::getName)
                .collect(Collectors.toList());

        // And add a fake column to allow for easier charting in CKAN
        resourceKeys.add("fake-weight");

        StringWriter resourceCsv = new StringWriter();

        try {
            writeCsvFileHeader( resourceKeys, resourceCsv);
        } catch (IOException e) {
            LOGGER.error("Error while writing CSV file header", e);
            return Optional.empty();
        }

        DCQueryParameters parameters = new DCQueryParameters("@id", DCOrdering.DESCENDING);
        int count = 0;
        while (true) {
            List<DCResource> intermediateResult = datacore.findResources(project, type, parameters, 0, 100);
            try {
                writeCsvFileLines( resourceKeys, intermediateResult, resourceCsv);
            } catch (IOException e) {
                LOGGER.error("Error while writing data to temp file", e);
                return Optional.empty();
            }
            count += intermediateResult.size();

            if (intermediateResult.size() < 100) {
                break;
            } else {
                // TODO : why DCResource does not have the @id field in its data ?
                //        (it is present in the data returned from the DC)
                String lastResultId = intermediateResult.get(99).getUri();
                parameters = new DCQueryParameters("@id", DCOrdering.DESCENDING, DCOperator.LT, lastResultId);
            }
        }

        LOGGER.debug("Got {} resources to export", count);

        return Optional.of(resourceCsv.toString());
    }

    private void writeCsvFileLines( List<String> resourceKeys, List<DCResource> resources, StringWriter resourceCsv) throws IOException {

        resources.forEach(resource -> {
            LOGGER.debug("Resource : {}", resource);

            Optional<String> resourceRow =
                    resourceKeys.stream().map(key -> {
                        LOGGER.debug("Searching for key {}", key);
                        // special case for the fake weight column
                        // we want them all to be equal to 1 for charts
                        if (key.equals("fake-weight"))
                            return "1";

                        DCResource.Value resourceValue = resource.getValues().get(key);

                        if (resourceValue == null) {
                            LOGGER.debug("No value for this key, skipping");
                            return "";
                        } else if (resourceValue.isString()) {
                            return resource.getAsString(key);
                        } else if (resourceValue.isMap()) {
                            // TODO it seems like we neither get a map
                            return getI18nFieldValue(resource.getAsStringMap(key), "fr");
                        } else if (resourceValue.isArray()) {
                            List<DCResource.Value> resourceRowInnerValues = resourceValue.asArray();
                            if (resourceRowInnerValues.isEmpty())
                                return "";

                            if (resourceRowInnerValues.get(0).isString()) {
                                Optional<String> reducedValue =
                                        resource.getAsStringList(key).stream().reduce((result, value) -> result + "," + value);
                                return reducedValue.orElse("");
                            } else if (resourceRowInnerValues.get(0).isMap()) {
                                String frenchValue = getI18nFieldValueFromList(resourceRowInnerValues, "fr");
                                if (!frenchValue.isEmpty())
                                    return frenchValue;
                                else
                                    return getI18nFieldValueFromList(resourceRowInnerValues, "en");
                            } else {
                                LOGGER.warn("Inner row value not managed for {}", resourceRowInnerValues);
                                return "";
                            }
                        } else
                            return "";
                    }).map(value -> "\"" + value + "\"").reduce((result, value) -> result + "," + value);

            if (resourceRow.isPresent())
                    resourceCsv.write(resourceRow.get());
                    resourceCsv.write("\n");
        });

        resourceCsv.flush();
        resourceCsv.close();
    }

    private void writeCsvFileHeader( List<String> resourceKeys, StringWriter resourceCsv) throws IOException {
        Optional<String> header = resourceKeys.stream().reduce((result, key) -> result + "," + key);
        if (header.isPresent()) {
            resourceCsv.write(header.get());
            resourceCsv.write("\n");
        }

        resourceCsv.flush();
        resourceCsv.close();
    }

    private String getI18nFieldValue(Map<String, String> field, String lang) {
        Optional<Map.Entry<String,String>> value = field.entrySet().stream()
                .filter(entry -> lang.equals(entry.getValue())).findFirst();

        return value.isPresent() ? value.get().getKey() : "";
    }

    private String getI18nFieldValueFromList(List<DCResource.Value> valueList, String lang) {
        // Trying to handle structures like that :
        // resource:name=[{@value=Test Ozwillo, @language=en}, {@value=Test Ozwillo, @language=fr}, ...]
        // which are lists of two entries maps, second entry having the language for value !

        Optional<DCResource.Value> result = valueList.stream().filter(value ->
                value.asMap().values().toArray()[1].toString().equals(lang)
        ).findFirst();

        return result.isPresent() ? result.get().asMap().values().toArray()[0].toString() : "";
    }

    // needed for field binding into a list
    public List<String> getExportExcludedFields() {
        return exportExcludedFields;
    }

    public void setModifiedField(String modifiedField) {
        this.modifiedField = modifiedField;
    }
}
