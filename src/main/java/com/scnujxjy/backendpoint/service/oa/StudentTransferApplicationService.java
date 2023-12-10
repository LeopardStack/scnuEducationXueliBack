package com.scnujxjy.backendpoint.service.oa;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferMajorDocument;
import com.scnujxjy.backendpoint.dao.repository.StudentTransferMajorRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转专业服务
 */
@Service
public class StudentTransferApplicationService {
    @Resource
    private StudentTransferMajorRepository repository;


    // 添加一个新的转专业申请
    public StudentTransferMajorDocument addNewTransferApplication(StudentTransferMajorDocument application) {
        return repository.save(application);
    }

    // 根据学生ID更新专业
    public StudentTransferMajorDocument updateMajor(String studentId, String newMajor) {
        StudentTransferMajorDocument application = repository.findById(studentId).orElse(null);
        if (application != null) {
            application.setIntendedMajor(newMajor);
            return repository.save(application);
        }
        // 或者抛出一个异常
        return null;
    }

    // 根据学生ID更新姓名
    public StudentTransferMajorDocument updateStudentName(String studentId, String newName) {
        StudentTransferMajorDocument application = repository.findById(studentId).orElse(null);
        if (application != null) {
            application.setName(newName);
            return repository.save(application);
        }
        // 或者抛出一个异常
        return null;
    }

    // 根据ID获取转专业申请
    public StudentTransferMajorDocument getApplicationById(String id) {
        return repository.findById(id).orElse(null);
        // 你也可以选择在找不到文档时抛出一个异常
    }
}
