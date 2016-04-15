package org.ozwillo.dcexporter.service;

import org.oasis_eu.spring.datacore.DatacoreClient;
import org.oasis_eu.spring.datacore.model.DCModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatacoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatacoreService.class);

    @Autowired
    private DatacoreClient datacore;

    public List<DCModel> getModels() {
        // TODO : iterate until we have all
        return datacore.findModels(50);
    }
}
