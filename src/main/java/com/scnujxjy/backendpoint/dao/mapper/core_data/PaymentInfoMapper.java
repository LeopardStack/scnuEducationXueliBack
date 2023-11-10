package com.scnujxjy.backendpoint.dao.mapper.core_data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoAllVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 缴费信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface PaymentInfoMapper extends BaseMapper<PaymentInfoPO> {

    /**
     * 获取学生的缴费信息 按照日期排序
     *
     * @param id_card_number 学生的身份证号码
     * @return
     */
    @Select("SELECT * FROM payment_info WHERE id_card_number = #{id_card_number} ORDER BY payment_date DESC")
    List<PaymentInfoPO> getStudentPayInfo(String id_card_number);


    /**
     * 根据筛选条件获取缴费信息
     *
     * @param entity   筛选条件
     * @param pageSize 筛选条件
     * @param l        筛选条件
     * @return
     */
    List<PaymentInfoVO> getStudentPayInfoByFilter(@Param("entity") PaymentInfoFilterRO entity,
                                                  @Param("pageSize") Long pageSize, @Param("l") long l);

    /**
     * 根据筛选条件获取缴费信息
     *
     * @param entity   筛选条件
     * @param pageSize 筛选条件
     * @param l        筛选条件
     * @return
     */
    List<PaymentInfoVO> getTeachingPointStudentPayInfoByFilter(@Param("entity") PaymentInfoFilterRO entity,
                                                               @Param("pageSize") Long pageSize, @Param("l") long l);

    Long getTeachingPointStudentPayInfoByFilterCount(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 去掉这个排序操作 SQL 会快很多
     * "ORDER BY ss.grade DESC, ss.college, ss.major_name, ss.level, ci.class_name " +
     */

    /**
     * 获取学费总数
     *
     * @param entity
     * @return
     */
    long getCountStudentPayInfoByFilter(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 获取缴费的年级筛选参数
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.grade " +
            "FROM payment_info pi " +
            "LEFT JOIN student_status ss ON ss.student_number = pi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND pi.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.admissionNumber != null'>AND pi.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.name != null'>AND pi.name = #{entity.name} </if>" +
            "<if test='entity.idCardNumber != null'>AND pi.id_card_number = #{entity.idCardNumber} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +

            "<if test='entity.paymentBeginDate != null'>AND pi.payment_date >= #{entity.paymentBeginDate} </if>" +
            "<if test='entity.paymentEndDate != null'>AND pi.payment_date &lt;= #{entity.paymentEndDate} </if>" +
            "<if test='entity.paymentDate != null'>AND pi.payment_date = #{entity.paymentDate} </if>" +

            "<if test='entity.paymentCategory != null'>AND pi.payment_category = #{entity.paymentCategory} </if>" +
            "<if test='entity.academicYear != null'>AND pi.academic_year = #{entity.academicYear} </if>" +
            "<if test='entity.paymentType != null'>AND pi.payment_type = #{entity.paymentType} </if>" +
            "<if test='entity.isPaid != null'>AND pi.is_paid = #{entity.isPaid} </if>" +
            "<if test='entity.paymentMethod != null'>AND pi.payment_method = #{entity.paymentMethod} </if>" +
            "<if test='entity.college != null'>AND ci.college = #{entity.college} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "</script>")
    List<String> getDistinctGrades(@Param("entity") PaymentInfoFilterRO entity);


    /**
     * 获取缴费的层次筛选参数
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.level " +
            "FROM payment_info pi " +
            "LEFT JOIN student_status ss ON ss.student_number = pi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND pi.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.admissionNumber != null'>AND pi.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.name != null'>AND pi.name = #{entity.name} </if>" +
            "<if test='entity.idCardNumber != null'>AND pi.id_card_number = #{entity.idCardNumber} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +

            "<if test='entity.paymentBeginDate != null'>AND pi.payment_date >= #{entity.paymentBeginDate} </if>" +
            "<if test='entity.paymentEndDate != null'>AND pi.payment_date &lt;= #{entity.paymentEndDate} </if>" +
            "<if test='entity.paymentDate != null'>AND pi.payment_date = #{entity.paymentDate} </if>" +

            "<if test='entity.paymentCategory != null'>AND pi.payment_category = #{entity.paymentCategory} </if>" +
            "<if test='entity.academicYear != null'>AND pi.academic_year = #{entity.academicYear} </if>" +
            "<if test='entity.paymentType != null'>AND pi.payment_type = #{entity.paymentType} </if>" +
            "<if test='entity.isPaid != null'>AND pi.is_paid = #{entity.isPaid} </if>" +
            "<if test='entity.paymentMethod != null'>AND pi.payment_method = #{entity.paymentMethod} </if>" +
            "<if test='entity.college != null'>AND ci.college = #{entity.college} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "</script>")
    List<String> getDistinctLevels(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 获取缴费的年级筛选参数
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.study_form " +
            "FROM payment_info pi " +
            "LEFT JOIN student_status ss ON ss.student_number = pi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND pi.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.admissionNumber != null'>AND pi.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.name != null'>AND pi.name = #{entity.name} </if>" +
            "<if test='entity.idCardNumber != null'>AND pi.id_card_number = #{entity.idCardNumber} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +

            "<if test='entity.paymentBeginDate != null'>AND pi.payment_date >= #{entity.paymentBeginDate} </if>" +
            "<if test='entity.paymentEndDate != null'>AND pi.payment_date &lt;= #{entity.paymentEndDate} </if>" +
            "<if test='entity.paymentDate != null'>AND pi.payment_date = #{entity.paymentDate} </if>" +

            "<if test='entity.paymentCategory != null'>AND pi.payment_category = #{entity.paymentCategory} </if>" +
            "<if test='entity.academicYear != null'>AND pi.academic_year = #{entity.academicYear} </if>" +
            "<if test='entity.paymentType != null'>AND pi.payment_type = #{entity.paymentType} </if>" +
            "<if test='entity.isPaid != null'>AND pi.is_paid = #{entity.isPaid} </if>" +
            "<if test='entity.paymentMethod != null'>AND pi.payment_method = #{entity.paymentMethod} </if>" +
            "<if test='entity.college != null'>AND ci.college = #{entity.college} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "</script>")
    List<String> getDistinctStudyForms(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 获取缴费的年级筛选参数
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.class_name " +
            "FROM payment_info pi " +
            "LEFT JOIN student_status ss ON ss.student_number = pi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND pi.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.admissionNumber != null'>AND pi.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.name != null'>AND pi.name = #{entity.name} </if>" +
            "<if test='entity.idCardNumber != null'>AND pi.id_card_number = #{entity.idCardNumber} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +

            "<if test='entity.paymentBeginDate != null'>AND pi.payment_date >= #{entity.paymentBeginDate} </if>" +
            "<if test='entity.paymentEndDate != null'>AND pi.payment_date &lt;= #{entity.paymentEndDate} </if>" +
            "<if test='entity.paymentDate != null'>AND pi.payment_date = #{entity.paymentDate} </if>" +

            "<if test='entity.paymentCategory != null'>AND pi.payment_category = #{entity.paymentCategory} </if>" +
            "<if test='entity.academicYear != null'>AND pi.academic_year = #{entity.academicYear} </if>" +
            "<if test='entity.paymentType != null'>AND pi.payment_type = #{entity.paymentType} </if>" +
            "<if test='entity.isPaid != null'>AND pi.is_paid = #{entity.isPaid} </if>" +
            "<if test='entity.paymentMethod != null'>AND pi.payment_method = #{entity.paymentMethod} </if>" +
            "<if test='entity.college != null'>AND ci.college = #{entity.college} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "</script>")
    List<String> getDistinctClassNames(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 获取缴费的教学点筛选参数
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT REGEXP_REPLACE(ci.class_name, '[0-9]', '') AS cleaned_class_name " +
            "FROM payment_info pi " +
            "LEFT JOIN student_status ss ON ss.student_number = pi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND pi.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.admissionNumber != null'>AND pi.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.name != null'>AND pi.name = #{entity.name} </if>" +
            "<if test='entity.idCardNumber != null'>AND pi.id_card_number = #{entity.idCardNumber} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +

            "<if test='entity.paymentBeginDate != null'>AND pi.payment_date >= #{entity.paymentBeginDate} </if>" +
            "<if test='entity.paymentEndDate != null'>AND pi.payment_date &lt;= #{entity.paymentEndDate} </if>" +
            "<if test='entity.paymentDate != null'>AND pi.payment_date = #{entity.paymentDate} </if>" +

            "<if test='entity.paymentCategory != null'>AND pi.payment_category = #{entity.paymentCategory} </if>" +
            "<if test='entity.academicYear != null'>AND pi.academic_year = #{entity.academicYear} </if>" +
            "<if test='entity.paymentType != null'>AND pi.payment_type = #{entity.paymentType} </if>" +
            "<if test='entity.isPaid != null'>AND pi.is_paid = #{entity.isPaid} </if>" +
            "<if test='entity.paymentMethod != null'>AND pi.payment_method = #{entity.paymentMethod} </if>" +
            "<if test='entity.college != null'>AND ci.college = #{entity.college} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "</script>")
    List<String> getDistinctTeachingPoints(@Param("entity") PaymentInfoFilterRO entity);


    /**
     * 获取缴费的学院筛选参数
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.college " +
            "FROM payment_info pi " +
            "LEFT JOIN student_status ss ON ss.student_number = pi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND pi.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.admissionNumber != null'>AND pi.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.name != null'>AND pi.name = #{entity.name} </if>" +
            "<if test='entity.idCardNumber != null'>AND pi.id_card_number = #{entity.idCardNumber} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +

            "<if test='entity.paymentBeginDate != null'>AND pi.payment_date >= #{entity.paymentBeginDate} </if>" +
            "<if test='entity.paymentEndDate != null'>AND pi.payment_date &lt;= #{entity.paymentEndDate} </if>" +
            "<if test='entity.paymentDate != null'>AND pi.payment_date = #{entity.paymentDate} </if>" +

            "<if test='entity.paymentCategory != null'>AND pi.payment_category = #{entity.paymentCategory} </if>" +
            "<if test='entity.academicYear != null'>AND pi.academic_year = #{entity.academicYear} </if>" +
            "<if test='entity.paymentType != null'>AND pi.payment_type = #{entity.paymentType} </if>" +
            "<if test='entity.isPaid != null'>AND pi.is_paid = #{entity.isPaid} </if>" +
            "<if test='entity.paymentMethod != null'>AND pi.payment_method = #{entity.paymentMethod} </if>" +
            "<if test='entity.college != null'>AND ci.college = #{entity.college} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "</script>")
    List<String> getDistinctCollegeNames(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 学年选择
     *
     * @param entity
     * @return
     */
    List<String> getDistinctAcademicYears(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 批量下载缴费数据
     *
     * @param entity
     * @return
     */
    List<PaymentInfoAllVO> downloadPaymentInfoDataByManager0(@Param("entity") PaymentInfoFilterRO entity);

    @Update("TRUNCATE TABLE payment_info")
    void truncateTable();

}
