package org.ozwillo.dcexporter.service;

import javaslang.control.Either;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.Ckan.CkanResource;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.ui.AuditLogWapper;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.ozwillo.dcexporter.model.Ckan.CkanDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DcModelMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcModelMappingService.class);

    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private CkanService ckanService;

    @Autowired
    private SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    @Value("${ckan.url:http://localhost:5000}")
    private String ckanUrl;


    public DcModelMapping getById(String id) {
        return dcModelMappingRepository.findById(id);
    }

    public Either<String, DcModelMapping> add(DcModelMapping dcModelMapping) {
        Either<String, CkanDataset> eitherDataset = ckanService.getOrCreateDataset(dcModelMapping);
        if(eitherDataset.isLeft()) return Either.left(eitherDataset.getLeft());
        CkanDataset ckanDataset = eitherDataset.get();

        Either<String, CkanResource> eitherResource = ckanService.createResource(ckanDataset.getId(), dcModelMapping.getResourceName(), dcModelMapping.getDescription());
        if(eitherDataset.isLeft()) return Either.left(eitherDataset.getLeft());
        CkanResource ckanResource = eitherResource.get();

        dcModelMapping.setCkanPackageId(ckanDataset.getId());
        dcModelMapping.setCkanResourceId(ckanResource.getId());
        dcModelMapping.setDeleted(false);

        dcModelMapping = dcModelMappingRepository.save(dcModelMapping);
        return Either.right(dcModelMapping);
    }

    public Either<String, DcModelMapping> edit(DcModelMapping dcModelMapping) {

        if (dcModelMappingRepository.findByDcId(dcModelMapping.getDcId()) == null) return Either.left("dataset.notif.not_exist");

        Either<String, CkanDataset> eitherDataset = ckanService.getOrCreateDataset(dcModelMapping);
        if(eitherDataset.isLeft()) return Either.left(eitherDataset.getLeft());
        CkanDataset ckanDataset = eitherDataset.get();

        Either<String, CkanResource> eitherResource = ckanService.updateResource(dcModelMapping.getCkanResourceId(), ckanDataset.getId(), dcModelMapping.getResourceName(), dcModelMapping.getDescription());
        if(eitherDataset.isLeft()) return Either.left(eitherDataset.getLeft());
        CkanResource ckanResource = eitherResource.get();

        dcModelMapping.setCkanPackageId(ckanDataset.getId());
        dcModelMapping.setCkanResourceId(ckanResource.getId());

        dcModelMapping = dcModelMappingRepository.save(dcModelMapping);
        return Either.right(dcModelMapping);

    }

    public List<AuditLogWapper> getAllAuditLogWithModel() {
        return dcModelMappingRepository.findAllByOrderByResourceNameAsc().stream().map(dcModelMapping -> {
            SynchronizerAuditLog auditLog =
                    synchronizerAuditLogRepository.findFirstByTypeOrderByDateDesc(dcModelMapping.getType());
            String datasetUrl = ckanUrl  + "/dataset/" + ckanService.slugify(dcModelMapping.getName());
            String resourceUrl = ckanUrl  + "/dataset/" + ckanService.slugify(dcModelMapping.getName()) + "/resource/" + dcModelMapping.getCkanResourceId();
            return new AuditLogWapper(dcModelMapping, auditLog, datasetUrl, resourceUrl);
        }).collect(Collectors.toList());
    }

    public Either<String, DcModelMapping> deleteById(String id) {
        DcModelMapping dcModelMapping = dcModelMappingRepository.findById(id);
        if ( dcModelMapping == null ) return Either.left("dataset.notif.not_exist");
        else if ( dcModelMapping.isDeleted() ) return Either.left("dataset.notif.not_synchronized");
        ckanService.deleteResource(dcModelMapping.getCkanResourceId());
        dcModelMapping.setDeleted(true);
        dcModelMappingRepository.save(dcModelMapping);
        return Either.right(dcModelMapping);
    }
}
