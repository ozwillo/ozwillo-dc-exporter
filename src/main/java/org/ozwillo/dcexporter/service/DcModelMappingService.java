package org.ozwillo.dcexporter.service;


import org.jvnet.hk2.annotations.Service;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DcModelMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizerService.class);

    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    public List<DcModelMapping> getAllAuditLogWithModel() {
        List<DcModelMapping> dcModelMappingListWithLog = new ArrayList<DcModelMapping>();

        dcModelMappingRepository.findAll().forEach((dcModelMapping) -> {
            List<SynchronizerAuditLog> auditLogs =
                    synchronizerAuditLogRepository.findByTypeOrderByDateDesc(dcModelMapping.getType());

            dcModelMapping.setSynchronizerAuditLog(auditLogs.get(0));
            dcModelMappingListWithLog.add(dcModelMapping);
        });
        return dcModelMappingListWithLog;
    }
}
