package org.ozwillo.dcexporter.dao;

import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SynchronizerAuditLogRepository extends MongoRepository<SynchronizerAuditLog, String> {

    List<SynchronizerAuditLog> findByTypeOrderByDateDesc(String type);
}
