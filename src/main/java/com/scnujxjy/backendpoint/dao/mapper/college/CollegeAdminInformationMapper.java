package com.scnujxjy.backendpoint.dao.mapper.college;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeAdminInformationVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 教务员信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface CollegeAdminInformationMapper extends BaseMapper<CollegeAdminInformationPO> {

    List<CollegeAdminInformationVO> selectCollegeAdminInfos(@Param("entity") CollegeAdminInformationRO collegeAdminInformationRO);
}
