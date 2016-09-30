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

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/orgfr:Organisation_0", "organisations",
            "Organisations", "org_1", "orgfr:Organisation_0", "Export des organisations françaises");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/orgbg:Организация_0", "organisations",
            "Organisations", "org_1", "orgbg:Организация_0", "Export des organisations bulgares");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/orgit:Organizzazione_0", "organisations",
            "Organisations", "org_1", "orgit:Organizzazione_0", "Export des organisations italiennes");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/orgtr:Organizasyon_0", "organisations",
            "Organisations", "org_1", "orgtr:Organizasyon_0", "Export des organisations turques");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/orges:Organización_0", "organisations",
            "Organisations", "org_1", "orges:Organización_0", "Export des organisations espagnoles");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/poi:Geoloc_0", "points-interet",
            "Points d'intérêt", "poi_0", "poi:Geoloc_0", "Export des points d'intérêt");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocofr:Pays_0", "pays",
            "Pays", "geo_1", "geocofr:Pays_0", "Export des pays français");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocoit:Paese_0", "pays",
            "Pays", "geo_1", "geocoit:Paese_0", "Export des pays italiens");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocoes:País_0", "pays",
            "Pays", "geo_1", "geocoes:País_0", "Export des pays espagnols");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocobg:Cтрана_0", "pays",
            "Pays", "geo_1", "geocobg:Cтрана_0", "Export des pays bulgares");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocotr:Ülke_0", "pays",
            "Pays", "geo_1", "geocotr:Ülke_0", "Export des pays turcs");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon2fr:Région_0", "regions",
            "Régions", "geo_1", "geon2fr:Région_0", "Export des régions françaises");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon2it:Regione_0", "regions",
            "Régions", "geo_1", "geon2it:Regione_0", "Export des régions italiennes");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon2es:CommunidadAutonoma_0", "regions",
            "Régions", "geo_1", "geon2es:CommunidadAutonoma_0", "Export des régions espagnoles");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon3fr:Département_0", "departements",
            "Départements", "geo_1", "geon3fr:Département_0", "Export des départements français");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon3it:Provincia_0", "departements",
            "Départements", "geo_1", "geon3it:Provincia_0", "Export des départements italiens");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon3es:Provincia_0", "departements",
            "Départements", "geo_1", "geon3es:Provincia_0", "Export des départements espagnols");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon3bg:Област_0", "departements",
            "Départements", "geo_1", "geon3bg:Област_0", "Export des départements bulgares");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geon3tr:İl_0", "departements",
            "Départements", "geo_1", "geon3tr:İl_0", "Export des départements turcs");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocigrfr:Agglomération_0", "agglomerations",
            "Agglomérations", "geo_1", "geocigrfr:Agglomération_0", "Export des agglomérations françaises");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocigres:Comarca_0", "agglomerations",
            "Agglomérations", "geo_1", "geocigres:Comarca_0", "Export des agglomérations espagnoles");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocigrbg:Община_0", "agglomerations",
            "Agglomérations", "geo_1", "geocigrbg:Община_0", "Export des agglomérations bulgares");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocifr:Commune_0", "communes",
            "Communes", "geo_1", "geocifr:Commune_0", "Export des communes françaises");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geociit:Comune_0", "communes",
            "Communes", "geo_1", "geociit:Comune_0", "Export des communes italiennes");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocies:Municipio_0", "communes",
            "Communes", "geo_1", "geocies:Municipio_0", "Export des communes espagnoles");
        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geocibg:НаселеноMесто_0", "communes",
            "Communes", "geo_1", "geocibg:НаселеноMесто_0", "Export des communes bulgares");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/geoditr:İlçe_0", "districts",
            "Districts", "geo_1", "geoditr:İlçe_0", "Export des districts turcs");

        createMappingIfNotExists(dcContainerUrl + "/dc/type/dcmo:model_0/citizenkin:electoral_roll_registration_0",
            "electoral-roll-registration", "Inscriptions sur les listes électorales", "citizenkin_0",
            "citizenkin:electoral_roll_registration_0", "Export des inscriptions sur les listes électorales françaises");
    }

    private void createMappingIfNotExists(String modelUrl, String packageId, String packageName, String project,
                                          String model, String resourceName) {

        if (dcModelMappingRepository.findByDcId(modelUrl) == null) {

            CkanDataset ckanDataset = ckanService.getOrCreateDataset(packageId, packageName);
            CkanResource ckanResource = ckanService.createResource(ckanDataset.getId(), resourceName);

            DcModelMapping orgMapping = new DcModelMapping(modelUrl, project, model, packageName);
            orgMapping.setCkanPackageId(ckanDataset.getId());
            orgMapping.setCkanResourceId(ckanResource.getId());
            dcModelMappingRepository.save(orgMapping);

            LOGGER.info("Initialized {} mappping", packageName);
        }
    }
}
