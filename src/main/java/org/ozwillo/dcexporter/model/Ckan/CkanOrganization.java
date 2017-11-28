package org.ozwillo.dcexporter.model.Ckan;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CkanOrganization {

    private List<Object> users = null;
    @JsonProperty("display_name")
    private String displayName;
    private String description;
    @JsonProperty("image_display_url")
    private String imageDisplayUrl;
    @JsonProperty("package_count")
    private int packageCount;
    private String created;
    private String name;
    @JsonProperty("is_organization")
    private boolean isOrganization;
    private String state;
    private List<CkanExtra> extras = null;
    @JsonProperty("image_url")
    private String imageUrl;
    private List<CkanOrganization> groups = null;
    private String type;
    private String title;
    @JsonProperty("revision_id")
    private String revisionId;
    @JsonProperty("num_followers")
    private int numFollowers;
    private String id;
    private List<CkanTag> tags = null;
    @JsonProperty("approval_status")
    private String approvalStatus;


    public CkanOrganization() {
    }

    public CkanOrganization(List<Object> users, String displayName, String description, String imageDisplayUrl, int packageCount, String created, String name, boolean isOrganization, String state, List<CkanExtra> extras, String imageUrl, List<CkanOrganization> groups, String type, String title, String revisionId, int numFollowers, String id, List<CkanTag> tags, String approvalStatus) {
        super();
        this.users = users;
        this.displayName = displayName;
        this.description = description;
        this.imageDisplayUrl = imageDisplayUrl;
        this.packageCount = packageCount;
        this.created = created;
        this.name = name;
        this.isOrganization = isOrganization;
        this.state = state;
        this.extras = extras;
        this.imageUrl = imageUrl;
        this.groups = groups;
        this.type = type;
        this.title = title;
        this.revisionId = revisionId;
        this.numFollowers = numFollowers;
        this.id = id;
        this.tags = tags;
        this.approvalStatus = approvalStatus;
    }


    public List<Object> getUsers() {
        return users;
    }
    public void setUsers(List<Object> users) {
        this.users = users;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageDisplayUrl() {
        return imageDisplayUrl;
    }
    public void setImageDisplayUrl(String imageDisplayUrl) {
        this.imageDisplayUrl = imageDisplayUrl;
    }
    public int getPackageCount() {
        return packageCount;
    }
    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }
    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isIsOrganization() {
        return isOrganization;
    }
    public void setIsOrganization(boolean isOrganization) {
        this.isOrganization = isOrganization;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public List<CkanExtra> getExtras() {
        return extras;
    }
    public void setExtras(List<CkanExtra> extras) {
        this.extras = extras;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public List<CkanOrganization> getGroups() {
        return groups;
    }
    public void setGroups(List<CkanOrganization> groups) {
        this.groups = groups;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
    public int getNumFollowers() {
        return numFollowers;
    }
    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<CkanTag> getTags() {
        return tags;
    }
    public void setTags(List<CkanTag> tags) {
        this.tags = tags;
    }
    public String getApprovalStatus() {
        return approvalStatus;
    }
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

}

