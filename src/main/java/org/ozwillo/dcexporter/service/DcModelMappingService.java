package org.ozwillo.dcexporter.service;

import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.ui.AuditLogWapper;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DcModelMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcModelMappingService.class);

    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    public List<AuditLogWapper> getAllAuditLogWithModel() {
        List<AuditLogWapper> dcModelMappingListWithLog = new ArrayList<>();

        dcModelMappingRepository.findAll().forEach(dcModelMapping -> {
            List<SynchronizerAuditLog> auditLogs =
                    synchronizerAuditLogRepository.findByTypeOrderByDateDesc(dcModelMapping.getType());
            if (!auditLogs.isEmpty()) {
                AuditLogWapper auditLogWapper = new AuditLogWapper(dcModelMapping, auditLogs.get(0));
                dcModelMappingListWithLog.add(auditLogWapper);
            } else {
                LOGGER.info("Found a mapping without audit logs : {}", dcModelMapping.getName());
            }
        });
        Collections.reverse(dcModelMappingListWithLog);
        return dcModelMappingListWithLog;
    }
}
