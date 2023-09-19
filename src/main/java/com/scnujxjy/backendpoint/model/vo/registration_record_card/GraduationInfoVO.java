package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class GraduationInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 自增主键
     */
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

    /**
     * 毕业日期
     */
    private Date graduationDate;
}
