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

        Either<String, CkanResource> eitherResourceCsv = ckanService.createResource(ckanDataset.getId(), dcModelMapping.getResourceName(), dcModelMapping.getDescription());
        if(eitherResourceCsv.isLeft()) return Either.left(eitherResourceCsv.getLeft());
        CkanResource ckanResourceCsv = eitherResourceCsv.get();

        Either<String, CkanResource> eitherResourceJson = ckanService.createResource(ckanDataset.getId(), dcModelMapping.getResourceName(), dcModelMapping.getDescription());
        if(eitherResourceJson.isLeft()) return Either.left(eitherResourceJson.getLeft());
        CkanResource ckanResourceJson = eitherResourceJson.get();

        Map<String,String> ckanResourcesId = new HashMap<String,String>();
        ckanResourcesId.put("csv", ckanResourceCsv.getId());
        ckanResourcesId.put("json", ckanResourceJson.getId());

        dcModelMapping.setCkanPackageId(ckanDataset.getId());
        dcModelMapping.setUrl(ckanDataset.getName());
        dcModelMapping.setCkanResourceId(ckanResourcesId);
        dcModelMapping.setDeleted(false);

        dcModelMapping = dcModelMappingRepository.save(dcModelMapping);
        return Either.right(dcModelMapping);
    }

    public Either<String, DcModelMapping> edit(DcModelMapping dcModelMapping) {

        if (dcModelMappingRepository.findByDcId(dcModelMapping.getDcId()) == null) return Either.left("dataset.notif.not_exist");

        Either<String, CkanDataset> eitherDataset = ckanService.getOrCreateDataset(dcModelMapping);
        if(eitherDataset.isLeft()) return Either.left(eitherDataset.getLeft());
        CkanDataset ckanDataset = eitherDataset.get();
        Map<String,String> ckanResourcesId = new HashMap<String,String>();

        for(Map.Entry<String, String> entry : dcModelMapping.getCkanResourceId().entrySet()) {
            Either<String, CkanResource> eitherResource = ckanService.updateResource(entry.getValue(), ckanDataset.getId(), dcModelMapping.getResourceName(), dcModelMapping.getDescription(), entry.getKey());
            if(eitherResource.isLeft()) return Either.left(eitherResource.getLeft());
            CkanResource ckanResource = eitherResource.get();
            ckanResourcesId.put(entry.getKey(), ckanResource.getId());
        }

        dcModelMapping.setCkanPackageId(ckanDataset.getId());
        dcModelMapping.setUrl(ckanDataset.getName());
        dcModelMapping.setCkanResourceId(ckanResourcesId);

        dcModelMapping = dcModelMappingRepository.save(dcModelMapping);
        return Either.right(dcModelMapping);

    }

    public List<AuditLogWapper> getAllAuditLogWithModel() {
        return dcModelMappingRepository.findAllByOrderByResourceNameAsc().stream().map(dcModelMapping -> {
            SynchronizerAuditLog auditLog =
                    synchronizerAuditLogRepository.findFirstByTypeOrderByDateDesc(dcModelMapping.getType());
            String datasetUrl = ckanUrl  + "/dataset/" + dcModelMapping.getUrl();
            return new AuditLogWapper(dcModelMapping, auditLog, datasetUrl);
        }).collect(Collectors.toList());
    }

    public Either<String, DcModelMapping> deleteById(String id) {
        DcModelMapping dcModelMapping = dcModelMappingRepository.findById(id);
        if ( dcModelMapping == null ) return Either.left("dataset.notif.not_exist");
        else if ( dcModelMapping.isDeleted() ) return Either.left("dataset.notif.not_synchronized");
        dcModelMapping.getCkanResourceId().forEach((key,resourceId) -> {
            ckanService.deleteResource(resourceId);
        });
        dcModelMapping.setDeleted(true);
        dcModelMappingRepository.save(dcModelMapping);
        return Either.right(dcModelMapping);
    }
}
