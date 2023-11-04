package com.scnujxjy.backendpoint.model.bo.teaching_process;

import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseScheduleTime implements Comparable<CourseScheduleTime>{
    private Date teachingDate;

    private String teachingTime;

    private ScnuXueliTools scnuXueliTools = new ScnuXueliTools();

    public CourseScheduleTime(Date teachingDate, String teachingTime) {
        this.teachingDate = teachingDate;
        this.teachingTime = teachingTime;
    }

    @Override
    public int compareTo(CourseScheduleTime other) {
        // 首先比较日期
        int dateCompare = this.teachingDate.compareTo(other.teachingDate);
        if (dateCompare != 0) {
            return dateCompare;
        }

        // 如果日期相同，比较时间
        try {
            Date thisStartTime = scnuXueliTools.getTimeInterval(this.getTeachingDate(), this.getTeachingTime()).getStart();
            Date otherStartTime = scnuXueliTools.getTimeInterval(other.getTeachingDate(), other.getTeachingTime()).getStart();
            return thisStartTime.compareTo(otherStartTime);
        } catch (Exception e) {
            throw new IllegalArgumentException("排课表日期时间格式有误 无法比较大小" + e.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseScheduleTime that = (CourseScheduleTime) o;
        return Objects.equals(teachingDate, that.teachingDate) &&
                Objects.equals(teachingTime, that.teachingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teachingDate, teachingTime);
    }
}
