package org.ozwillo.dcexporter.controller;

import org.oasis_eu.spring.datacore.model.DCModel;
import org.ozwillo.dcexporter.service.DatacoreService;
import org.ozwillo.dcexporter.service.SynchronizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/dc")
public class DatacoreController {

    @Autowired
    private DatacoreService datacoreService;

    @Autowired
    private SynchronizerService synchronizerService;

    @RequestMapping(value = "/models", method = RequestMethod.GET)
    public List<DCModel> getModels() {
        return datacoreService.getModels();
    }

    @RequestMapping(value = "/sync-poi", method = RequestMethod.GET)
    public String syncPoi() {
        return synchronizerService.syncPoi();
    }
}
