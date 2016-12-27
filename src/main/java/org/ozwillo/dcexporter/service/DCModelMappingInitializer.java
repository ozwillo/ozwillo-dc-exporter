package org.ozwillo.dcexporter.service;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.ozwillo.dcexporter.model.DcModelMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Temporary initializer to bootstrap some DC to CKAN mappings.
 *
 * In the future, they will be created and managed from the application's interface
 */
@Component
public class DCModelMappingInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DCModelMappingInitializer.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private DcModelMappingRepository dcModelMappingRepository;

    @Autowired
    private CkanService ckanService;

    @Value("${datacore.containerUrl:http://data.ozwillo.com}")
    private String dcContainerUrl;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        LOGGER.info("Initializing missing sample DC model mappings");
    }

    private void createMappingIfNotExists(String modelUrl, String packageId, String packageName, String project,
                                          String model, String resourceName) {
        DcModelMapping dcModelMapping = dcModelMappingRepository.findByDcId(modelUrl);

        if ( dcModelMapping == null) {

            CkanDataset ckanDataset = ckanService.getOrCreateDataset(dcModelMapping);
            CkanResource ckanResource = ckanService.createResource(ckanDataset.getId(), resourceName);

            DcModelMapping orgMapping = new DcModelMapping(modelUrl, project, model, packageName);
            orgMapping.setCkanPackageId(ckanDataset.getId());
            orgMapping.setCkanResourceId(ckanResource.getId());
            dcModelMappingRepository.save(orgMapping);

            LOGGER.info("Initialized {} mappping", packageName);
        }
    }
}
