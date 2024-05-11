package com.scnujxjy.backendpoint.model.vo.core_data;

import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherSelectVO {
    private String label;

    private Long value;
}
