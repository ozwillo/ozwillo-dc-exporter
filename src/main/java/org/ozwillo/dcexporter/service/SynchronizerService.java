package org.ozwillo.dcexporter.service;

import javaslang.control.Either;
import org.joda.time.DateTime;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.Ckan.CkanDataset;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SynchronizerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizerService.class);

    @Autowired
    private DatacoreService datacoreService;

    @Autowired
    private CkanService ckanService;

    @Autowired
    private SystemUserService systemUserService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Scheduled(fixedDelayString = "${application.syncDelay}")
    public void synchronizeOrgs() {
        systemUserService.runAs(() -> {

            dcModelMappingRepository.findAll().forEach(dcModelMapping -> {
                SynchronizerAuditLog auditLog =
                        synchronizerAuditLogRepository.findFirstByTypeOrderByDateDesc(dcModelMapping.getType());

                if ((auditLog != null && auditLog.isSucceeded()) &&
                        !datacoreService.hasMoreRecentResources(dcModelMapping.getProject(), dcModelMapping.getType(), auditLog.getDate())) {
                    LOGGER.info("No more recent resources for {}, returning", dcModelMapping.getType());
                    return;
                }

                LOGGER.info("Got some recent data for {}, synchronizing them", dcModelMapping.getType());

                SynchronizerAuditLog newAuditLog;
                try {
                    this.sync(dcModelMapping);
                    newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), true, null,  DateTime.now());
                    synchronizerAuditLogRepository.save(newAuditLog);
                } catch (Exception exception) {
                    newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), false, exception.getMessage(),  DateTime.now());
                    synchronizerAuditLogRepository.save(newAuditLog);
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

    private void sync(DcModelMapping dcModelMapping) throws Exception {
        Map<String, Optional<String>> optionalResourceFiles =
                datacoreService.exportResource(dcModelMapping.getProject(), dcModelMapping.getType(), dcModelMapping.getExcludedFields());

        if (!optionalResourceFiles.get("csv").isPresent()) {
            LOGGER.error("Did not get the resource's CSV file, stopping");
            throw new Exception("Unable to get the resource from datacore");
        }

        if (!optionalResourceFiles.get("json").isPresent()) {
            LOGGER.error("Did not get the resource's JSON file, stopping");
            throw new Exception("Unable to get the resource from datacore");
        }

        ckanService.updateResourceData(dcModelMapping, optionalResourceFiles);
    }
}
