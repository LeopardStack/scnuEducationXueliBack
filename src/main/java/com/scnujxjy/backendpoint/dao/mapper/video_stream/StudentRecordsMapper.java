package com.scnujxjy.backendpoint.dao.mapper.video_stream;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.video_stream.StudentRecords;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentRecordsMapper  extends BaseMapper<StudentRecords> {

    @Select("select * from student_records where student_number=#{studentNumber} and video_id=#{videoId} limit 1")
    StudentRecords selectByNumberAndVideoId(String studentNumber,Integer videoId);

    @Select("select video_id from student_records where student_number=#{studentNumber}")
    List<Long> selectAllVideos(String studentNumber);

}
