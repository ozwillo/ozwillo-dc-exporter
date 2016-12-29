package org.ozwillo.dcexporter.dao;

import org.ozwillo.dcexporter.model.DcModelMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface DcModelMappingRepository extends MongoRepository<DcModelMapping, String> {

    DcModelMapping findByDcId(String dcId);
}
