package org.ozwillo.dcexporter.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
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
    private boolean succeeded;

    @NotNull
    @NotEmpty
    private String errorMessage;

    @NotNull
    @NotEmpty
    private DateTime date;

    public SynchronizerAuditLog() {
    }

    public SynchronizerAuditLog(String type, boolean succeeded, String errorMessage, DateTime date) {
        this.type = type;
        this.succeeded = succeeded;
        this.errorMessage = errorMessage;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public DateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "SynchronizerAuditLog{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", succeeded=" + succeeded + '\'' +
            ", errorMessage='" + errorMessage + '\'' +
            ", date=" + date +
            '}';
    }
}
