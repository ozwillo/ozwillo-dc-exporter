package org.ozwillo.dcexporter.service;

import org.oasis_eu.spring.datacore.DatacoreClient;
import org.oasis_eu.spring.datacore.model.DCModel;
import org.oasis_eu.spring.datacore.model.DCResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatacoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatacoreService.class);

    @Autowired
    private DatacoreClient datacore;

    public List<DCModel> getModels() {
        // TODO : iterate until we have all
        return datacore.findModels(50);
    }

    public String syncPoi() {
        List<DCResource> pois = datacore.findResources("poi_0", "poi:Geoloc_0", null, 0, 100);
        LOGGER.debug("Got {} pois", pois.size());

        // TODO : this can be quite non exhaustive as we can't guarantee the first field has all the possible fields
        // TODO : get the model instead
        DCResource firstPoi = pois.get(0);
        List<String> poiKeys = firstPoi.getValues().keySet().stream()
            .filter(key -> !"@type".equals(key))
            .collect(Collectors.toList());

        FileWriter poiFileWriter;
        try {
            File poiFile = File.createTempFile("poi", ".csv");
            poiFileWriter = new FileWriter(poiFile);

            Optional<String> header = poiKeys.stream().reduce((result, key) -> result + "," + key);
            if (header.isPresent()) {
                poiFileWriter.write(header.get());
                poiFileWriter.write("\n");
            }

            pois.forEach(poi -> {
                LOGGER.debug("Looking at poi {}", poi.getIri());
                LOGGER.debug("POI : {}", poi);
                Optional<String> poiRow =
                    poiKeys.stream().map(key -> {
                        LOGGER.debug("Searching for key {}", key);
                        DCResource.Value poiValue = poi.getValues().get(key);
                        if (poiValue == null) {
                            LOGGER.debug("No value for this key, skipping");
                            return "";
                        }

                        if (poiValue.isString())
                            return poi.getAsString(key);
                        else if (poiValue.isMap()) {
                            return getI18nFieldValue(poi.getAsStringMap(key), "fr");
                        } else if (poiValue.isArray()) {
                            List<DCResource.Value> poiRowInnerValues = poiValue.asArray();
                            if (poiRowInnerValues.isEmpty())
                                return "";

                            if (poiRowInnerValues.get(0).isString()) {
                                Optional<String> reducedValue =
                                    poi.getAsStringList(key).stream().reduce((result, value) -> result + "," + value);
                                return reducedValue.isPresent() ? reducedValue.get() : "";
                            } else if (poiRowInnerValues.get(0).isMap()) {
                                // Ugliest code ever to handle structures like that :
                                // poi:name=[{@value=Test Ozwillo, @language=en}, {@value=Test Ozwillo, @language=fr}, ...]
                                // which are lists of two entries maps, second entry having the language for value !
                                /*
                                LOGGER.debug("Size of the list : {}", poiRowInnerValues.size());
                                LOGGER.debug("Size of the map in the list : {}", poiRowInnerValues.get(0).asMap().size());
                                poiRowInnerValues.stream().forEach(value -> {
                                    value.asMap().entrySet().stream().forEach(stringValueEntry ->
                                        LOGGER.debug("{} - {}", stringValueEntry.getKey(), stringValueEntry.getValue())
                                    );
                                });
                                */
                                Optional<DCResource.Value> frenchValue = poiRowInnerValues.stream().filter(value ->
                                    value.asMap().values().toArray()[1].toString().equals("en")
                                ).findFirst();
                                LOGGER.debug("Got value : {}", frenchValue.isPresent());
                                return frenchValue.isPresent() ? frenchValue.get().asMap().values().toArray()[0].toString() : "";
                            } else {
                                LOGGER.warn("Inner row value not managed for {}", poiRowInnerValues);
                                return "";
                            }
                        }
                        else
                            return "";
                    }).reduce((result, value) -> result + ",\"" + value + "\"");

                if (poiRow.isPresent())
                    try {
                        poiFileWriter.write(poiRow.get());
                        poiFileWriter.write("\n");
                    } catch (IOException e) {
                        LOGGER.error("Error while writing row : {}", e.getMessage());
                    }
            });

            poiFileWriter.flush();
            poiFileWriter.close();
            LOGGER.info("Wrote temp data in file {}", poiFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Unable to create temp file : {}", e.getMessage());
            return "KO";
        }

        return "Ok";
    }

    private String getI18nFieldValue(Map<String, String> field, String lang) {
        Optional<Map.Entry<String,String>> frenchValue = field.entrySet().stream()
            .filter(entry -> lang.equals(entry.getValue())).findFirst();
        return frenchValue.isPresent() ? frenchValue.get().getKey() : "";
    }
}
