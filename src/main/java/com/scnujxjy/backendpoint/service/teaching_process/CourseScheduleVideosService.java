package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseScheduleVideosPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleVideosMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 排课表视频信息表 服务类
 *
 * @author 谢辉龙
 * @since 2023-10-30
 */
@Service
@Slf4j
public class CourseScheduleVideosService extends ServiceImpl<CourseScheduleVideosMapper, CourseScheduleVideosPO> implements IService<CourseScheduleVideosPO> {

}
