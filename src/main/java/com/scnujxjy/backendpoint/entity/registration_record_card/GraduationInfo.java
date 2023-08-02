package com.scnujxjy.backendpoint.entity.registration_record_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 毕业信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GraduationInfo implements Serializable {

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
     * 毕业论文ID
     */
    private Long thesisId;

    /**
     * 毕业照片
     */
    private String graduationPhoto;

    /**
     * 毕业证号
     */
    private String graduationNumber;

    /**
     * 文号
     */
    private String documentNumber;


}
