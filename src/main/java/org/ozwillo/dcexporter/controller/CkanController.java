package org.ozwillo.dcexporter.controller;

import javaslang.control.Either;
import org.ozwillo.dcexporter.model.Ckan.CkanDataset;
import org.ozwillo.dcexporter.model.Ckan.CkanTag;
import org.ozwillo.dcexporter.service.CkanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> getLicences() {
        Either<String, Map<String, String>> either = ckanService.getLicences();
        if(either.isRight()) return new ResponseEntity<>(either.get(), HttpStatus.OK);
        else return new ResponseEntity<>(either.getLeft(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ResponseEntity<Object> getTags() {
        Either<String, List<CkanTag>> either = ckanService.getTags();
        if(either.isRight()) return new ResponseEntity<>(either.get(), HttpStatus.OK);
        else return new ResponseEntity<>(either.getLeft(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    public ResponseEntity<Object> getDatasets() {
        Either<String, List<CkanDataset>> either = ckanService.getDatasets();
        if(either.isRight()) return new ResponseEntity<>(either.get(), HttpStatus.OK);
        else return new ResponseEntity<>(either.getLeft(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
