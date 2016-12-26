package org.ozwillo.dcexporter.controller;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.service.CkanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/dc-model-mapping")
public class DcModelMappingController {

    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private CkanService ckanService;

    @RequestMapping(method = POST)
    public ResponseEntity<String> addMapping(@RequestBody DcModelMapping dcModelMapping) {
        CkanDataset ckanDataset = ckanService.getOrCreateDataset(dcModelMapping.getName(), dcModelMapping.getName()); //TODO : Remove the capital letter and special caracteres for first parametre before passing
        CkanResource ckanResource = ckanService.createResource(ckanDataset.getId(), dcModelMapping.getResourceName());

        dcModelMapping.setCkanPackageId(ckanDataset.getId());
        dcModelMapping.setCkanResourceId(ckanResource.getId());

        dcModelMappingRepository.save(dcModelMapping);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
