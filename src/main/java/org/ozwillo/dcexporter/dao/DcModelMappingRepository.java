package org.ozwillo.dcexporter.dao;

import org.ozwillo.dcexporter.model.DcModelMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DcModelMappingRepository extends MongoRepository<DcModelMapping, String> {

    DcModelMapping findByDcId(String dcId);

    DcModelMapping findByDcIdAndIsDeleted(String dcId, Boolean isDeleted);

    @Query("{ 'isDeleted' : false }")
    List<DcModelMapping> findAll();
    
    @Override
    Optional<DcModelMapping> findById(String id);

    void deleteById(String id);
}
