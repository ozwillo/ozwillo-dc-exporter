package org.ozwillo.dcexporter.model.ui;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;

import javax.validation.constraints.NotNull;

public class AuditLogWapper implements Comparable<AuditLogWapper>{

    @JsonProperty
    @NotNull
    @NotEmpty
    DcModelMapping dcModelMapping;

    @JsonProperty
    @NotNull
    @NotEmpty
    SynchronizerAuditLog synchronizerAuditLog;

    public AuditLogWapper(DcModelMapping dcModelMapping, SynchronizerAuditLog synchronizerAuditLog) {
        this.dcModelMapping = dcModelMapping;
        this.synchronizerAuditLog = synchronizerAuditLog;
    }

    public DcModelMapping getDcModelMapping() {
        return dcModelMapping;
    }

    public SynchronizerAuditLog getSynchronizerAuditLog() {
        return synchronizerAuditLog;
    }

    @Override
    public int compareTo(AuditLogWapper auditLogWapper) {
        if (this.synchronizerAuditLog.getDate() == null || auditLogWapper.synchronizerAuditLog.getDate() == null)
            return 0;
        return this.synchronizerAuditLog.getDate().compareTo(auditLogWapper.synchronizerAuditLog.getDate());
    }
}
