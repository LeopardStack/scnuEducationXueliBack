package com.scnujxjy.backendpoint.service.project_manage.impl;

import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingProject;
import com.scnujxjy.backendpoint.mapper.project_manage.TrainingProjectMapper;
import com.scnujxjy.backendpoint.service.project_manage.TrainingProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-07-06
 */
@Service
public class TrainingProjectServiceImpl extends ServiceImpl<TrainingProjectMapper, TrainingProject> implements TrainingProjectService {
    @Override
    public List<AllProjectSummaryFiles> getProjectSummaryFiles(long projectId){
        List<AllProjectSummaryFiles> projectSummaryFiles = this.baseMapper.getProjectSummaryFiles(projectId);
        return projectSummaryFiles;
    }
}
