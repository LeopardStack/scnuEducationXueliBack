package com.scnujxjy.backendpoint.dao.mapper.core_data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import org.apache.ibatis.annotations.Select;

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
     * @param id_card_number 学生的身份证号码
     * @return
     */
    @Select("SELECT * FROM payment_info WHERE id_card_number = #{id_card_number} ORDER BY payment_date DESC")
    List<PaymentInfoPO> getStudentPayInfo(String id_card_number);


}
