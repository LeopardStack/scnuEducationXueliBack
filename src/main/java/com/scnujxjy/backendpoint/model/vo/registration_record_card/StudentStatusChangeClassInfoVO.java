package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentStatusChangeClassInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 班级名称
     */
    private String label;

    /**
     * 班级标识
     */
    private String Value;
}
