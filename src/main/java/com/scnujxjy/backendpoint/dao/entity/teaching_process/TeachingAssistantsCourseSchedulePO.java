package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Objects;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 存储助教用户名和批次ID的表，批次ID代表教师、课程以及合班的班级
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("teaching_assistants_course_schedule")
public class TeachingAssistantsCourseSchedulePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 助教用户名
     */
    private String username;

    /**
     * 批次ID，代表教师、课程以及合班的班级
     */
    private Long batchId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TeachingAssistantsCourseSchedulePO that = (TeachingAssistantsCourseSchedulePO) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(batchId, that.batchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, batchId);
    }


}
