package org.ozwillo.dcexporter.controller;

import org.ozwillo.dcexporter.service.CkanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ckan")
public class CkanController {

    @Autowired
    private CkanService ckanService;

    @RequestMapping(value = "/licences", method = RequestMethod.GET)
    public Map<String, String> getLicences() {
        return ckanService.getLicences();
    }

    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public Map<String, String> getTags() { return ckanService.getTags(); }
}
