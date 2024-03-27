package com.scnujxjy.backendpoint.model.ro.courses_learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author 谢辉龙
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TeacherInformationSearchRO {
    private Long id;

    // 老师用户名
    private String username;

    // 老师用户名
    private Set<String> usernames;

    // 老师姓名
    private String name;

    private String idNumber;

    private String workNumber;

}
