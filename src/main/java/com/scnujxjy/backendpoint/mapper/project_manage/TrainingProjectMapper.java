package com.scnujxjy.backendpoint.mapper.project_manage;

import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-07-06
 */
public interface TrainingProjectMapper extends BaseMapper<TrainingProject> {
    /**
     * 获取培训项目对应的所有培训概要
     * @param projectId 项目 id
     * @return List<AllProjectSummaryFiles>
     */
    @Select("SELECT ts.id, ts.file_name, ts.file_type, " +
            "ts.file_path, ts.uploader, ts.uploaded_at, " +
            "ts.file_size, tp.`name`, pu.Username FROM TrainingSummary ts " +
            "LEFT JOIN TrainingProject tp ON ts.project_id = tp.id " +
            "LEFT JOIN PlatformUser pu ON ts.uploader = pu.UserID" +
            "WHERE tp.id = #{projectId}")
    List<AllProjectSummaryFiles> getProjectSummaryFiles(long projectId);
}
