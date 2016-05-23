package org.ozwillo.dcexporter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class SynchronizerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizerService.class);

    @Autowired
    private DatacoreService datacoreService;

    @Autowired
    private CkanService ckanService;

    public String syncPoi() {
        Optional<File> optionalPoiCsvFile = datacoreService.exportPoiToCsv();
        if (!optionalPoiCsvFile.isPresent()) {
            LOGGER.error("Did not get the POI's CSV file, stopping");
            return "KO";
        }

        File poiCvsFile = optionalPoiCsvFile.get();

        ckanService.updatePoi(poiCvsFile);

        return "OK";
    }
}
