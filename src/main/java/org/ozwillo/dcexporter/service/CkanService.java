package org.ozwillo.dcexporter.service;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.*;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CkanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CkanService.class);

    @Value("${ckan.url:http://localhost:5000}")
    private String ckanUrl;

    @Value("${ckan.apikey:apikey}")
    private String ckanApiKey;

    public List<CkanDataset> getDatasets() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        List<String> datasets = ckanClient.getDatasetList();
        return datasets.stream()
            .map(ckanClient::getDataset)
            .collect(Collectors.toList());
    }

    public Map<String, String> getLicences() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        List<CkanLicense> licenses = ckanClient.getLicenseList();
        Map<String, String> unsortedLicences =
            licenses.stream().collect(Collectors.toMap(CkanLicense::getId, CkanLicense::getTitle));
        Map<String, String> result = new LinkedHashMap<>();
        unsortedLicences.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
        return result;
    }

    public List<CkanTag> getTags() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        return ckanClient.getTagList();
    }

    public CkanDataset getOrCreateDataset(DcModelMapping dcModelMapping) throws CkanException {
        CkanClient ckanClient = new CkanClient(ckanUrl, ckanApiKey);

        CkanDataset ckanDataset = null;
        try {
            if (dcModelMapping.getCkanPackageId() != null && !dcModelMapping.getCkanPackageId().equals("")) {
                ckanDataset = ckanClient.getDataset(dcModelMapping.getCkanPackageId());
            }
        } catch (CkanException e) {
            // Not Found Error is an « expected » result
            // FIXME : this is a poor way to perform a search
            if (!e.getCkanResponse().getError().getType().equals("Not Found Error")) {
                throw e;
            }
        }

        if (ckanDataset == null) {
            String name = slugify(dcModelMapping.getName());
            LOGGER.debug("Creating dataset with slug name {} ", name);
            CkanOrganization ckanOrganization = ckanClient.getOrganization("ozwillo");
            ckanDataset = new CkanDataset(name);
            ckanDataset.setOrganization(ckanOrganization);
            ckanDataset.setMaintainer("ozwillo");
            ckanDataset.setMaintainerEmail("contact@ozwillo.org");
            ckanDataset.setOpen(true);
            ckanDataset.setOwnerOrg(ckanOrganization.getId());
            ckanDataset.setTitle(dcModelMapping.getName());
            ckanDataset.setNotes(dcModelMapping.getNotes());
            ckanDataset.setLicenseId(dcModelMapping.getLicense());
            ckanDataset.setUrl(dcModelMapping.getSource());
            ckanDataset.setVersion(dcModelMapping.getVersion());
            ckanDataset.setTags(dcModelMapping.getTags());
            ckanDataset.setPriv(false);

            return ckanClient.createDataset(ckanDataset);
        } else {
            LOGGER.debug("Reusing existing dataset with slug name {} ", ckanDataset.getName());
            return ckanDataset;
        }
    }

    public CkanResource createResource(String packageId, String name,String description) {
        CkanClient ckanClient = new CkanClient(ckanUrl, ckanApiKey);

        CkanResource ckanResource = new CkanResource();
        ckanResource.setPackageId(packageId);
        ckanResource.setDescription(description);
        ckanResource.setName(name);
        ckanResource.setUrl("upload");

        return ckanClient.createResource(ckanResource);
    }

    public CkanResource updateResource(String resourceId, String packageId, String name,String description) {
        CkanClient ckanClient = new CkanClient(ckanUrl, ckanApiKey);

        CkanResource ckanResource = ckanClient.getResource(resourceId);
        ckanResource.setPackageId(packageId);
        ckanResource.setDescription(description);
        ckanResource.setName(name);
        ckanResource.setUrl("upload");

        return ckanClient.updateResource(ckanResource);
    }

    public void updateResourceData(DcModelMapping dcModelMapping, String resource) throws Exception {
        CkanClient ckanClient = new CheckedCkanClient(ckanUrl, ckanApiKey);


        CkanResourceBase ckanResourceBase = new CkanResourceBase();
        ckanResourceBase.setPackageId(dcModelMapping.getCkanPackageId());
        ckanResourceBase.setUrl("upload");
        ckanResourceBase.setFormat("CSV");
        ckanResourceBase.setMimetype("text/csv");
        ckanResourceBase.setName(dcModelMapping.getType() + ".csv");
        ckanResourceBase.setDescription(dcModelMapping.getDescription());
        ckanResourceBase.setId(dcModelMapping.getCkanResourceId());
        ckanResourceBase.setUploadByte(resource.getBytes());

        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
        ckanResourceBase.setLastModified(dateTimeFormatter.print(LocalDateTime.now()));

        try{
            ckanClient.updateResourceData(ckanResourceBase);
        }catch(CkanException ckanException){
            throw new Exception(ckanException.getCkanResponse().getError().getMessage());
        }
    }

    private String slugify(String input) {
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
