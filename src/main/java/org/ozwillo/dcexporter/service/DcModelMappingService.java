package org.ozwillo.dcexporter.service;

import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.ui.AuditLogWapper;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DcModelMappingService {

    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    public List<AuditLogWapper> getAllAuditLogWithModel() {
        List<AuditLogWapper> dcModelMappingListWithLog = new ArrayList<>();

        dcModelMappingRepository.findAll().forEach((dcModelMapping) -> {
            AuditLogWapper auditLogWapper = new AuditLogWapper();
            List<SynchronizerAuditLog> auditLogs =
                    synchronizerAuditLogRepository.findByTypeOrderByDateDesc(dcModelMapping.getType());
            auditLogWapper.setDcModelMapping(dcModelMapping);
            auditLogWapper.setSynchronizerAuditLog(auditLogs.get(0));
            dcModelMappingListWithLog.add(auditLogWapper);
        });
        Collections.reverse(dcModelMappingListWithLog);
        return dcModelMappingListWithLog;
    }
}
