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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sun.deploy.util.StringUtils.trimWhitespace;
import static org.apache.commons.lang3.StringUtils.stripAccents;

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
            String name = stripAccents(trimWhitespace(dcModelMapping.getName()).replaceAll(" ", "-").toLowerCase());
            LOGGER.debug("Dataset {} creating id", name);
            CkanOrganization ckanOrganization = ckanClient.getOrganization("ozwillo");
            ckanDataset = new CkanDataset(name);
            ckanDataset.setOrganization(ckanOrganization);
            ckanDataset.setMaintainer("ozwillo");
            ckanDataset.setMaintainerEmail("contact@ozwillo.org");
            ckanDataset.setOpen(true);
            ckanDataset.setOwnerOrg(ckanOrganization.getId());
            ckanDataset.setTitle(dcModelMapping.getName());
            ckanDataset.setLicenseId(dcModelMapping.getLicense());
            ckanDataset.setUrl(dcModelMapping.getSource());
            ckanDataset.setVersion(dcModelMapping.getVersion());
            ckanDataset.setTags(dcModelMapping.getTags());
            ckanDataset.setPriv(false);

            return ckanClient.createDataset(ckanDataset);
        } else {
            String title = dcModelMapping.getName().replaceAll("-", " ");
            CkanOrganization ckanOrganization = ckanClient.getOrganization("ozwillo");
            ckanDataset = new CkanDataset(dcModelMapping.getName());
            ckanDataset.setOrganization(ckanOrganization);
            ckanDataset.setMaintainer("ozwillo");
            ckanDataset.setMaintainerEmail("contact@ozwillo.org");
            ckanDataset.setOpen(true);
            ckanDataset.setOwnerOrg(ckanOrganization.getId());
            ckanDataset.setTitle(title);
            ckanDataset.setLicenseId(dcModelMapping.getLicense());
            ckanDataset.setUrl(dcModelMapping.getSource());
            ckanDataset.setVersion(dcModelMapping.getVersion());
            ckanDataset.setTags(dcModelMapping.getTags());
            ckanDataset.setPriv(false);
            return ckanClient.updateDataset(ckanDataset);
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

    public void updateResourceData(DcModelMapping dcModelMapping, String resource) {
        CkanClient ckanClient = new CheckedCkanClient(ckanUrl, ckanApiKey);


        CkanResourceBase ckanResourceBase = new CkanResourceBase();
        ckanResourceBase.setPackageId(dcModelMapping.getCkanPackageId());
        ckanResourceBase.setUrl("upload");
        ckanResourceBase.setFormat("CSV");
        ckanResourceBase.setMimetype("text/csv");
        ckanResourceBase.setName("file.csv");
        ckanResourceBase.setDescription(dcModelMapping.getDescription());
        ckanResourceBase.setId(dcModelMapping.getCkanResourceId());
        ckanResourceBase.setUploadByte(resource.getBytes());

        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
        ckanResourceBase.setLastModified(dateTimeFormatter.print(LocalDateTime.now()));

        ckanClient.updateResourceData(ckanResourceBase);
    }
}
