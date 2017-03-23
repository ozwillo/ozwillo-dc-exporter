package org.ozwillo.dcexporter.dao;

import org.ozwillo.dcexporter.model.DcModelMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DcModelMappingRepository extends MongoRepository<DcModelMapping, String> {

    DcModelMapping findByDcId(String dcId);

    @Query("{ 'isDeleted' : false }")
    List<DcModelMapping> findAllByOrderByResourceNameAsc();

    @Query("{ 'isDeleted' : false }")
    List<DcModelMapping> findAll();
    
    DcModelMapping findById(String id);

    Long deleteById(String id);
}
