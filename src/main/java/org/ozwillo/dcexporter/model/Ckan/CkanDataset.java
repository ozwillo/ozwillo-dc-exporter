package org.ozwillo.dcexporter.model.Ckan;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CkanDataset {

    @JsonProperty("license_title")
    private String licenseTitle;
    private String maintainer;
    @JsonProperty("relationships_as_object")
    private List<Object> relationshipsAsObject;
    @JsonProperty("private")
    private boolean _private;
    @JsonProperty("maintainer_email")
    private String maintainerEmail;
    @JsonProperty("num_tags")
    private int numTags;
    private String id;
    @JsonProperty("metadata_created")
    private String metadataCreated;
    @JsonProperty("metadata_modified")
    private String metadataModified;
    private String author;
    @JsonProperty("author_email")
    private String authorEmail;
    private String state;
    private String version;
    @JsonProperty("creator_user_id")
    private String creatorUserId;
    private String type;
    private List<CkanResource> resources;
    @JsonProperty("num_resources")
    private int numResources;
    private List<CkanTag> tags;
    private List<CkanOrganization> groups;
    @JsonProperty("license_id")
    private String licenseId;
    @JsonProperty("relationships_as_subject")
    private List<Object> relationshipsAsSubject;
    private CkanOrganization organization;
    private String name;
    @JsonProperty("isopen")
    private boolean open;
    private String url;
    private String notes;
    @JsonProperty("owner_org")
    private String ownerOrg;
    private List<CkanExtra> extras;
    @JsonProperty("license_url")
    private String licenseUrl;
    private String title;
    @JsonProperty("revision_id")
    private String revisionId;


    public CkanDataset() {
    }

    public CkanDataset(String name) {
        this.name = name;
        this.resources = new ArrayList<>();
        this.relationshipsAsObject = new ArrayList<>();
        this.relationshipsAsSubject = new ArrayList<>();
        this.extras = new ArrayList<>();
    }

    public CkanDataset(String licenseTitle, String maintainer, List<Object> relationshipsAsObject, boolean _private, String maintainerEmail, int numTags, String id, String metadataCreated, String metadataModified, String author, String authorEmail, String state, String version, String creatorUserId, String type, List<CkanResource> resources, int numResources, List<CkanTag> tags, List<CkanOrganization> groups, String licenseId, List<Object> relationshipsAsSubject, CkanOrganization organization, String name, boolean open, String url, String notes, String ownerOrg, List<CkanExtra> extras, String licenseUrl, String title, String revisionId) {
        this.licenseTitle = licenseTitle;
        this.maintainer = maintainer;
        this.relationshipsAsObject = relationshipsAsObject;
        this._private = _private;
        this.maintainerEmail = maintainerEmail;
        this.numTags = numTags;
        this.id = id;
        this.metadataCreated = metadataCreated;
        this.metadataModified = metadataModified;
        this.author = author;
        this.authorEmail = authorEmail;
        this.authorEmail = authorEmail;
        this.state = state;
        this.version = version;
        this.creatorUserId = creatorUserId;
        this.type = type;
        this.resources = resources;
        this.numResources = numResources;
        this.tags = tags;
        this.groups = groups;
        this.licenseId = licenseId;
        this.relationshipsAsSubject = relationshipsAsSubject;
        this.organization = organization;
        this.name = name;
        this.open = open;
        this.url = url;
        this.notes = notes;
        this.ownerOrg = ownerOrg;
        this.extras = extras;
        this.licenseUrl = licenseUrl;
        this.title = title;
        this.revisionId = revisionId;
    }


    public String getLicenseTitle() {
        return licenseTitle;
    }
    public void setLicenseTitle(String licenseTitle) {
        this.licenseTitle = licenseTitle;
    }
    public String getMaintainer() {
        return maintainer;
    }
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }
    public List<Object> getRelationshipsAsObject() {
        return relationshipsAsObject;
    }
    public void setRelationshipsAsObject(List<Object> relationshipsAsObject) {
        this.relationshipsAsObject = relationshipsAsObject;
    }
    public boolean getPrivate() {
        return _private;
    }
    public void setPrivate(boolean _private) {
        this._private = _private;
    }
    public String getMaintainerEmail() {
        return maintainerEmail;
    }
    public void setMaintainerEmail(String maintainerEmail) {
        this.maintainerEmail = maintainerEmail;
    }
    public int getNumTags() {
        return numTags;
    }
    public void setNumTags(int numTags) {
        this.numTags = numTags;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMetadataCreated() {
        return metadataCreated;
    }
    public void setMetadataCreated(String metadataCreated) {
        this.metadataCreated = metadataCreated;
    }
    public String getMetadataModified() {
        return metadataModified;
    }
    public void setMetadataModified(String metadataModified) {
        this.metadataModified = metadataModified;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthorEmail() {
        return authorEmail;
    }
    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getCreatorUserId() {
        return creatorUserId;
    }
    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<CkanResource> getResources() {
        return resources;
    }
    public void setResources(List<CkanResource> resources) {
        this.resources = resources;
    }
    public int getNumResources() {
        return numResources;
    }
    public void setNumResources(int numResources) {
        this.numResources = numResources;
    }
    public List<CkanTag> getTags() {
        return tags;
    }
    public void setTags(List<CkanTag> tags) {
        this.tags = tags;
    }
    public List<CkanOrganization> getGroups() {
        return groups;
    }
    public void setGroups(List<CkanOrganization> groups) {
        this.groups = groups;
    }
    public String getLicenseId() {
        return licenseId;
    }
    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }
    public List<Object> getRelationshipsAsSubject() {
        return relationshipsAsSubject;
    }
    public void setRelationshipsAsSubject(List<Object> relationshipsAsSubject) {
        this.relationshipsAsSubject = relationshipsAsSubject;
    }
    public CkanOrganization getOrganization() {
        return organization;
    }
    public void setOrganization(CkanOrganization organization) {
        this.organization = organization;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isOpen() {
        return open;
    }
    public void setOpen(boolean open) {
        this.open = open;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getOwnerOrg() {
        return ownerOrg;
    }
    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
    }
    public List<CkanExtra> getExtras() {
        return extras;
    }
    public void setExtras(List<CkanExtra> extras) {
        this.extras = extras;
    }
    public String getLicenseUrl() {
        return licenseUrl;
    }
    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getRevisionId() {
        return revisionId;
    }
    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

}
