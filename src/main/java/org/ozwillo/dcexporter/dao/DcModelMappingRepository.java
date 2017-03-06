package org.ozwillo.dcexporter.dao;

import org.ozwillo.dcexporter.model.DcModelMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DcModelMappingRepository extends MongoRepository<DcModelMapping, String> {

    DcModelMapping findByDcId(String dcId);

    List<DcModelMapping> findAllByOrderByResourceNameAsc();
    
    DcModelMapping findById(String id);
}
