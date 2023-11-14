package com.scnujxjy.backendpoint.dao.mongoEntity;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentTransferApplicationRepository extends MongoRepository<StudentTransferApplication, String> {
    // 这里可以添加一些自定义的查询方法，如果需要的话
}
