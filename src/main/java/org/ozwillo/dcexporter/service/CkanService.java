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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CkanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CkanService.class);

    @Value("${ckan.url:http://localhost:5000}")
    private String ckanUrl;

    @Value("${ckan.apikey:apikey}")
    private String ckanApiKey;

    public List<String> getDatasets() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        return ckanClient.getDatasetList();
    }

    public Map<String, String> getLicences() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        List<CkanLicense> licenses = ckanClient.getLicenseList();
        return licenses.stream().collect(Collectors.toMap(CkanLicense::getId, CkanLicense::getTitle));
    }

    public Map<String, String> getTags() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        List<CkanTag> tags = ckanClient.getTagList();
        return tags.stream().collect(Collectors.toMap(CkanTag::getId, CkanTagBase::getName));
    }

    public CkanDataset getOrCreateDataset(DcModelMapping dcModelMapping) {
        CkanClient ckanClient = new CkanClient(ckanUrl, ckanApiKey);

        CkanDataset ckanDataset = null;
        try {
            ckanDataset = ckanClient.getDataset(dcModelMapping.getName());
        } catch (CkanException e) {
            // Not Found Error is an « expected » result
            // FIXME : this is a poor way to perform a search
            if (!e.getCkanResponse().getError().getType().equals("Not Found Error")) {
                throw e;
            }

            LOGGER.debug("Dataset {} already exists", dcModelMapping.getName());
        }

        if (ckanDataset == null) {
            CkanOrganization ckanOrganization = ckanClient.getOrganization("ozwillo");
            ckanDataset = new CkanDataset(dcModelMapping.getName());
            ckanDataset.setOrganization(ckanOrganization);
            ckanDataset.setMaintainer("ozwillo");
            ckanDataset.setMaintainerEmail("contact@ozwillo.org");
            ckanDataset.setOpen(true);
            ckanDataset.setOwnerOrg(ckanOrganization.getId());
            ckanDataset.setTitle(dcModelMapping.getResourceName());
            ckanDataset.setLicenseTitle(dcModelMapping.getLicense());
            ckanDataset.setUrl(dcModelMapping.getSource());
            ckanDataset.setVersion(dcModelMapping.getVersion());
            ckanDataset.setTags(dcModelMapping.getTags());
            ckanDataset.setPriv(false);

            return ckanClient.createDataset(ckanDataset);
        } else {
            CkanOrganization ckanOrganization = ckanClient.getOrganization("ozwillo");
            ckanDataset = new CkanDataset(dcModelMapping.getName());
            ckanDataset.setOrganization(ckanOrganization);
            ckanDataset.setMaintainer("ozwillo");
            ckanDataset.setMaintainerEmail("contact@ozwillo.org");
            ckanDataset.setOpen(true);
            ckanDataset.setOwnerOrg(ckanOrganization.getId());
            ckanDataset.setTitle(dcModelMapping.getResourceName());
            ckanDataset.setLicenseTitle(dcModelMapping.getLicense());
            ckanDataset.setUrl(dcModelMapping.getSource());
            ckanDataset.setVersion(dcModelMapping.getVersion());
            ckanDataset.setTags(dcModelMapping.getTags());
            ckanDataset.setPriv(false);
            return ckanClient.updateDataset(ckanDataset);
        }
    }

    public CkanResource createResource(String packageId, String name) {
        CkanClient ckanClient = new CkanClient(ckanUrl, ckanApiKey);

        CkanResource ckanResource = new CkanResource();
        ckanResource.setPackageId(packageId);
        ckanResource.setName(name);
        ckanResource.setUrl("upload");

        return ckanClient.createResource(ckanResource);
    }

    public void updateResourceData(String packageId, String id, String resource) throws IOException {
        CkanClient ckanClient = new CheckedCkanClient(ckanUrl, ckanApiKey);
        //We create a file waiting to modify the "setUpload" function in jackan
        File resourceFile = null;

        resourceFile = File.createTempFile("export-", ".csv");
        LOGGER.debug("Writing data in temp file {}", resourceFile.getAbsolutePath());
        FileWriter resourceFileWriter = new FileWriter(resourceFile, true);
        resourceFileWriter.write(resource);
        resourceFileWriter.flush();
        resourceFileWriter.close();


        CkanResourceBase ckanResourceBase = new CkanResourceBase();
        ckanResourceBase.setPackageId(packageId);
        ckanResourceBase.setUrl("upload");
        ckanResourceBase.setId(id);
        // TODO: Change the "setUpload" function to set an "inputstream" object in jackan(External Libraries)
        ckanResourceBase.setUpload(resourceFile, true);

        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
        ckanResourceBase.setLastModified(dateTimeFormatter.print(LocalDateTime.now()));

        ckanClient.updateResourceData(ckanResourceBase);
        resourceFile.delete();
    }
}
