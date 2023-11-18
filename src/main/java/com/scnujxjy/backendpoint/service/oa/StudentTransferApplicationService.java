package com.scnujxjy.backendpoint.service.oa;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import com.scnujxjy.backendpoint.dao.repository.StudentTransferApplicationRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转专业服务
 */
@Service
public class StudentTransferApplicationService {
    @Resource
    private StudentTransferApplicationRepository repository;


    // 添加一个新的转专业申请
    public StudentTransferApplication addNewTransferApplication(StudentTransferApplication application) {
        return repository.save(application);
    }

    // 根据学生ID更新专业
    public StudentTransferApplication updateMajor(String studentId, String newMajor) {
        StudentTransferApplication application = repository.findById(studentId).orElse(null);
        if (application != null) {
            application.setIntendedMajor(newMajor);
            return repository.save(application);
        }
        // 或者抛出一个异常
        return null;
    }

    // 根据学生ID更新姓名
    public StudentTransferApplication updateStudentName(String studentId, String newName) {
        StudentTransferApplication application = repository.findById(studentId).orElse(null);
        if (application != null) {
            application.setName(newName);
            return repository.save(application);
        }
        // 或者抛出一个异常
        return null;
    }

    // 根据ID获取转专业申请
    public StudentTransferApplication getApplicationById(String id) {
        return repository.findById(id).orElse(null);
        // 你也可以选择在找不到文档时抛出一个异常
    }
}
