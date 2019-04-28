package org.ozwillo.dcexporter.service;

import io.vavr.control.Either;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.Ckan.CkanResource;
import org.ozwillo.dcexporter.model.Ckan.Format;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerStatus;
import org.ozwillo.dcexporter.model.ui.AuditLogWapper;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.ozwillo.dcexporter.model.Ckan.CkanDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DcModelMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcModelMappingService.class);

    private final DcModelMappingRepository dcModelMappingRepository;
    private final CkanService ckanService;
    private final SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    @Value("${ckan.url:http://localhost:5000}")
    private String ckanUrl;

    @Autowired
    public DcModelMappingService(DcModelMappingRepository dcModelMappingRepository, CkanService ckanService,
                                 SynchronizerAuditLogRepository synchronizerAuditLogRepository) {
        this.dcModelMappingRepository = dcModelMappingRepository;
        this.ckanService = ckanService;
        this.synchronizerAuditLogRepository = synchronizerAuditLogRepository;
    }

    public Either<String, DcModelMapping> getById(String id) {
        Optional<DcModelMapping> opt = dcModelMappingRepository.findById(id);
        return opt.<Either<String, DcModelMapping>>map(Either::right).orElseGet(() -> Either.left("dataset.notif.not_exist"));
    }

    public Either<String, DcModelMapping> add(final DcModelMapping dcModelMapping) {
        Either<String, CkanDataset> eitherDataset = ckanService.getOrCreateDataset(dcModelMapping);
        if(eitherDataset.isLeft()) return Either.left(eitherDataset.getLeft());
        CkanDataset ckanDataset = eitherDataset.get();
        Map<String,String> ckanResourcesId = new HashMap<>();

        if (dcModelMapping.getPivotField() != null) {
            LOGGER.debug("Dataset has a pivot field, not pre-creating any resource on CKAN");
        } else {
            for (Format format: Format.values()) {
                Either<String, CkanResource> eitherResource =
                        ckanService.createResource(ckanDataset.getId(),
                                String.format("%s (%s)", dcModelMapping.getResourceName(), format.name()),
                                dcModelMapping.getDescription());
                if(eitherResource.isLeft()) return Either.left(eitherResource.getLeft());
                ckanResourcesId.put(format.name(), eitherResource.get().getId());
            }
        }

        dcModelMapping.setCkanPackageId(ckanDataset.getId());
        dcModelMapping.setUrl(ckanDataset.getName());
        dcModelMapping.setCkanResourceId(ckanResourcesId);
        dcModelMapping.setDeleted(false);
        dcModelMapping.setOrganizationId(ckanDataset.getOrganization().getId());

        DcModelMapping savedDcModelMapping = dcModelMappingRepository.save(dcModelMapping);
        SynchronizerAuditLog newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), SynchronizerStatus.PENDING, null,  LocalDateTime.now());
        synchronizerAuditLogRepository.save(newAuditLog);
        return Either.right(savedDcModelMapping);
    }

    public Either<String, DcModelMapping> edit(DcModelMapping dcModelMapping) {

        DcModelMapping oldDcModelMapping = dcModelMappingRepository.findByDcId(dcModelMapping.getDcId());

        if(oldDcModelMapping == null) return Either.left("dataset.notif.not_exist");

        SynchronizerAuditLog newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), SynchronizerStatus.MODIFIED, null,  LocalDateTime.now());
        synchronizerAuditLogRepository.save(newAuditLog);
        dcModelMappingRepository.save(dcModelMapping);

        if(!oldDcModelMapping.getName().equals(dcModelMapping.getName())){
            dcModelMapping.getCkanResourceId().forEach((key,resourceId) -> ckanService.deleteResource(resourceId));
            dcModelMapping.setUrl("");
            dcModelMapping.setCkanPackageId("");
            dcModelMapping.setCkanResourceId(new HashMap<>());
            Either<String, DcModelMapping> eitherDcModelMapping = this.add(dcModelMapping);
            if(eitherDcModelMapping.isLeft()) return Either.left(eitherDcModelMapping.getLeft());
            return Either.right(eitherDcModelMapping.get());
        }

        Either<String, CkanDataset> eitherDataset = ckanService.getOrCreateDataset(dcModelMapping);
        if(eitherDataset.isLeft()) return Either.left(eitherDataset.getLeft());
        CkanDataset ckanDataset = eitherDataset.get();
        Map<String,String> ckanResourcesId = new HashMap<>();

        for(Map.Entry<String, String> entry : dcModelMapping.getCkanResourceId().entrySet()) {
            Either<String, CkanResource> eitherResource = ckanService.updateResource(entry.getValue(), ckanDataset.getId(), dcModelMapping.getResourceName(), dcModelMapping.getDescription(), entry.getKey());
            if(eitherResource.isLeft()) return Either.left(eitherResource.getLeft());
            CkanResource ckanResource = eitherResource.get();
            ckanResourcesId.put(entry.getKey(), ckanResource.getId());
        }

        dcModelMapping.setCkanPackageId(ckanDataset.getId());
        dcModelMapping.setUrl(ckanDataset.getName());
        dcModelMapping.setCkanResourceId(ckanResourcesId);
        dcModelMapping.setOrganizationId(ckanDataset.getOrganization().getId());

        dcModelMapping = dcModelMappingRepository.save(dcModelMapping);
        return Either.right(dcModelMapping);

    }

    public List<AuditLogWapper> getAllAuditLogWithModel() {
        List<DcModelMapping> dcModelMappings = dcModelMappingRepository.findAllActive();
        dcModelMappings.sort(Comparator.comparing(DcModelMapping::getResourceName, String.CASE_INSENSITIVE_ORDER));
        return dcModelMappings.stream()
                .map(dcModelMapping -> {
                    SynchronizerAuditLog auditLog = synchronizerAuditLogRepository.findFirstByTypeOrderByDateDesc(dcModelMapping.getType());
                    String datasetUrl = ckanUrl  + "/dataset/" + dcModelMapping.getUrl();
                    return new AuditLogWapper(dcModelMapping, auditLog, datasetUrl);
                })
                .collect(Collectors.toList());
    }

    public Either<String, DcModelMapping> deleteById(String id) {
        Optional<DcModelMapping> opt = dcModelMappingRepository.findById(id);
        
        if (opt.isPresent()) {
            DcModelMapping dcModelMapping = opt.get();
            if (dcModelMapping.isDeleted()) 
                return Either.left("dataset.notif.not_synchronized");
            
            dcModelMapping.getCkanResourceId().forEach((key,resourceId) -> ckanService.deleteResource(resourceId));
            
            dcModelMapping.setDeleted(true);
            dcModelMappingRepository.save(dcModelMapping);
            return Either.right(dcModelMapping);
        }
        else {
            return Either.left("dataset.notif.not_exist");
        }
    }
}
