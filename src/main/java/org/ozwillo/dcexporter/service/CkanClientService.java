package org.ozwillo.dcexporter.service;

import okhttp3.*;
import org.ozwillo.dcexporter.model.Ckan.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CkanClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CkanClientService.class);

    /* Datasets */

    public Optional<List<String>> getDatasetList(String ckanUrl) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<StringListResponse> call = ckanAPI.getDatasets();

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to fetch datasets from CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<CkanDataset> getDataset(String ckanUrl, String idOrName) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<DatasetResponse> call = ckanAPI.getDataset(idOrName);

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to fetch datasets from CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<CkanDataset> createDataset(String ckanUrl, String ckanApiKey, CkanDataset ckanDataset) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();
        if(!isValid(ckanApiKey, "CKAN API key")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<DatasetResponse> call = ckanAPI.createDataset(ckanApiKey, ckanDataset, "application/json");

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to create dataset to CKAN: {}", e);
            return Optional.empty();
        }
    }


    /* Resources */

    public Optional<CkanResource> getResource(String ckanUrl, String id) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<ResourceResponse> call = ckanAPI.getResource(id);

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to get resource from CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<CkanResource> createResource(String ckanUrl, String ckanApiKey, CkanResource ckanResource) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();
        if(!isValid(ckanApiKey, "CKAN API key")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<ResourceResponse> call = ckanAPI.createResource(ckanApiKey, ckanResource);

        try {
            CkanResource newResource = call.execute().body().result;
            ckanResource.setId(newResource.getId());
            Optional<CkanResource> opt = updateResource(ckanUrl, ckanApiKey, ckanResource);
            if(opt.isPresent()) return Optional.ofNullable(opt.get());
            LOGGER.error("Error while trying to upload resource to CKAN");
            return Optional.empty();
        } catch (IOException e) {
            LOGGER.error("Error while trying to create resource to CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<CkanResource> updateResource(String ckanUrl, String ckanApiKey, CkanResource ckanResource) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();
        if(!isValid(ckanApiKey, "CKAN API key")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<ResourceResponse> call = ckanAPI.updateResource(ckanApiKey, ckanResource);

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to update resource to CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<CkanResource> updateResourceFile(String ckanUrl, String ckanApiKey, CkanResource ckanResource) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();
        if(!isValid(ckanApiKey, "CKAN API key")) return Optional.empty();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", ckanResource.getId())
                .addFormDataPart("size", ckanResource.getSize())
                .addFormDataPart("url", ckanResource.getUrl())
                .addFormDataPart("package_id", ckanResource.getPackageId())
                .addFormDataPart("format", ckanResource.getFormat())
                .addFormDataPart("mimetype", ckanResource.getMimetype())
                .addFormDataPart("last_modified", ckanResource.getLastModified())
                .addFormDataPart("upload", ckanResource.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream; charset=utf-8"), ckanResource.getUpload()))
                .build();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<ResourceResponse> call = ckanAPI.updateResourceFile(ckanApiKey, requestBody);

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to update file resource to CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<CkanResponse> deleteResource(String ckanUrl, String ckanApiKey, String idResource) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();
        if(!isValid(ckanApiKey, "CKAN API key")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Map<String, String> id = new HashMap<String, String>();
        id.put("id", idResource);
        Call<CkanResponse> call = ckanAPI.deleteResource(ckanApiKey, id);

        try {
            return Optional.ofNullable(call.execute().body());
        } catch (IOException e) {
            LOGGER.error("Error while trying to delete resource to CKAN: {}", e);
            return Optional.empty();
        }
    }


    /* Others */

    public Optional<List<CkanLicense>> getLicenseList(String ckanUrl) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<LicenseListResponse> call = ckanAPI.getLicences();

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to fetch licenses from CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<List<CkanTag>> getTagList(String ckanUrl) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<TagListResponse> call = ckanAPI.getTags(true);

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to fetch tags from CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<CkanOrganization> getOrganization(String ckanUrl, String idOrName) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<OrganizationResponse> call = ckanAPI.getOrganization(idOrName, false);

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to fetch group from CKAN: {}", e);
            return Optional.empty();
        }
    }

    public Optional<List<CkanOrganization>> getOrganizationList(String ckanUrl) {
        if(!isValid(ckanUrl, "CKAN URL")) return Optional.empty();

        CkanAPI ckanAPI = getCkanAPI(ckanUrl);
        Call<OrganizationListResponse> call = ckanAPI.getOrganizations(true);

        try {
            return Optional.ofNullable(call.execute().body().result);
        } catch (IOException e) {
            LOGGER.error("Error while trying to fetch organizations from CKAN: {}", e);
            return Optional.empty();
        }
    }

    private CkanAPI getCkanAPI(String ckanUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ckanUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(CkanAPI.class);
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


interface CkanAPI {

    /* Datasets */

    @GET("/api/3/action/package_list")
    Call<StringListResponse> getDatasets();

    @GET("/api/3/action/package_show")
    Call<DatasetResponse> getDataset(@Query("id") String idOrName);

    @POST("/api/3/action/package_create")
    Call<DatasetResponse> createDataset(@Header("Authorization") String ckanApiKey, @Body CkanDataset ckanDataset, @Header("Content-Type") String content_type);


    /* Resources */

    @GET("/api/3/action/resource_show")
    Call<ResourceResponse> getResource(@Query("id") String id);

    @POST("/api/3/action/resource_create")
    Call<ResourceResponse> createResource(@Header("Authorization") String ckanApiKey, @Body CkanResource ckanResource);

    @POST("/api/3/action/resource_update")
    Call<ResourceResponse> updateResource(@Header("Authorization") String ckanApiKey, @Body CkanResource ckanResource);

    @POST("/api/3/action/resource_update")
    Call<ResourceResponse> updateResourceFile(@Header("Authorization") String ckanApiKey, @Body RequestBody ckanResource);

    @POST("/api/3/action/resource_delete")
    Call<CkanResponse> deleteResource(@Header("Authorization") String ckanApiKey, @Body Object id);


    /* Others */

    @GET("/api/3/action/organization_show")
    Call<OrganizationResponse> getOrganization(@Query("id") String idOrName, @Query("include_datasets") boolean includeDatasets);

    @GET("/api/3/action/license_list")
    Call<LicenseListResponse> getLicences();

    @GET("/api/3/action/tag_list")
    Call<TagListResponse> getTags(@Query("all_fields") boolean all_fields);

    @GET("/api/3/action/organization_list")
    Call<OrganizationListResponse> getOrganizations(@Query("all_fields") boolean all_fields);

}


class TagListResponse extends CkanResponse {
    public List<CkanTag> result;
}
class StringListResponse extends CkanResponse {
    public List<String> result;
}
class LicenseListResponse extends CkanResponse {
    public List<CkanLicense> result;
}
class DatasetResponse extends CkanResponse {
    public CkanDataset result;
}
class ResourceResponse extends CkanResponse {
    public CkanResource result;
}
class OrganizationResponse extends CkanResponse {
    public CkanOrganization result;
}
class OrganizationListResponse extends CkanResponse {
    public List<CkanOrganization> result;
}