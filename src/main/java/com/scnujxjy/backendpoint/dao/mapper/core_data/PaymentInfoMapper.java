package com.scnujxjy.backendpoint.dao.mapper.core_data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.vo.core_data.NewStudentNotPayExcelVO;
import com.scnujxjy.backendpoint.model.vo.core_data.NewStudentPaymentInfoExcelVO;
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

    List<String> getDistinctGrades(@Param("entity") PaymentInfoFilterRO entity);


    /**
     * 获取缴费的层次筛选参数
     *
     * @param entity
     * @return
     */
    List<String> getDistinctLevels(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 获取缴费的年级筛选参数
     *
     * @param entity
     * @return
     */
    List<String> getDistinctStudyForms(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 获取缴费的年级筛选参数
     *
     * @param entity
     * @return
     */
    List<String> getDistinctClassNames(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 获取缴费的教学点筛选参数
     *
     * @param entity
     * @return
     */
    List<String> getDistinctTeachingPoints(@Param("entity") PaymentInfoFilterRO entity);


    /**
     * 获取缴费的学院筛选参数
     *
     * @param entity
     * @return
     */
    List<String> getDistinctCollegeNames(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 学年选择
     *
     * @param entity
     * @return
     */
    List<String> getDistinctAcademicYears(@Param("entity") PaymentInfoFilterRO entity);

    /**
     * 专业名称选择
     * @param filter
     * @return
     */
    List<String> getDistinctMajorNames(@Param("entity")PaymentInfoFilterRO filter);

    /**
     * 批量下载缴费数据
     *
     * @param entity
     * @return
     */
    List<PaymentInfoAllVO> downloadPaymentInfoDataByManager0(@Param("entity") PaymentInfoFilterRO entity);

    @Update("TRUNCATE TABLE payment_info")
    void truncateTable();

    List<String> getDistinctRemarks(@Param("entity") PaymentInfoFilterRO filter);

    List<PaymentInfoVO> getNewStudentPayInfoByFilter(@Param("entity") PaymentInfoFilterRO entity,
                                                     @Param("pageSize") Long pageSize, @Param("l") long l);

    List<NewStudentPaymentInfoExcelVO> exportNewStudentPayInfoByFilter(@Param("entity") PaymentInfoFilterRO entity);

    long getCountNewStudentPayInfoByFilter(@Param("entity")PaymentInfoFilterRO entity);

    List<String> getDistinctNewStudentGrades(@Param("entity")PaymentInfoFilterRO filter);

    List<String> getDistinctNewStudentLevels(@Param("entity")PaymentInfoFilterRO filter);

    List<String> getDistinctNewStudentStudyForms(@Param("entity")PaymentInfoFilterRO filter);

    List<String> getDistinctNewStudentTeachingPoints(@Param("entity")PaymentInfoFilterRO filter);

    List<String> getDistinctNewStudentCollegeNames(@Param("entity")PaymentInfoFilterRO filter);

    List<NewStudentNotPayExcelVO> exportNewStudentNotPayInfoByFilter(@Param("entity")PaymentInfoFilterRO entity);

    List<String> getDistinctNewStudentMajorNames(@Param("entity")PaymentInfoFilterRO filter);


}
