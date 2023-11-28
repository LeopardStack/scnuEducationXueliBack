package com.scnujxjy.backendpoint.dao.entity.pay;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 收费项管理表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fee_item_management")
public class FeeItemManagementPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    private String year;

    /**
     * 收费项名称
     */
    private String feeItemName;

    /**
     * 费用类型
     */
    private String feeType;

    /**
     * 办学类型（成教）
     */
    private String schoolType;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 收费批次号
     */
    private String feeBatchNo;

    /**
     * 收费开关
     */
    private Boolean feeSwitch;

    /**
     * 秘钥
     */
    private String secretKey;


}
