package org.ozwillo.dcexporter.controller;

import javaslang.control.Either;
import org.ozwillo.dcexporter.model.ui.AuditLogWapper;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.service.DcModelMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/dc-model-mapping")
public class DcModelMappingController {

    @Autowired
    private DcModelMappingService dcModelMappingService;

    @RequestMapping(value = "/model", method = RequestMethod.POST)
    public ResponseEntity<String> addMapping(@RequestBody DcModelMapping dcModelMapping) {
        Either<String, Boolean> result = dcModelMappingService.add(dcModelMapping);

        if (result.isRight())
            return new ResponseEntity<>(HttpStatus.CREATED);
        else
            return new ResponseEntity<>(result.getLeft(), HttpStatus.CONFLICT);

    }

    @RequestMapping(value = "/model/{id}", method = RequestMethod.GET)
    public DcModelMapping getMapping(@PathVariable String id) {
        return dcModelMappingService.getById(id);
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    public @ResponseBody
    List<AuditLogWapper> getLogs() {
        return dcModelMappingService.getAllAuditLogWithModel();
    }
}
