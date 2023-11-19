package com.scnujxjy.backendpoint.model.ro.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * 单个设置考试的命题人和阅卷助教
 * @author leopard
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SingleSetTeachersInfoRO {
    /**
     * 主讲教师 userId int 类型
     */
    String mainTeacher;

    /**
     * 助教 userId int 类型
     */
    List<String> assistants;

    /**
     * 考试信息表 ID
     */
    String id;
}
