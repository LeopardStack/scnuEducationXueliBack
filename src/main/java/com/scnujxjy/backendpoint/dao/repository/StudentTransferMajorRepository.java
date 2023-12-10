package com.scnujxjy.backendpoint.dao.repository;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferMajorDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentTransferMajorRepository extends MongoRepository<StudentTransferMajorDocument, String> {
}

