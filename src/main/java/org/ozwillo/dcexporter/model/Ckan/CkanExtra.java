package org.ozwillo.dcexporter.model.Ckan;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CkanExtra {

    private String id;
    private String key;
    private String value;
    private String state;
    @JsonProperty("revision_id")
    private String revisionId;
    @JsonProperty("group_id")
    private String groupId;


    public CkanExtra() {
    }

    public CkanExtra(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public CkanExtra(String id, String key, String value, String state, String revisionId, String groupId) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.state = state;
        this.revisionId = revisionId;
        this.groupId = groupId;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getRevisionId() {
        return revisionId;
    }
    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
