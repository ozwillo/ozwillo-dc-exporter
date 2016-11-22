package org.ozwillo.dcexporter.service;

import org.joda.time.DateTime;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
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
                List<SynchronizerAuditLog> auditLogs =
                        synchronizerAuditLogRepository.findByTypeOrderByDateDesc(dcModelMapping.getType());

                if (!auditLogs.isEmpty() &&
                        !datacoreService.hasMoreRecentResources(dcModelMapping.getProject(), dcModelMapping.getType(), auditLogs.get(0).getDate())) {
                    LOGGER.info("No more recent resources for {}, returning", dcModelMapping.getType());
                    return;
                }

                LOGGER.info("Got some recent data for {}, synchronizing them", dcModelMapping.getType());
                this.sync(dcModelMapping);

                SynchronizerAuditLog newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), DateTime.now());
                synchronizerAuditLogRepository.save(newAuditLog);
            });
        });
    }

    public String sync(DcModelMapping dcModelMapping) {
        Optional<String> optionalResourceCsvFile =
                datacoreService.exportResourceToCsv(dcModelMapping.getProject(), dcModelMapping.getType());

        if (!optionalResourceCsvFile.isPresent()) {
            LOGGER.error("Did not get the resource's CSV file, stopping");
            return "KO";
        }

        ckanService.updateResourceData(dcModelMapping.getCkanPackageId(), dcModelMapping.getCkanResourceId(), optionalResourceCsvFile);

        return "OK";
    }
}
