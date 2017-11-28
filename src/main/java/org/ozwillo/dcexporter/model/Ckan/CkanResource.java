package org.ozwillo.dcexporter.model.Ckan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CkanResource {

    @JsonProperty("cache_last_updated")
    private String cacheLastUpdated;
    @JsonProperty("package_id")
    private String packageId;
    @JsonProperty("webstore_last_updated")
    private String webstoreLastUpdated;
    @JsonProperty("datastore_active")
    private boolean datastoreActive;
    private String id;
    private String size;
    private String state;
    private String hash;
    private String description;
    private String format;
    @JsonProperty("last_modified")
    private String lastModified;
    @JsonProperty("url_type")
    private String urlType;
    private String mimetype;
    @JsonProperty("cache_url")
    private String cacheUrl;
    private String name;
    private String created;
    private String url;
    @JsonProperty("webstore_url")
    private String webstoreUrl;
    @JsonProperty("mimetype_inner")
    private String mimetypeInner;
    private int position;
    @JsonProperty("revision_id")
    private String revisionId;
    @JsonProperty("resource_type")
    private String resourceType;
    @JsonProperty("openspending_hint")
    private String openspendingHint;
    @JsonIgnore
    private byte[] upload;


    public CkanResource() {
    }

    public CkanResource(String cacheLastUpdated, String packageId, String webstoreLastUpdated, boolean datastoreActive, String id, String size, String state, String hash, String description, String format, String lastModified, String urlType, String mimetype, String cacheUrl, String name, String created, String url, String webstoreUrl, String mimetypeInner, int position, String revisionId, String resourceType, String openspendingHint, byte[] upload) {
        this.cacheLastUpdated = cacheLastUpdated;
        this.packageId = packageId;
        this.webstoreLastUpdated = webstoreLastUpdated;
        this.datastoreActive = datastoreActive;
        this.id = id;
        this.size = size;
        this.state = state;
        this.hash = hash;
        this.description = description;
        this.format = format;
        this.lastModified = lastModified;
        this.urlType = urlType;
        this.mimetype = mimetype;
        this.cacheUrl = cacheUrl;
        this.name = name;
        this.created = created;
        this.url = url;
        this.webstoreUrl = webstoreUrl;
        this.mimetypeInner = mimetypeInner;
        this.position = position;
        this.revisionId = revisionId;
        this.resourceType = resourceType;
        this.openspendingHint = openspendingHint;
        this.upload = upload;
    }


    public String getCacheLastUpdated() {
        return cacheLastUpdated;
    }
    public void setCacheLastUpdated(String cacheLastUpdated) {
        this.cacheLastUpdated = cacheLastUpdated;
    }
    public String getPackageId() {
        return packageId;
    }
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    public String getWebstoreLastUpdated() {
        return webstoreLastUpdated;
    }
    public void setWebstoreLastUpdated(String webstoreLastUpdated) {
        this.webstoreLastUpdated = webstoreLastUpdated;
    }
    public boolean isDatastoreActive() {
        return datastoreActive;
    }
    public void setDatastoreActive(boolean datastoreActive) {
        this.datastoreActive = datastoreActive;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    public String getLastModified() {
        return lastModified;
    }
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
    public String getUrlType() {
        return urlType;
    }
    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }
    public String getMimetype() {
        return mimetype;
    }
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
    public String getCacheUrl() {
        return cacheUrl;
    }
    public void setCacheUrl(String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getWebstoreUrl() {
        return webstoreUrl;
    }
    public void setWebstoreUrl(String webstoreUrl) {
        this.webstoreUrl = webstoreUrl;
    }
    public String getMimetypeInner() {
        return mimetypeInner;
    }
    public void setMimetypeInner(String mimetypeInner) {
        this.mimetypeInner = mimetypeInner;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public String getRevisionId() {
        return revisionId;
    }
    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }
    public String getResourceType() {
        return resourceType;
    }
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    public String getOpenspendingHint() {
        return openspendingHint;
    }
    public void setOpenspendingHint(String openspendingHint) {
        this.openspendingHint = openspendingHint;
    }

    /*public File getUpload() {
        return upload;
    }
    public void setUpload(@Nullable File upload) {
        this.setUpload(upload, false);
    }
    public void setUpload(@Nullable File upload, boolean guessMimeTypeAndFormat) {
        if (upload == null) {
            this.upload = null;
            this.size = null;
        } else {
            this.upload = upload;
            this.size = String.valueOf(upload.length());
            if (guessMimeTypeAndFormat) {
                try (InputStream is = new FileInputStream(upload);
                    BufferedInputStream bis = new BufferedInputStream(is);) {
                    this.uploadByte = Files.toByteArray(upload);
                    AutoDetectParser parser = new AutoDetectParser();
                    Metadata md = new Metadata();
                    md.add(Metadata.RESOURCE_NAME_KEY, upload.getName());
                    MediaType mediaType = parser.getDetector().detect(bis, md);
                    this.mimetype = mediaType.getBaseType(  ).toString();
                    this.format = mediaType.getSubtype().toUpperCase();
                } catch (FileNotFoundException e) {
                    // TODO : something ?
                } catch (IOException e) {
                    // TODO : something ?
                }
            }
        }
    }*/
    public byte[] getUpload() {
        return upload;
    }
    public void setUpload(byte[] upload) {
        if (upload == null) {
            this.upload = null;
            this.size = null;
        } else {
            this.upload = upload;
            this.size = String.valueOf(upload.length);
        }
    }
}
