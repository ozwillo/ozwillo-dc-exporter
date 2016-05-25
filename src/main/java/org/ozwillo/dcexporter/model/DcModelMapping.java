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

    /**
     * aka pointOfViewAbsoluteName in model definition
     */
    @NotNull
    @NotEmpty
    private String project;

    /**
     * Could be retrieved from dcId but it makes it clearer
     */
    @NotNull
    @NotEmpty
    private String type;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String name;

    private String ckanPackageId;

    private String ckanResourceId;

    public DcModelMapping() {
    }

    public DcModelMapping(String dcId, String project, String type, String name) {
        this.dcId = dcId;
        this.project = project;
        this.type = type;
        this.name = name;
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

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCkanPackageId() {
        return ckanPackageId;
    }

    public void setCkanPackageId(String ckanPackageId) {
        this.ckanPackageId = ckanPackageId;
    }

    public String getCkanResourceId() {
        return ckanResourceId;
    }

    public void setCkanResourceId(String ckanResourceId) {
        this.ckanResourceId = ckanResourceId;
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
