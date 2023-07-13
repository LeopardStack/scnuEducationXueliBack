package com.scnujxjy.backendpoint.mapper.project_manage;

import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingSummary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
public interface TrainingSummaryMapper extends BaseMapper<TrainingSummary> {
    /**
     * 获取所有的培训概要文件
     * @return List<AllProjectSummaryFiles>
     */
    @Select("SELECT ts.id, ts.file_name, ts.file_type, " +
            "ts.file_path, ts.uploader, pu.Username as uploaderName, ts.uploaded_at, " +
            "ts.file_size, ts.project_id  as projectId, tp.`name` as projectName FROM TrainingSummary ts " +
            "LEFT JOIN TrainingProject tp ON ts.project_id = tp.id " +
            "LEFT JOIN PlatformUser pu ON ts.uploader = pu.UserID")
    List<AllProjectSummaryFiles> getAllProjectSummaryFiles();
}
