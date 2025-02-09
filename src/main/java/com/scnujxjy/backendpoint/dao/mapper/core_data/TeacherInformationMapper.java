package com.scnujxjy.backendpoint.dao.mapper.core_data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRequest;
import com.scnujxjy.backendpoint.model.ro.courses_learning.TeacherInformationSearchRO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 教师信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface TeacherInformationMapper extends BaseMapper<TeacherInformationPO> {

    /**
     * 根据 身份证号码查找教师
     *
     * @param idCardNumber 身份证号码
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM teacher_information WHERE id_card_number = #{idCardNumber}")
    List<TeacherInformationPO> selectByIdCardNumber(String idCardNumber);

    /**
     * 根据 工号查找教师
     *
     * @param workNumber 工号
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM teacher_information WHERE work_number = #{workNumber}")
    List<TeacherInformationPO> selectByWorkNumber(String workNumber);

    /**
     * 根据 手机号码查找教师
     *
     * @param phone 手机号码
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM teacher_information WHERE phone = #{phone}")
    List<TeacherInformationPO> selectByPhone(String phone);

    @Select("SELECT * FROM teacher_information WHERE teacher_username = #{username} limit 1")
    TeacherInformationPO selectByUserName(String username);

    /**
     * 根据 姓名查找教师
     *
     * @param name 姓名
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM teacher_information WHERE name = #{name}")
    List<TeacherInformationPO> selectByName(String name);

    @Select("SELECT * FROM teacher_information WHERE name = #{name} and  work_number = #{workNumber} limit 1")
    TeacherInformationPO selectByNameAndWorkNumber(String name, String workNumber);

    @Select("SELECT * FROM teacher_information WHERE teacher_username = #{name}  limit 1")
    TeacherInformationPO selectByTeacherUserName(String name);

    /**
     * 根据 user_id 更新 teacher_username
     *
     * @param userId          教师的 user_id
     * @param teacherUsername 新的 teacher_username
     */
    @Update("UPDATE teacher_information SET teacher_username = #{teacherUsername} WHERE user_id = #{userId}")
    void updateTeacherUsernameByUserId(@Param("userId") Long userId, @Param("teacherUsername") String teacherUsername);


    List<TeacherInformationPO> selectTeacherInfo(@Param("entity") TeacherInformationSearchRO teacherInformationSearchRO);

    List<TeacherInformationPO> selectTeacherInformation(TeacherInformationRequest teacherInformationRequest);

    Long selectTeacherInformationCount(TeacherInformationRequest teacherInformationRequeste);

    List<TeacherInformationVO> selectTeacherInformationWithAccountInfo(@Param("entity") TeacherInformationRO entity,
                                                                       @Param("pageSize") Long pageSize, @Param("l") long l);

    Long selectTeacherInformationWithAccountInfoCount(@Param("entity") TeacherInformationRO entity);

    Set<String> getDistincetTeacherNames(@Param("entity") TeacherInformationRO teacherInformationRO);

    @Update("UPDATE teacher_information SET user_id = #{newUserId} WHERE teacher_username = #{username}")
    int updateTeacherUserId(@Param("username") String username, @Param("newUserId") Long newUserId);
}
