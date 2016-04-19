package org.ozwillo.dcexporter.controller;

import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.model.DcModelMapping;
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

    @RequestMapping(method = POST)
    public ResponseEntity<String> addMapping(@RequestBody DcModelMapping dcModelMapping) {
        dcModelMappingRepository.save(dcModelMapping);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
