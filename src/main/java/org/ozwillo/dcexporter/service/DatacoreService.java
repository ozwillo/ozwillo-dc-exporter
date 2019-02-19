package org.ozwillo.dcexporter.service;

import org.oasis_eu.spring.datacore.DatacoreClient;
import org.oasis_eu.spring.datacore.model.*;
import org.ozwillo.dcexporter.dao.DcModelMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatacoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatacoreService.class);

    private final DatacoreClient datacore;
    private final DcModelMappingRepository dcModelMappingRepository;

    @Value("${datacore.modifiedField}")
    private String modifiedField;

    @Autowired
    public DatacoreService(DatacoreClient datacore, DcModelMappingRepository dcModelMappingRepository) {
        this.datacore = datacore;
        this.dcModelMappingRepository = dcModelMappingRepository;
    }

    @Cacheable("dc-models")
    public List<DCModel> getModels() {
        // TODO : iterate until we have all
        return datacore.findModels(50).stream()
            .filter(dcModel -> dcModelMappingRepository.findByDcIdAndIsDeleted(dcModel.getId().toString(),false) == null)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public DCModel getModel(String project, String modelType) {
        return datacore.findModel(project, modelType);
    }

    public boolean hasMoreRecentResources(String project, String type, LocalDateTime fromDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter. ISO_LOCAL_DATE_TIME;//ISODateTimeFormat.dateTime();
        DCQueryParameters parameters = new DCQueryParameters(modifiedField, DCOperator.GTE, fromDate.format(dateTimeFormatter));

        List<DCResource> newResources = datacore.findResources(project, type, parameters, 0, 1);
        LOGGER.debug("Retrieved {} resources newer than {}", newResources.size(), fromDate.format(dateTimeFormatter));
        return !newResources.isEmpty();
    }

    List<DCResource> getAllResources(String projet, String type) {
        List<DCResource> allDCResources = new ArrayList<>();
        DCQueryParameters parameters = new DCQueryParameters("@id", DCOrdering.DESCENDING);
        // TODO : keep a local cache of the DC resources
        while (true) {
            List<DCResource> intermediateResult = datacore.findResources(projet, type, parameters, 0, 100);

            allDCResources.addAll(intermediateResult);

            if (intermediateResult.size() < 100) {
                break;
            } else {
                // TODO : why DCResource does not have the @id field in its data ?
                //        (it is present in the data returned from the DC)
                String lastResultId = intermediateResult.get(99).getUri();
                parameters = new DCQueryParameters("@id", DCOrdering.DESCENDING, DCOperator.LT, lastResultId);
            }
        }

        return allDCResources;
    }
}
