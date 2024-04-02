package com.scnujxjy.backendpoint.model.vo.core_data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class TeacherQueryArgsVO {
    Set<String> names;

    List<String> teacherTypes;

}
