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
    private DateTime date;

    public SynchronizerAuditLog() {
    }

    public SynchronizerAuditLog(String type, boolean succeeded, DateTime date) {
        this.type = type;
        this.succeeded = succeeded;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SynchronizerAuditLog{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", succeeded=" + succeeded + '\'' +
            ", date=" + date +
            '}';
    }
}
