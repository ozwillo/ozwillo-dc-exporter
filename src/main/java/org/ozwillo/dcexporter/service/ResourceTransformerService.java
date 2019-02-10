package org.ozwillo.dcexporter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.oasis_eu.spring.datacore.model.DCResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.ozwillo.dcexporter.util.DCResourceUtils.getI18nFieldValue;
import static org.ozwillo.dcexporter.util.DCResourceUtils.getI18nFieldValueFromList;

@Service
public class ResourceTransformerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceTransformerService.class);

    public String resourcesToCsv(List<DCResource> resources, List<String> resourceKeys) {

        StringWriter resourceCsv = new StringWriter();

        resourceCsv.append(csvFileHeader(resourceKeys));

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

            resourceRow.ifPresent(resourceCsv::write);
            resourceCsv.write("\n");
        });

        return resourceCsv.toString();
    }

    private String csvFileHeader(List<String> resourceKeys) {
        Optional<String> optHeader = resourceKeys.stream().reduce((result, key) -> result + "," + key);
        return optHeader.map(header -> new StringWriter().append(header).append("\n").toString())
                .orElse("");
    }

    public String resourcesToJson(List<DCResource> resources, List<String> excludedFields) {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode arrayResource = mapper.createArrayNode();

        resources.forEach(resource -> {
            Map<String,DCResource.Value> values = resource.getValues().entrySet().stream()
                    .filter(entry -> excludedFields.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            ObjectNode objectResource = writeDCResource(values);
            arrayResource.add(objectResource);
        });

        return arrayResource.toString();
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
                objectResource.set(key,resourceArray);
            }
        });
        return objectResource;
    }
}
