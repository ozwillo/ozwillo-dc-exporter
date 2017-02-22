package org.ozwillo.dcexporter.controller;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanTag;
import org.ozwillo.dcexporter.service.CkanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public List<CkanTag> getTags() { return ckanService.getTags(); }

    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    public List<CkanDataset> getDatasets() { return ckanService.getDatasets(); }
}
