package org.ozwillo.dcexporter.service;

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

    @Value("${ckan.organizationsResourceId}")
    private String organizationsResourceId;

    @Value("${ckan.poisResourceId}")
    private String poisResourceId;

    @Value("${ckan.geoAreasResourceId}")
    private String geoAreasResourceId;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        LOGGER.info("Initializing missing sample DC model mappings");

        if (dcModelMappingRepository.findByDcId("http://data.ozwillo.com/dc/type/dcmo:model_0/org:Organization_0") == null) {
            DcModelMapping orgMapping = new DcModelMapping("http://data.ozwillo.com/dc/type/dcmo:model_0/org:Organization_0",
                "org_1", "org:Organization_0", "Organisations");
            orgMapping.setCkanPackageId("organisations");
            orgMapping.setCkanResourceId(organizationsResourceId);
            dcModelMappingRepository.save(orgMapping);
            LOGGER.info("Initialized organizations mappping");
        }

        if (dcModelMappingRepository.findByDcId("http://data.ozwillo.com/dc/type/dcmo:model_0/poi:Geoloc_0") == null) {
            DcModelMapping poiMapping = new DcModelMapping("http://data.ozwillo.com/dc/type/dcmo:model_0/poi:Geoloc_0",
                "poi_0", "poi:Geoloc_0", "Points d'intérêt");
            poiMapping.setCkanPackageId("points-interet-poi");
            poiMapping.setCkanResourceId(poisResourceId);
            dcModelMappingRepository.save(poiMapping);
            LOGGER.info("Initialized pois mappping");
        }

        if (dcModelMappingRepository.findByDcId("http://data.ozwillo.com/dc/type/dcmo:model_0/geo:Area_0") == null) {
            DcModelMapping geoAreaMapping = new DcModelMapping("http://data.ozwillo.com/dc/type/dcmo:model_0/geo:Area_0",
                "geo_1", "geo:Area_0", "Données géographiques");
            geoAreaMapping.setCkanPackageId("donnees-geographiques");
            geoAreaMapping.setCkanResourceId(geoAreasResourceId);
            dcModelMappingRepository.save(geoAreaMapping);
            LOGGER.info("Initialized geo areas mappping");
        }
    }
}
