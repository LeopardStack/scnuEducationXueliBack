package com.scnujxjy.backendpoint.dao.mapper.teaching_point;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 教学点基础信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface TeachingPointInformationMapper extends BaseMapper<TeachingPointInformationPO> {

    @Select("SELECT MAX(CAST(teaching_point_id AS UNSIGNED)) AS max_id FROM teaching_point_information")
    Long selectMaxTeachingPointId();

    @Select("SELECT teaching_point_name FROM teaching_point_information;")
    List<String> getAllTeachingPointNames();
}
