package org.ozwillo.dcexporter.model.ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;

import javax.validation.constraints.NotNull;

public class AuditLogWapper {

    @JsonProperty
    @NotNull
    @NotEmpty
    private DcModelMapping dcModelMapping;

    @JsonProperty
    @NotNull
    @NotEmpty
    private SynchronizerAuditLog synchronizerAuditLog;

    @JsonProperty
    private String datasetUrl;
    @JsonProperty
    private String orgaName;

    public AuditLogWapper(DcModelMapping dcModelMapping, SynchronizerAuditLog synchronizerAuditLog, String datasetUrl) {
        this.dcModelMapping = dcModelMapping;
        this.synchronizerAuditLog = synchronizerAuditLog;
        this.datasetUrl = datasetUrl;
    }

    public AuditLogWapper(DcModelMapping dcModelMapping, SynchronizerAuditLog synchronizerAuditLog, String datasetUrl, String orgaName) {
        this.dcModelMapping = dcModelMapping;
        this.synchronizerAuditLog = synchronizerAuditLog;
        this.datasetUrl = datasetUrl;
        this.orgaName = orgaName;
    }

    public DcModelMapping getDcModelMapping() {
        return dcModelMapping;
    }

    public SynchronizerAuditLog getSynchronizerAuditLog() {
        return synchronizerAuditLog;
    }
}
