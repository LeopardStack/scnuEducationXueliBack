package com.scnujxjy.backendpoint.service.oa;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentTransferApplicationService {

    private final StudentTransferApplicationRepository repository;

    public StudentTransferApplicationService(StudentTransferApplicationRepository repository) {
        this.repository = repository;
    }

    /**
     * 创建一个空文档
     * @return
     */
    public StudentTransferApplication createNewApplication() {
        // 创建一个空的表单实例
        StudentTransferApplication newApplication = new StudentTransferApplication();
        // 初始化或设置一些默认值（如果需要的话）
        // ...
        return repository.save(newApplication);
    }

    /**
     * 创建一个指定的文档
     * @return
     */
    public StudentTransferApplication createUserApplication(StudentTransferApplication newApplication) {
        return repository.save(newApplication);
    }

    /**
     * 指定 id 更新相应的文档
     * @param id
     * @param updatedApplication
     * @return
     */
    public StudentTransferApplication updateApplication(String id, StudentTransferApplication updatedApplication) {
        // 查找现有的表单实例
        Optional<StudentTransferApplication> existingApplication = repository.findById(id);
        if (existingApplication.isPresent()) {
            StudentTransferApplication application = existingApplication.get();
            // 检查每个字段，如果新实例中有值，则更新
            if (updatedApplication.getIntendedMajor() != null) {
                application.setIntendedMajor(updatedApplication.getIntendedMajor());
            }
            if (updatedApplication.getTransferOutApprover() != null) {
                application.setTransferOutApprover(updatedApplication.getTransferOutApprover());
            }
            return repository.save(application);
        } else {
            // 处理找不到实例的情况，例如抛出异常或返回null
            // ...
            return null;
        }
    }

    /**
     * 获取指定 id 的文档实例
     * @param id
     * @return
     */
    public Optional<StudentTransferApplication> getApplicationById(String id) {
        return repository.findById(id);
    }
}
