package org.ozwillo.dcexporter.model.Ckan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CkanTag {

    @Expose private String id;
    @Expose private String name;
    @SerializedName("vocabulary_id")
    @Expose private String vocabularyId;
    @SerializedName("display_name")
    @Expose private String displayName;


    public CkanTag() {
    }

    public CkanTag(String id, String name, String vocabularyId, String displayName) {
        this.id = id;
        this.name = name;
        this.vocabularyId = vocabularyId;
        this.displayName = displayName;
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
}
