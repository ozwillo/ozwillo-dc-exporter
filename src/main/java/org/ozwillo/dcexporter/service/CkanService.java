package org.ozwillo.dcexporter.service;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.*;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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

    public Map<String, String> getLicences() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        List<CkanLicense> licenses = ckanClient.getLicenseList();
        return licenses.stream().collect(Collectors.toMap(CkanLicense::getId, CkanLicense::getTitle));
    }

    public CkanDataset getOrCreateDataset(String name, String title) {
        CkanClient ckanClient = new CkanClient(ckanUrl, ckanApiKey);

        CkanDataset ckanDataset = null;
        try {
            ckanDataset = ckanClient.getDataset(name);
        } catch (CkanException e) {
            // Not Found Error is an « expected » result
            // FIXME : this is a poor way to perform a search
            if (!e.getCkanResponse().getError().getType().equals("Not Found Error")) {
                throw e;
            }

            LOGGER.debug("Dataset {} already exists", name);
        }

        if (ckanDataset == null) {
            CkanOrganization ckanOrganization = ckanClient.getOrganization("ozwillo");
            ckanDataset = new CkanDataset(name);
            ckanDataset.setOrganization(ckanOrganization);
            ckanDataset.setOwnerOrg(ckanOrganization.getId());
            ckanDataset.setTitle(title);
            ckanDataset.setPriv(false);

            return ckanClient.createDataset(ckanDataset);
        } else {
            return ckanDataset;
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

    public void updateResourceData(String packageId, String id, File poiCsvFile) {
        CkanClient ckanClient = new CheckedCkanClient(ckanUrl, ckanApiKey);

        CkanResourceBase ckanResourceBase = new CkanResourceBase();
        ckanResourceBase.setPackageId(packageId);
        ckanResourceBase.setUrl("upload");
        ckanResourceBase.setId(id);
        ckanResourceBase.setUpload(poiCsvFile, true);

        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
        ckanResourceBase.setLastModified(dateTimeFormatter.print(LocalDateTime.now()));

        CkanResource result = ckanClient.updateResourceData(ckanResourceBase);
    }
}
