package org.ozwillo.dcexporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.trentorise.opendata.jackan.model.CkanDatasetBase;
import eu.trentorise.opendata.jackan.model.CkanTag;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    private String resourceName;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String name;

    private String description;

    private List<CkanTag> tags;

    private String license;

    private String source;

    private String version;

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

    public DcModelMapping(String dcId, String project, String type, String resourceName, String name, String description, List<CkanTag> tags, String license, String source, String version) {
        this.dcId = dcId;
        this.project = project;
        this.type = type;
        this.resourceName = resourceName;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.license = license;
        this.source = source;
        this.version = version;
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

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CkanTag> getTags() {
        return tags;
    }

    public void setTags(List<CkanTag> tags) {
        this.tags = tags;
    }

    public String getLicense() { return license; }

    public void setLicense(String license) { this.license = license; }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DcModelMapping{" +
                "dcId='" + dcId + '\'' +
                ", project='" + project + '\'' +
                ", type='" + type + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", license='" + license + '\'' +
                ", source='" + source + '\'' +
                ", version='" + version + '\'' +
                ", ckanPackageId='" + ckanPackageId + '\'' +
                ", ckanResourceId='" + ckanResourceId + '\'' +
                '}';
    }
}
