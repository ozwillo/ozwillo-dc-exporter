package org.ozwillo.dcexporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

public class DcModelMapping {

    @Id
    private String id;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String dcId;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String name;

    public DcModelMapping() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDcId() {
        return dcId;
    }

    public void setDcId(String dcId) {
        this.dcId = dcId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DcModelMapping{" +
            "id='" + id + '\'' +
            ", dcId='" + dcId + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
