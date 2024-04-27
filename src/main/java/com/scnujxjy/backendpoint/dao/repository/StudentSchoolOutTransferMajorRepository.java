package com.scnujxjy.backendpoint.dao.repository;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentSchoolOutTransferMajorDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentSchoolOutTransferMajorRepository extends MongoRepository<StudentSchoolOutTransferMajorDocument, String> {
}
