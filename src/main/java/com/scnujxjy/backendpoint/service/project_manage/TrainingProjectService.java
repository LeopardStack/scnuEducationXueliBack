package com.scnujxjy.backendpoint.service.project_manage;

import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingProject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leopard
 * @since 2023-07-06
 */
public interface TrainingProjectService extends IService<TrainingProject> {
    public List<AllProjectSummaryFiles> getProjectSummaryFiles(long projectId);
}
