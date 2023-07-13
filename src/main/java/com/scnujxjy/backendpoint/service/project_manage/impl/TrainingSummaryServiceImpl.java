package com.scnujxjy.backendpoint.service.project_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingProject;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingSummary;
import com.scnujxjy.backendpoint.mapper.project_manage.TrainingSummaryMapper;
import com.scnujxjy.backendpoint.service.project_manage.TrainingSummaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
@Service
public class TrainingSummaryServiceImpl extends ServiceImpl<TrainingSummaryMapper, TrainingSummary> implements TrainingSummaryService {
    @Override
    public List<AllProjectSummaryFiles> getAllProjectSummaryFiles(){
        return this.baseMapper.getAllProjectSummaryFiles();
    }
}
