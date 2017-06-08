package org.ozwillo.dcexporter.service;

import org.ozwillo.dcexporter.model.Geocoding.GeocodingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.Optional;

@Service
public class GeocodingClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CkanClientService.class);

    public Optional<GeocodingResponse> getFeatures(String geocodingUrl, String address) {
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
