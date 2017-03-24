package org.ozwillo.dcexporter.model.Ckan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CkanDataset {

    @SerializedName("license_title")
    @Expose private String licenseTitle;
    @Expose private String maintainer;
    @SerializedName("relationships_as_object")
    @Expose private List<Object> relationshipsAsObject;
    @SerializedName("private")
    @Expose private boolean _private;
    @SerializedName("maintainer_email")
    @Expose private String maintainerEmail;
    @SerializedName("num_tags")
    @Expose private int numTags;
    @Expose private String id;
    @SerializedName("metadata_created")
    @Expose private String metadataCreated;
    @SerializedName("metadata_modified")
    @Expose private String metadataModified;
    @Expose private String author;
    @SerializedName("author_email")
    @Expose private String authorEmail;
    @Expose private String state;
    @Expose private String version;
    @SerializedName("creator_user_id")
    @Expose private String creatorUserId;
    @Expose private String type;
    @Expose private List<CkanResource> resources;
    @SerializedName("num_resources")
    @Expose private int numResources;
    @Expose private List<CkanTag> tags;
    @Expose private List<CkanOrganization> groups;
    @SerializedName("license_id")
    @Expose private String licenseId;
    @SerializedName("relationships_as_subject")
    @Expose private List<Object> relationshipsAsSubject;
    @Expose private CkanOrganization organization;
    @Expose private String name;
    @SerializedName("isopen")
    @Expose private boolean open;
    @Expose private String url;
    @Expose private String notes;
    @SerializedName("owner_org")
    @Expose private String ownerOrg;
    @Expose private List<CkanExtra> extras;
    @SerializedName("license_url")
    @Expose private String licenseUrl;
    @Expose private String title;
    @SerializedName("revision_id")
    @Expose private String revisionId;


    public CkanDataset() {
    }

    public CkanDataset(String name) {
        this.name = name;
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
