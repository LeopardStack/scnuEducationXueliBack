package com.scnujxjy.backendpoint.dao.entity.registration_record_card;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 学位信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DegreeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年级
     */
    private String grade;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 学位外语成绩
     */
    private String foreignLanguageScore;

    /**
     * 学位外语科目
     */
    private String foreignLanguageSubject;

    /**
     * 学位外语通过状态
     */
    private String foreignLanguageStatus;

    /**
     * 学位外语通过日期
     */
    private Date foreignLanguageDate;

    /**
     * 学位类型
     */
    private String degreeType;

    /**
     * 学位授予日期
     */
    private Date degreeDate;

    /**
     * 学位证号
     */
    private String degreeNumber;

    /**
     * 文号
     */
    private String documentNumber;


}
