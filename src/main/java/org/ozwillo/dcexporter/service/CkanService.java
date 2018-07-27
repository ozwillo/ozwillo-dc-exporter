package org.ozwillo.dcexporter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javaslang.control.Either;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.ozwillo.dcexporter.model.*;
import org.ozwillo.dcexporter.model.Ckan.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CkanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CkanService.class);

    @Autowired
    private CkanClientService ckanClientService;

    @Value("${ckan.url:http://localhost:5000}")
    private String ckanUrl;

    @Value("${ckan.apikey:apikey}")
    private String ckanApiKey;

    public Either<String, List<CkanDataset>> getDatasets() {
        Optional<List<String>> opt = ckanClientService.getDatasetList(ckanUrl);
        if(!opt.isPresent()) return Either.left("dataset.notif.error.fetch_datasets");

        List<String> datasets = opt.get();
        return Either.right(datasets.stream()
            .map(idOrName -> ckanClientService.getDataset(ckanUrl, idOrName).get())
            .collect(Collectors.toList()));
    }

    public Either<String, Map<String, String>> getLicences() {
        Optional<List<CkanLicense>> opt = ckanClientService.getLicenseList(ckanUrl);
        if(!opt.isPresent()) return Either.left("dataset.notif.error.fetch_licences");

        List<CkanLicense> licenses = opt.get();
        Map<String, String> unsortedLicences =
            licenses.stream().collect(Collectors.toMap(CkanLicense::getId, CkanLicense::getTitle));
        Map<String, String> result = new LinkedHashMap<>();
        unsortedLicences.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
        return Either.right(result);
    }

    public Either<String, List<CkanOrganization>> getOrganizations() {
        Optional<List<CkanOrganization>> opt = ckanClientService.getOrganizationList(ckanUrl);
        if(!opt.isPresent()) return Either.left("dataset.notif.error.fetch_organizations");

        return Either.right(opt.get());
    }

    public Either<String, CkanOrganization> getOrganization(String OrganizationId) {
        Optional<CkanOrganization> opt = ckanClientService.getOrganization(ckanUrl,OrganizationId);
        if(!opt.isPresent()) return Either.left("dataset.notif.error.fetch_organizations");

        return Either.right(opt.get());
    }

    public Either<String, List<CkanTag>> getTags() {
        Optional<List<CkanTag>> opt = ckanClientService.getTagList(ckanUrl);
        if(!opt.isPresent()) return Either.left("dataset.notif.error.fetch_tags");

        return Either.right(opt.get());
    }

    public Either<String, CkanDataset> getOrCreateDataset(DcModelMapping dcModelMapping) {
        Optional<CkanDataset> optGet = null;
        if (!StringUtils.isEmpty(dcModelMapping.getCkanPackageId())) {
            optGet = ckanClientService.getDataset(ckanUrl, dcModelMapping.getCkanPackageId());
        } else if (!StringUtils.isEmpty(dcModelMapping.getName())) {
            optGet = ckanClientService.getDataset(ckanUrl, slugify(dcModelMapping.getName()));
        }

        if (optGet == null || !optGet.isPresent()) {
            String name = slugify(dcModelMapping.getName());
            String organizationId = dcModelMapping.getOrganizationId();
            LOGGER.debug("Creating dataset with slug name {} and for organization {} ", name,organizationId);
            CkanOrganization ckanOrganization = ckanClientService.getOrganization(ckanUrl, organizationId).get() ;
            CkanDataset ckanDataset = new CkanDataset(name);
            ckanDataset.setOrganization(ckanOrganization);
            ckanDataset.setMaintainer("ozwillo");
            ckanDataset.setMaintainerEmail("contact@ozwillo.org");
            ckanDataset.setOpen(true);
            ckanDataset.setOwnerOrg(ckanOrganization.getId());
            ckanDataset.setGroups(ckanOrganization.getGroups());
            ckanDataset.setTitle(dcModelMapping.getName());
            ckanDataset.setNotes(dcModelMapping.getNotes());
            ckanDataset.setLicenseId(dcModelMapping.getLicense());
            ckanDataset.setUrl(dcModelMapping.getSource());
            ckanDataset.setVersion(dcModelMapping.getVersion());
            ckanDataset.setTags(dcModelMapping.getTags());
            ckanDataset.setPrivate(dcModelMapping.isPrivate());
            if (dcModelMapping.getGeoLocation() != null) {
                List<CkanExtra> ckanExtras = new ArrayList<>();
                ObjectMapper mapperObj = new ObjectMapper();
                try {
                    String spatialJson = mapperObj.writeValueAsString(dcModelMapping.getGeoLocation());
                    CkanExtra geoLocation = new CkanExtra("spatial", spatialJson);
                    ckanExtras.add(geoLocation);
                    ckanDataset.setExtras(ckanExtras);
                } catch (JsonProcessingException e) {
                    LOGGER.error("Writing spatial dataset values as string failed : {}", e.getMessage());
                }
            }

            Optional<CkanDataset> optCreate = ckanClientService.createDataset(ckanUrl, ckanApiKey, ckanDataset);
            if(!optCreate.isPresent()) return Either.left("dataset.notif.error.create_dataset");
            return Either.right(optCreate.get());
        } else {
            LOGGER.debug("Reusing existing dataset with slug name {} ", optGet.get().getName());
            return Either.right(optGet.get());
        }
    }

    public Either<String, CkanResource> createResource(String packageId, String name,String description) {
        CkanResource ckanResource = new CkanResource();
        ckanResource.setPackageId(packageId);
        ckanResource.setDescription(description);
        ckanResource.setName(name);
        ckanResource.setUrl("_datastore_only_resource");

        Optional<CkanResource> opt = ckanClientService.createResource(ckanUrl, ckanApiKey, ckanResource);
        if(!opt.isPresent()) return Either.left("dataset.notif.error.create_resource");
        return Either.right(opt.get());
    }

    public Either<String, CkanResource> updateResource(String resourceId, String packageId, String name,String description, String type) {
        CkanResource ckanResource = ckanClientService.getResource(ckanUrl, resourceId).get();
        ckanResource.setPackageId(packageId);
        ckanResource.setDescription(description);
        ckanResource.setName(name + "." + type);
        ckanResource.setUrl("upload");

        Optional<CkanResource> opt = ckanClientService.updateResource(ckanUrl, ckanApiKey, ckanResource);
        if(!opt.isPresent()) return Either.left("dataset.notif.error.update_resource");
        return Either.right(opt.get());
    }

    public void updateResourceData(DcModelMapping dcModelMapping, Map<String, Optional<String>> optionalResourceFiles ) throws Exception {
        optionalResourceFiles.forEach((key, optionalResource) -> {
            CkanResource ckanResource = new CkanResource();
            ckanResource.setPackageId(dcModelMapping.getCkanPackageId());
            ckanResource.setUrl("upload");
            ckanResource.setFormat(key);
            ckanResource.setMimetype(key == "csv" ? "text/csv" : "application/json");
            ckanResource.setName(dcModelMapping.getResourceName() + "." + key);
            ckanResource.setDescription(dcModelMapping.getDescription());
            ckanResource.setId(dcModelMapping.getCkanResourceId().get(key));
            ckanResource.setUpload(optionalResource.get().getBytes());

            DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
            ckanResource.setLastModified(dateTimeFormatter.print(LocalDateTime.now()));

            Optional<ResourceResponse> resourceResponseOptional = ckanClientService.updateResourceFile(ckanUrl, ckanApiKey, ckanResource);
            if(resourceResponseOptional.isPresent()) {
                ResourceResponse updateResourceResponse = resourceResponseOptional.get();
                if(!updateResourceResponse.isSuccess()) LOGGER.error("Error while trying to update {} file resource {} to CKAN : {} ", key, dcModelMapping.getResourceName(), updateResourceResponse.getError().getMessage());
                else LOGGER.info("{} file resource {} is updated in CKAN : {} ", key, dcModelMapping.getResourceName(), updateResourceResponse.result.getName());
            } else {
                LOGGER.info("No CKAN response while trying to update {} file of resource {}", key, dcModelMapping.getResourceName());
            }
        });
    }

    public void deleteResource(String id) {
        ckanClientService.deleteResource(ckanUrl, ckanApiKey, id);
    }


    public String slugify(String input) {
        // Decompose unicode characters
        return Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD)
                // replace all combining diacritical marks and also everything that isn't a word or a whitespace character
                .replaceAll("\\p{InCombiningDiacriticalMarks}|[^\\w\\s]", "")
                // replace all occurences of whitespaces or dashes with one single whitespace
                .replaceAll("[\\s-]+", " ")
                // trim the string
                .trim()
                // and replace all blanks with a dash
                .replaceAll("\\s", "-");
    }
}
