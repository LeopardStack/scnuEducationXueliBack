package com.scnujxjy.backendpoint.dao.mapper.basic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import org.apache.ibatis.annotations.Delete;
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

    /**
     * 根据学院和年级查询用户
     * @return 满足条件的平台用户信息集合，类型为 PlatformUserPO
     */
    @Select("SELECT * FROM platform_user WHERE username IN (SELECT id_number FROM student_status WHERE college = #{college} " +
            "AND grade = #{grade})")
    List<PlatformUserPO> selectUsersByCollegeAndGrade(String grade, String college);

    /**
     * 根据学院和年级删除用户
     * @return 删除的用户数量
     */
    @Delete("DELETE FROM platform_user WHERE username IN (SELECT id_number FROM student_status WHERE college = #{college} " +
            "AND grade = #{grade})")
    int deleteUsersByCollegeAndGrade(String grade, String college);

    /**
     * 检查用户名是否在数据库中存在
     * @param username 用户名
     * @return 如果存在返回 true，否则返回 false
     */
    @Select("SELECT COUNT(*) FROM platform_user WHERE username = #{username}")
    boolean existsByUsername(String username);

    /**
     * 根据用户名返回 user_id
     * @param username 用户名
     * @return user_id
     */
    @Select("SELECT user_id FROM platform_user WHERE username = #{username}")
    long getUserIdByUsername(String username);

    /**
     * 根据学生的身份证号码删除其账户信息
     * @param idNumber 身份证号码
     * @return 删除的记录数
     */
    @Delete("DELETE FROM platform_user WHERE username = #{idNumber}")
    int deleteStudentByIdNumber(String idNumber);

    @Select("SELECT  1 FROM platform_user;")
    void healthCheck();
}
