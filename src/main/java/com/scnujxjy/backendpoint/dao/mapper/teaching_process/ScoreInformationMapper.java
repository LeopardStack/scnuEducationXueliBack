package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 成绩信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-09-10
 */
public interface ScoreInformationMapper extends BaseMapper<ScoreInformationPO> {

    /**
     * 获取学生的成绩信息 按照年级和课程代码排序
     * @param id_number 学生的身份证号码
     * @return
     */
    @Select("SELECT g.* " +
            "FROM student_status s " +
            "JOIN score_information g ON s.student_number = g.student_id " +
            "WHERE s.id_number = #{id_number} " +
            "ORDER BY s.grade DESC, g.course_code ASC")
    List<ScoreInformationPO> getGradeInfo(String id_number);
}
