package org.ozwillo.dcexporter.service;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanResourceBase;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CkanService {

    @Value("${ckan.url:http://localhost:5000}")
    private String ckanUrl;

    @Value("${ckan.apikey:apikey}")
    private String ckanApiKey;

    public Map<String, String> getLicences() {
        CkanClient ckanClient = new CkanClient(ckanUrl);
        List<CkanLicense> licenses = ckanClient.getLicenseList();
        return licenses.stream().collect(Collectors.toMap(CkanLicense::getId, CkanLicense::getTitle));
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
