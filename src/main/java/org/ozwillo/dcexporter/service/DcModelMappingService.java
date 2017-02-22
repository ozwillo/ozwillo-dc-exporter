package org.ozwillo.dcexporter.service;

import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.ui.AuditLogWapper;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DcModelMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcModelMappingService.class);

    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    public List<AuditLogWapper> getAllAuditLogWithModel() {
        return dcModelMappingRepository.findAllByOrderByNameAsc().stream().map(dcModelMapping -> {
            List<SynchronizerAuditLog> auditLogs =
                    synchronizerAuditLogRepository.findByTypeOrderByDateDesc(dcModelMapping.getType());
            if (!auditLogs.isEmpty()) {
                return new AuditLogWapper(dcModelMapping, auditLogs.get(0));
            } else {
                return new AuditLogWapper(dcModelMapping, null);
            }
        }).collect(Collectors.toList());
    }
}
