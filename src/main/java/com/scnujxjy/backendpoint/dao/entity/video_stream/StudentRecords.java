package com.scnujxjy.backendpoint.dao.entity.video_stream;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_records")
public class StudentRecords {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String studentNumber;

    private Integer watched;

    private Integer videoId;



}
