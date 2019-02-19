package org.ozwillo.dcexporter.service;

import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.oasis_eu.spring.datacore.model.DCModel;
import org.oasis_eu.spring.datacore.model.DCResource;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.Ckan.CkanDataset;
import org.ozwillo.dcexporter.model.Ckan.CkanResource;
import org.ozwillo.dcexporter.model.Ckan.Format;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.ozwillo.dcexporter.model.SynchronizerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SynchronizerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizerService.class);

    private final DatacoreService datacoreService;
    private final CkanService ckanService;
    private final SystemUserService systemUserService;
    private final SynchronizerAuditLogRepository synchronizerAuditLogRepository;
    private final DcModelMappingRepository dcModelMappingRepository;
    private final ResourceTransformerService resourceTransformerService;
    private final GeocodingClientService geocodingClientService;

    @Autowired
    public SynchronizerService(DatacoreService datacoreService, CkanService ckanService, SystemUserService systemUserService,
                               SynchronizerAuditLogRepository synchronizerAuditLogRepository,
                               DcModelMappingRepository dcModelMappingRepository,
                               ResourceTransformerService resourceTransformerService,
                               GeocodingClientService geocodingClientService) {
        this.datacoreService = datacoreService;
        this.ckanService = ckanService;
        this.systemUserService = systemUserService;
        this.synchronizerAuditLogRepository = synchronizerAuditLogRepository;
        this.dcModelMappingRepository = dcModelMappingRepository;
        this.resourceTransformerService = resourceTransformerService;
        this.geocodingClientService = geocodingClientService;
    }

    @Scheduled(fixedDelayString = "${application.syncDelay}")
    public void synchronizeOrgs() {
        systemUserService.runAs(() -> {

            dcModelMappingRepository.findAll().forEach(dcModelMapping -> {
                SynchronizerAuditLog auditLog =
                        synchronizerAuditLogRepository.findFirstByTypeOrderByDateDesc(dcModelMapping.getType());

                if ((auditLog != null && auditLog.getStatus().equals(SynchronizerStatus.SUCCEEDED)) &&
                    !datacoreService.hasMoreRecentResources(dcModelMapping.getProject(), dcModelMapping.getType(), auditLog.getDate())) {
                    LOGGER.info("No more recent resources for {}, returning", dcModelMapping.getType());
                    return;
                }

                LOGGER.info("Got some recent data for {}, synchronizing them", dcModelMapping.getType());

                try {
                    this.sync(dcModelMapping);
                    SynchronizerAuditLog newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), SynchronizerStatus.SUCCEEDED, null,  LocalDateTime.now());
                    synchronizerAuditLogRepository.save(newAuditLog);
                } catch (Exception exception) {
                    LOGGER.error("Error while trying to synchronize model {} : {} ", dcModelMapping.getType(), exception.getMessage());
                    if (auditLog != null && auditLog.getStatus() == SynchronizerStatus.FAILED) {
                        // update current one
                        auditLog.updateOnError(exception.getMessage());
                        synchronizerAuditLogRepository.save(auditLog);
                    } else {
                        // create a new one with failed status
                        SynchronizerAuditLog newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), SynchronizerStatus.FAILED, exception.getMessage(),  LocalDateTime.now());
                        synchronizerAuditLogRepository.save(newAuditLog);
                    }
                }
            });
        });
    }

    @Scheduled(fixedDelayString = "${application.syncDatasetUrl}")
    public void synchronizeDatasetUrl() {
        systemUserService.runAs(() -> {
            dcModelMappingRepository.findAll().forEach(dcModelMapping -> {
                if(!dcModelMapping.isDeleted()) {
                    Either<String, CkanDataset> datasetOpt = ckanService.getOrCreateDataset(dcModelMapping);
                    if(datasetOpt.isRight()) {
                        LOGGER.info("Updating URL for dataset {}", dcModelMapping.getCkanPackageId());
                        // CKAN store the url in the name attribute
                        dcModelMapping.setUrl(datasetOpt.get().getName());
                        dcModelMappingRepository.save(dcModelMapping);
                    } else {
                        LOGGER.warn("Dataset not available for DCModelMapping: {}", dcModelMapping.getDcId());
                    }
                }
            });
        });
    }

    private void sync(DcModelMapping dcModelMapping) {
        List<DCResource> allDCResources = datacoreService.getAllResources(dcModelMapping.getProject(),
                dcModelMapping.getType());

        // Extract all possible columns from the type's model (filtering explicitely excluded ones)
        DCModel model = datacoreService.getModel(dcModelMapping.getProject(), dcModelMapping.getType());
        List<String> resourceKeys = model.getFields().stream()
                .filter(field -> dcModelMapping.getExcludedFields() == null || !dcModelMapping.getExcludedFields().contains(field.getName()))
                .map(DCModel.DcModelField::getName)
                .collect(Collectors.toList());

        if (!StringUtils.isEmpty(dcModelMapping.getAddressField())
                || !StringUtils.isEmpty(dcModelMapping.getPostalCodeField())
                || !StringUtils.isEmpty(dcModelMapping.getCityField())){
            resourceKeys.add("lon");
            resourceKeys.add("lat");
            geocodingClientService.addGeocodingToResource(allDCResources, dcModelMapping.getAddressField(),
                    dcModelMapping.getPostalCodeField(), dcModelMapping.getCityField());
        }
        // And add a fake column to allow for easier charting in CKAN
        resourceKeys.add("fake-weight");

        if (dcModelMapping.getPivotField() == null) {
            String csvResource = resourceTransformerService.resourcesToCsv(allDCResources, resourceKeys);
            ckanService.updateResourceData(dcModelMapping, Format.CSV, csvResource, Option.none(), Option.none());

            String jsonResource = resourceTransformerService.resourcesToJson(allDCResources, dcModelMapping.getExcludedFields());
            ckanService.updateResourceData(dcModelMapping, Format.JSON, jsonResource, Option.none(), Option.none());
        } else {
            Map<String, io.vavr.collection.List<DCResource>> resourcesByPivotValues =
                    io.vavr.collection.List.ofAll(allDCResources)
                            .groupBy(dcResource -> dcResource.getAsString(dcModelMapping.getPivotField()));
            LOGGER.debug("Got pivot values : {}", resourcesByPivotValues.keySet());
            resourcesByPivotValues.forEach((pivotValue, dcResources) -> {
                String ckanResourceIdKey = Format.CSV.name() + "-" + pivotValue;
                String ckanResourceName = dcModelMapping.getName() + " - " + pivotValue;
                if (dcModelMapping.getCkanResourceId().get(ckanResourceIdKey) == null) {
                    LOGGER.debug("Resource does not exist yet for {}, creating it", pivotValue);
                    Either<String, CkanResource> result = ckanService.createResource(dcModelMapping.getCkanPackageId(),
                            ckanResourceName, dcModelMapping.getDescription());
                    if (result.isLeft()) {
                        LOGGER.warn("Unable to create resource for {}", pivotValue);
                    } else {
                        dcModelMapping.getCkanResourceId().put(ckanResourceIdKey, result.right().get().getId());
                        dcModelMappingRepository.save(dcModelMapping);
                    }
                }

                if (dcModelMapping.getCkanResourceId().get(ckanResourceIdKey) != null) {
                    String ckanResourceId = dcModelMapping.getCkanResourceId().get(ckanResourceIdKey);
                    String csvResource = resourceTransformerService.resourcesToCsv(dcResources.asJava(), resourceKeys);
                    ckanService.updateResourceData(dcModelMapping, Format.CSV, csvResource,
                            Option.of(ckanResourceId), Option.of(ckanResourceName));
                }
            });
        }
    }
}
