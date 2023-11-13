package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import lombok.Data;

import java.util.Objects;
import java.util.Set;

@Data
public class BatchInfo {

    private String teacherName;
    private String courseName;
    private Set<String> classes;

    public BatchInfo(String teacherName, String courseName, Set<String> classes) {
        this.teacherName = teacherName;
        this.courseName = courseName;
        this.classes = classes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchInfo batchInfo = (BatchInfo) o;
        return Objects.equals(teacherName, batchInfo.teacherName) &&
                Objects.equals(courseName, batchInfo.courseName) &&
                Objects.equals(classes, batchInfo.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teacherName, courseName, classes);
    }

}
