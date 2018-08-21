package org.ozwillo.dcexporter.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.ozwillo.dcexporter.config.CustomDateTimeSerializer;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

public class SynchronizerAuditLog {

    @Id
    private String id;

    @NotNull
    @NotEmpty
    private String type;

    @NotNull
    @NotEmpty
    private SynchronizerStatus status;

    @NotNull
    @NotEmpty
    private String errorMessage;

    private int errorCount = 0;
    
    @NotNull
    @NotEmpty
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private DateTime date;

    public SynchronizerAuditLog() {
    }

    public SynchronizerAuditLog(String type, SynchronizerStatus status, String errorMessage, DateTime date) {
        this.type = type;
        this.status = status;
        this.errorMessage = errorMessage;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public SynchronizerStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public DateTime getDate() {
        return date;
    }

    public int getErrorCount() {
        return errorCount;
    }
    
    public void updateOnError (String message) {
        date = DateTime.now();
        errorCount++;
        errorMessage = message;
        status = SynchronizerStatus.FAILED;
    }

    @Override
    public String toString() {
        return "SynchronizerAuditLog{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", succeeded=" + status + '\'' +
            ", errorMessage='" + errorMessage + '\'' +
            ", errorCount='" + errorCount + '\'' +
            ", date=" + date +
            '}';
    }
}
