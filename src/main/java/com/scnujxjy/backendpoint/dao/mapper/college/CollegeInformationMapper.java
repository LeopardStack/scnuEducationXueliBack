package com.scnujxjy.backendpoint.dao.mapper.college;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 学院基础信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface CollegeInformationMapper extends BaseMapper<CollegeInformationPO> {

    @Select("SELECT * FROM college_information WHERE college_name=#{collegeName}")
    List<CollegeInformationPO> selectByCollegeName(String collegeName);

    @Select("SELECT college_name FROM college_information;")
    List<String> getAllCollegeNames();
}
