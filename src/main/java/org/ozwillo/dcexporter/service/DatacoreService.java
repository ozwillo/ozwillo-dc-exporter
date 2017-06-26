package org.ozwillo.dcexporter.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.oasis_eu.spring.datacore.DatacoreClient;
import org.oasis_eu.spring.datacore.model.*;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.model.Geocoding.GeocodingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatacoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatacoreService.class);

    @Autowired
    private DatacoreClient datacore;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private DcModelMappingRepository dcModelMappingRepository;

    @Value("${datacore.modifiedField}")
    private String modifiedField;

    @Value("${geocoding.url}")
    private String geocodingUrl;

    @Autowired
    private GeocodingClientService geocodingClientService;

    @Cacheable("dc-models")
    public List<DCModel> getModels() {
        // TODO : iterate until we have all
        return datacore.findModels(50).stream()
            .filter(dcModel -> dcModelMappingRepository.findByDcIdAndIsDeleted(dcModel.getId().toString(),false) == null)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public DCModel getModel(String project, String modelType) {
        return datacore.findModel(project, modelType);
    }

    public boolean hasMoreRecentResources(String project, String type, DateTime fromDate) {
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        DCQueryParameters parameters = new DCQueryParameters(modifiedField, DCOperator.GTE, dateTimeFormatter.print(fromDate));

        List<DCResource> newResources = datacore.findResources(project, type, parameters, 0, 1);
        LOGGER.debug("Retrieved {} resources newer than {}", newResources.size(), dateTimeFormatter.print(fromDate));
        return !newResources.isEmpty();
    }

    Map<String,Optional<String>> exportResource(String project, String type, List<String> excludedFields,
                                                String addressField, String postalCodeField, String cityField) {

        // Extract all possible columns from the type's model (filtering explicitely excluded ones)
        DCModel model = datacore.findModel(project, type);
        List<String> resourceKeys = model.getFields().stream()
                .filter(field -> excludedFields == null || !excludedFields.contains(field.getName()))
                .map(DCModel.DcModelField::getName)
                .collect(Collectors.toList());

        boolean geocoding = false;
        if(!StringUtils.isEmpty(addressField) || !StringUtils.isEmpty(postalCodeField) || !StringUtils.isEmpty(cityField)){
            resourceKeys.add("lon");
            resourceKeys.add("lat");
            geocoding = true;
        }
        // And add a fake column to allow for easier charting in CKAN
        resourceKeys.add("fake-weight");

        StringWriter resourceCsv = new StringWriter();
        StringWriter resourceJson = new StringWriter();

        writeCsvFileHeader( resourceCsv, resourceKeys);

        DCQueryParameters parameters = new DCQueryParameters("@id", DCOrdering.DESCENDING);
        int count = 0;
        while (true) {
            List<DCResource> intermediateResult = datacore.findResources(project, type, parameters, 0, 100);
            if(geocoding) {
                addGeocodingToResource(intermediateResult, addressField, postalCodeField, cityField);
            }
            writeCsvFileLines( resourceCsv, resourceKeys, intermediateResult);
            writeJsonFileLines( resourceJson, excludedFields, intermediateResult);
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
        Map<String, Optional<String>> resourceFiles = new HashMap<>();

        resourceFiles.put("csv", Optional.of(resourceCsv.toString()));
        resourceFiles.put("json", Optional.of(resourceJson.toString()));

        LOGGER.debug("Got {} resources to export", count);

        return resourceFiles;
    }

    private void writeCsvFileLines( StringWriter resourceCsv, List<String> resourceKeys, List<DCResource> resources){

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
    }

    private void writeCsvFileHeader( StringWriter resourceCsv, List<String> resourceKeys) {
        Optional<String> header = resourceKeys.stream().reduce((result, key) -> result + "," + key);
        if (header.isPresent()) {
            resourceCsv.write(header.get());
            resourceCsv.write("\n");
        }
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

    private void writeJsonFileLines( StringWriter resourceJson, List<String> excludedFields, List<DCResource> resources){
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode arrayResource = mapper.createArrayNode();

        resources.forEach(resource -> {
            excludedFields.forEach(key -> {
                resource.getValues().remove(key);
            });
            ObjectNode objectResource = writeDCResource(resource.getValues());
            arrayResource.add(objectResource);
        });
        resourceJson.write(arrayResource.toString());
    }

    private ObjectNode writeDCResource(Map<String,DCResource.Value> values){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectResource = mapper.createObjectNode();
        values.forEach((key,value) ->{
            if (value == null ) {
                LOGGER.debug("No value for this key, skipping");
                objectResource.put(key, "");
            } else if (value.isString()) {
                objectResource.put(key, value.asString());
            } else if (value.isMap()) {
                objectResource.putPOJO(key,value.asMap());
            } else if (value.isArray()) {
                ArrayNode resourceArray = mapper.createArrayNode();
                List<DCResource.Value> resourceRowInnerValues = value.asArray();
                if (resourceRowInnerValues.isEmpty())
                    resourceArray.add("");
                else if (resourceRowInnerValues.get(0).isString()) {
                    resourceRowInnerValues.forEach(resourceRowInnerValue -> resourceArray.add(resourceRowInnerValue.asString()));
                }
                else if (resourceRowInnerValues.get(0).isMap()) {
                    resourceRowInnerValues.forEach(resourceRowInnerValue -> {
                        ObjectNode objectInnerValue = writeDCResource(resourceRowInnerValue.asMap());
                        resourceArray.add(objectInnerValue);
                    });
                } else {
                    LOGGER.warn("Inner row value not managed for {}", resourceRowInnerValues);
                    resourceArray.add("");
                }
                objectResource.put(key,resourceArray);
            }
        });
        return objectResource;
    }

    private void addGeocodingToResource(List<DCResource> intermediateResult, String addressField, String postalCodeField, String cityField) {
        intermediateResult.forEach(resource -> {
            String address = "";
            if(!StringUtils.isEmpty(addressField) && resource.getValues().get(addressField) != null) {
                if (resource.getValues().get(addressField).isString()) address += resource.getAsString(addressField).concat(",");
            }
            if(!StringUtils.isEmpty(cityField) && resource.getValues().get(cityField) != null) {
                DCResource.Value resourceValue = resource.getValues().get(cityField);
                if(resourceValue.isString()) address += resource.getAsString(cityField).concat(",");
                if(resourceValue.isArray()) {
                    List<DCResource.Value> resourceRowInnerValues = resourceValue.asArray();
                    if (resourceRowInnerValues.get(0).isString()) {
                        Optional<String> reducedValue =
                                resource.getAsStringList(cityField).stream().reduce((result, value) -> result + "," + value);
                        address += reducedValue.get().concat(",");
                    } else if (resourceRowInnerValues.get(0).isMap()) {
                        address += getI18nFieldValueFromList(resourceRowInnerValues, "fr").concat(",");
                    }
                }
            }
            if(!StringUtils.isEmpty(postalCodeField) && resource.getValues().get(postalCodeField) != null) {
                if(resource.getValues().get(postalCodeField).isString()) address += resource.getAsString(postalCodeField);
            }

            if(!StringUtils.isEmpty(address)){
                Optional<GeocodingResponse> bnaResponse = geocodingClientService.getFeatures(geocodingUrl, address);
                if (bnaResponse.isPresent()) {
                    LOGGER.debug("Address {} geocoding in coordinates {}", address, bnaResponse.get().getFeatures().get(0).getGeometry().getCoordinates());
                    resource.set("lon", bnaResponse.get().getFeatures().get(0).getGeometry().getCoordinates().get(0).toString());
                    resource.set("lat", bnaResponse.get().getFeatures().get(0).getGeometry().getCoordinates().get(1).toString());
                } else {
                    LOGGER.info("No geocoding results for this address {}", address);
                }
            } else {
                LOGGER.debug("Address field it's empty or null");
            }
        });
    }
}
