package org.ozwillo.dcexporter.dao;

import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynchronizerAuditLogRepository extends MongoRepository<SynchronizerAuditLog, String> {

    SynchronizerAuditLog findFirstByTypeOrderByDateDesc(String type);
}
