package org.ozwillo.dcexporter.model.Geocoding;

import java.util.List;

public class GeocodingResponse {
    private String query;
    private int limit;
    private String type;
    private List<GeocodingFeature> features = null;
    private String licence;
    private String version;
    private String attribution;

    public GeocodingResponse(){
    }

    public GeocodingResponse(String query, int limit, String type, List<GeocodingFeature> features, String licence, String version, String attribution) {
        this.query = query;
        this.limit = limit;
        this.type = type;
        this.features = features;
        this.licence = licence;
        this.version = version;
        this.attribution = attribution;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GeocodingFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeocodingFeature> features) {
        this.features = features;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }
}
