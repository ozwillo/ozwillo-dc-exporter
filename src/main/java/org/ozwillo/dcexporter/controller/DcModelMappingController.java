package org.ozwillo.dcexporter.controller;

import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.model.ui.AuditLogWapper;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.service.CkanService;
import org.ozwillo.dcexporter.service.DcModelMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/dc-model-mapping")
public class DcModelMappingController {

    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private CkanService ckanService;
    @Autowired
    private DcModelMappingService dcModelMappingService;

    @RequestMapping(value = "/models",method = POST)
    public ResponseEntity<String> addMapping(@RequestBody DcModelMapping dcModelMapping) {
        if (dcModelMappingRepository.findByDcId(dcModelMapping.getDcId()) == null) {
            CkanDataset ckanDataset;
            try {
                ckanDataset = ckanService.getOrCreateDataset(dcModelMapping);
            } catch (CkanException e) {
                if (e.getCkanResponse() != null && e.getCkanResponse().getError() != null)
                   return new ResponseEntity<>(e.getCkanResponse().getError().getMessage(), HttpStatus.CONFLICT);
                else
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }

            CkanResource ckanResource = ckanService.createResource(ckanDataset.getId(), dcModelMapping.getResourceName(), dcModelMapping.getDescription());

            dcModelMapping.setCkanPackageId(ckanDataset.getId());
            dcModelMapping.setCkanResourceId(ckanResource.getId());

            dcModelMappingRepository.save(dcModelMapping);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>("This dataset is already synchronized", HttpStatus.CONFLICT);
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    public @ResponseBody
    List<AuditLogWapper> getLogs() {
        return dcModelMappingService.getAllAuditLogWithModel();
    }
}
