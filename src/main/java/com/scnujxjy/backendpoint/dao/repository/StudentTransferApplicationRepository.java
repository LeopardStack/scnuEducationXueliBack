package com.scnujxjy.backendpoint.dao.repository;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentTransferApplicationRepository extends MongoRepository<StudentTransferApplication, String> {
}

