package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInformationSelectArgsManagerZero extends  CourseInformationSelectArgs{
    /**
     * 学院名称
     */
    private List<String> collegeNames;
}
