package com.scnujxjy.backendpoint.dao.mapper.basic;

import com.scnujxjy.backendpoint.dao.entity.basic.AdminInfoPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 管理员信息表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-15
 */
public interface AdminInfoMapper extends BaseMapper<AdminInfoPO> {

    @Select("select distinct department from admin_info;")
    List<String> getAllDepartments();

}
