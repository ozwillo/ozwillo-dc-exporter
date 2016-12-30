package org.ozwillo.dcexporter.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document
public class SynchronizerAuditLog {

    @Id
    private String id;

    @NotNull
    @NotEmpty
    private String type;

    @NotNull
    @NotEmpty
    private Boolean sync;

    @NotNull
    @NotEmpty
    private DateTime date;

    public SynchronizerAuditLog() {
    }

    public SynchronizerAuditLog(String type,Boolean sync, DateTime date) {
        this.type = type;
        this.sync = sync;
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

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
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
                ", syncj=" + sync +
                ", date=" + date +
                '}';
    }
}
