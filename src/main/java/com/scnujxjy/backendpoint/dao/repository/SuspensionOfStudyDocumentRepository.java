package com.scnujxjy.backendpoint.dao.repository;

import com.scnujxjy.backendpoint.dao.mongoEntity.oa.SuspensionOfStudyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB Repository for handling operations related to the Suspension of Study documents.
 * @author lth
 */
@Repository
public interface SuspensionOfStudyDocumentRepository extends MongoRepository<SuspensionOfStudyDocument, String> {

}
