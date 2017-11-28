package org.ozwillo.dcexporter.model.Ckan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CkanTag {

    private String id;
    private String name;
    @JsonProperty("vocabulary_id")
    private String vocabularyId;
    @JsonProperty("display_name")
    private String displayName;
    private String state;


    public CkanTag() {
    }

    public CkanTag(String id, String name, String vocabularyId, String displayName, String state) {
        this.id = id;
        this.name = name;
        this.vocabularyId = vocabularyId;
        this.displayName = displayName;
        this.state = state;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVocabularyId() {
        return vocabularyId;
    }
    public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}
