package org.ozwillo.dcexporter.service;

import org.oasis_eu.spring.datacore.model.DCResource;
import org.ozwillo.dcexporter.model.Geocoding.GeocodingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.ozwillo.dcexporter.util.DCResourceUtils.getI18nFieldValueFromList;

@Service
public class GeocodingClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CkanClientService.class);

    @Value("${geocoding.url}")
    private String geocodingUrl;

    public void addGeocodingToResource(List<DCResource> intermediateResult, String addressField, String postalCodeField, String cityField) {
        intermediateResult.forEach(resource -> {
            String address = "";
            if(!StringUtils.isEmpty(addressField) && resource.getValues().get(addressField) != null) {
                if (resource.getValues().get(addressField).isString()) address += resource.getAsString(addressField).concat(",");
            }
            if(!StringUtils.isEmpty(cityField) && resource.getValues().get(cityField) != null) {
                DCResource.Value resourceValue = resource.getValues().get(cityField);
                if(resourceValue.isString()) address += resource.getAsString(cityField).concat(",");
                if(resourceValue.isArray()) {
                    List<DCResource.Value> resourceRowInnerValues = resourceValue.asArray();
                    if (resourceRowInnerValues.get(0).isString()) {
                        Optional<String> reducedValue =
                                resource.getAsStringList(cityField).stream().reduce((result, value) -> result + "," + value);
                        address += reducedValue.get().concat(",");
                    } else if (resourceRowInnerValues.get(0).isMap()) {
                        address += getI18nFieldValueFromList(resourceRowInnerValues, "fr").concat(",");
                    }
                }
            }
            if(!StringUtils.isEmpty(postalCodeField) && resource.getValues().get(postalCodeField) != null) {
                if(resource.getValues().get(postalCodeField).isString()) address += resource.getAsString(postalCodeField);
            }

            if(!StringUtils.isEmpty(address)){
                Optional<GeocodingResponse> bnaResponse = getFeatures(geocodingUrl, address);
                if (bnaResponse.isPresent()) {
                    LOGGER.debug("Address {} geocoding in coordinates {}", address, bnaResponse.get().getFeatures().get(0).getGeometry().getCoordinates());
                    resource.set("lon", bnaResponse.get().getFeatures().get(0).getGeometry().getCoordinates().get(0).toString());
                    resource.set("lat", bnaResponse.get().getFeatures().get(0).getGeometry().getCoordinates().get(1).toString());
                } else {
                    LOGGER.info("No geocoding results for this address {}", address);
                }
            } else {
                LOGGER.debug("Address field it's empty or null");
            }
        });
    }

    private Optional<GeocodingResponse> getFeatures(String geocodingUrl, String address) {
        if(!isValid(geocodingUrl, "Geocoding URL")) return Optional.empty();

        GeocodingAPI ckanAPI = getGeocodingAPI(geocodingUrl);
        Call<GeocodingResponse> call = ckanAPI.getFeatures(address);

        try {
            GeocodingResponse response = call.execute().body();
            if (response.getFeatures().isEmpty()){
                LOGGER.debug("Features is empty for this address : {}", address);
                return Optional.empty();
            }
            return Optional.ofNullable(response);
        } catch (IOException e) {
            LOGGER.error("Error while trying to get geocoding : {}", e);
            return Optional.empty();
        }
    }

    private GeocodingAPI getGeocodingAPI(String geocodingUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(geocodingUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(GeocodingAPI.class);
    }

    private boolean isValid(String str, String strName) {
        if(str == null) {
            LOGGER.error(strName + " is null");
            return false;
        }
        if(str.equals("")) LOGGER.warn(strName + " is empty");
        return true;
    }
}

interface GeocodingAPI {
    @GET("/search")
    Call<GeocodingResponse> getFeatures(@Query("q") String address);
}
