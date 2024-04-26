package com.scnujxjy.backendpoint.dao.mapper.basic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface PlatformUserMapper extends BaseMapper<PlatformUserPO> {

    /**
     * 根据学院和年级查询用户
     *
     * @return 满足条件的平台用户信息集合，类型为 PlatformUserPO
     */
    @Select("SELECT * FROM platform_user WHERE username IN (SELECT id_number FROM student_status WHERE college = #{college} " +
            "AND grade = #{grade})")
    List<PlatformUserPO> selectUsersByCollegeAndGrade(String grade, String college);

    /**
     * 根据学院和年级删除用户
     *
     * @return 删除的用户数量
     */
    @Delete("DELETE FROM platform_user WHERE username IN (SELECT id_number FROM student_status WHERE college = #{college} " +
            "AND grade = #{grade})")
    int deleteUsersByCollegeAndGrade(String grade, String college);


    @Select("SELECT 1")
    Long healthCheck();

    /**
     * 根据userId更新数据
     * <p>目前只更新补充角色id名单</p>
     *
     * @param platformUserPO
     * @return
     */
    Integer updateUser(@Param("platformUserPO") PlatformUserPO platformUserPO);

    /**
     * 批量查询用户信息
     *
     * @param platformUserRO
     * @return
     */
    List<PlatformUserPO> selectPlatformUserList(@Param("entity") PlatformUserRO platformUserRO);
}
