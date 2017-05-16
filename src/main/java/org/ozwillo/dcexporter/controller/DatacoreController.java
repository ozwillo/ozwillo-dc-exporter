package org.ozwillo.dcexporter.controller;

import org.oasis_eu.spring.datacore.model.DCModel;
import org.ozwillo.dcexporter.service.DatacoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dc")
public class DatacoreController {

    @Autowired
    private DatacoreService datacoreService;

    @RequestMapping(value = "/models", method = RequestMethod.GET)
    public List<DCModel> getModels() {
        return datacoreService.getModels();
    }

    @RequestMapping(value = "/model/{project}/{type}", method = RequestMethod.GET)
    public DCModel getModel(@PathVariable String project, @PathVariable String type) {
        return datacoreService.getModel(project, type);
    }

}
