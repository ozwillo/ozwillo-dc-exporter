package org.ozwillo.dcexporter.service;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.oasis_eu.spring.datacore.model.DCResource;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.dao.SynchronizerAuditLogRepository;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.ozwillo.dcexporter.model.SynchronizerAuditLog;
import org.ozwillo.dcexporter.utils.CsvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SynchronizerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizerService.class);

    @Autowired
    private DatacoreService datacoreService;

    @Autowired
    private CkanService ckanService;

    @Autowired
    private SystemUserService systemUserService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SynchronizerAuditLogRepository synchronizerAuditLogRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Value("${datacore.containerUrl:http://data.ozwillo.com}")
    private String dcContainerUrl;

    @Scheduled(fixedDelayString = "${application.syncDelay}")
    public void synchronizeOrgs() {
        systemUserService.runAs(() -> {

            dcModelMappingRepository.findAll().forEach(dcModelMapping -> {
                List<SynchronizerAuditLog> auditLogs =
                    synchronizerAuditLogRepository.findByTypeOrderByDateDesc(dcModelMapping.getType());

                if (!auditLogs.isEmpty() &&
                    !datacoreService.hasMoreRecentResources(dcModelMapping.getProject(), dcModelMapping.getType(), auditLogs.get(0).getDate())) {
                    LOGGER.debug("No more recent resources for {}, returning", dcModelMapping.getType());
                    return;
                }

                LOGGER.info("Got some recent data for {}, synchronizing them", dcModelMapping.getType());
                this.sync(dcModelMapping);

                SynchronizerAuditLog newAuditLog = new SynchronizerAuditLog(dcModelMapping.getType(), DateTime.now());
                synchronizerAuditLogRepository.save(newAuditLog);
            });
        });
    }

    private String sync(DcModelMapping dcModelMapping) {
        Optional<File> optionalResourceCsvFile =
            datacoreService.exportResourceToCsv(dcModelMapping.getProject(), dcModelMapping.getType());

        if (!optionalResourceCsvFile.isPresent()) {
            LOGGER.error("Did not get the resource's CSV file, stopping");
            return "KO";
        }

        File csvFile = optionalResourceCsvFile.get();

        ckanService.updateResourceData(dcModelMapping.getCkanPackageId(), dcModelMapping.getCkanResourceId(), csvFile);

        if (dcModelMapping.getDcId().equals(dcContainerUrl + "/dc/type/dcmo:model_0/orgfr:Organisation_0")) {
            LOGGER.debug("Generating data file for organizations count by department");
            List<DCResource> orgResources =
                datacoreService.getAllResourcesForType(dcModelMapping.getProject(), dcModelMapping.getType());
            Map<String, List<DCResource>> orgByDepartment = orgResources.stream()
                .filter(resource -> resource.getAsString("adrpost:postName") != null)
                .collect(Collectors.groupingBy(resource -> {
                    String postName = resource.getAsString("adrpost:postName");
                    return postName.substring(postName.indexOf("FR-") + 3, postName.lastIndexOf("/"));
                }));
            List<List<String>> lines = orgByDepartment.keySet().stream()
                .map(departmentCode -> {
                    DCResource orgResource = orgByDepartment.get(departmentCode).get(0);
                    String departmentName = datacoreService.getDepartementNameFromOrganization(orgResource).orElse("Inconnu");
                    LOGGER.info("adding {} - {} - {}", departmentName, departmentCode, orgByDepartment.get(departmentCode).size());
                    List<String> row = new ArrayList<>();
                    row.add(departmentName);
                    row.add(departmentCode);
                    row.add(String.valueOf(orgByDepartment.get(departmentCode).size()));
                    return row;
                })
                .collect(Collectors.toList());
            ImmutableList<String> headers = ImmutableList.of("departementName", "departementCode", "count");
            LOGGER.info("adding headers {}", headers.toString());

            CsvUtils.writeToFile(headers, lines);
        }

        return "OK";
    }
}
