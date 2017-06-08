package org.ozwillo.dcexporter.model.Geocoding;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GeocodingFeature {
    @JsonIgnore
    private GeocodingProperties properties;
    private GeocodingGeometry geometry;
    private String type;

    public GeocodingProperties getProperties() {
        return properties;
    }

    public void setProperties(GeocodingProperties properties) {
        this.properties = properties;
    }

    public GeocodingGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeocodingGeometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
