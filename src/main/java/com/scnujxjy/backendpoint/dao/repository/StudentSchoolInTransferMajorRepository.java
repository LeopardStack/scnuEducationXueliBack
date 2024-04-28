package com.scnujxjy.backendpoint.dao.repository;

import com.scnujxjy.backendpoint.dao.mongoEntity.oa.StudentSchoolInTransferMajorDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentSchoolInTransferMajorRepository extends MongoRepository<StudentSchoolInTransferMajorDocument, String> {
}

