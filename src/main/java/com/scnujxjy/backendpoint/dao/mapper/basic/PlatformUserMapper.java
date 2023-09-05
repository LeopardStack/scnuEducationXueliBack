package com.scnujxjy.backendpoint.dao.mapper.basic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface PlatformUserMapper extends BaseMapper<PlatformUserPO> {
    /**
     * 根据 用户名查找用户
     * @param username 用户名
     * @return 平台用户信息集合，类型为 PlatformUserPO
     */
    @Select("SELECT * FROM platform_user WHERE username = #{username}")
    List<PlatformUserPO> selectPlatformUsers1(String username);
}
