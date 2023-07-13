package com.scnujxjy.backendpoint.service.project_manage;

import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingSummary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
public interface TrainingSummaryService extends IService<TrainingSummary> {
    /**
     * 获取所有的培训概要文件信息
     * @return List<AllProjectSummaryFiles>
     */
    public List<AllProjectSummaryFiles> getAllProjectSummaryFiles();
}
