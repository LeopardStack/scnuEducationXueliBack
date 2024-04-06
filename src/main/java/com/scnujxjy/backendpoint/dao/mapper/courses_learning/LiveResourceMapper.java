package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.LiveResourcesPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
public interface LiveResourceMapper extends BaseMapper<LiveResourcesPO> {

    @Update("TRUNCATE TABLE live_resources")
    void truncateTable();

    @Select("select * from live_resources where course_id =#{courseId} limit 1")
    LiveResourcesPO query(Long courseId);

    @Select("select * from live_resources where channel_id =#{channelId} limit 1")
    LiveResourcesPO queryCourseId(String channelId);
    /**
     * 根据课程 ID 以及 SectionID 为空 查找该门课的直播资源
     * @param courseId
     * @return
     */
    LiveResourcesPO selectLiveResource(Long courseId);
}
