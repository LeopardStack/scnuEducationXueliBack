package com.scnujxjy.backendpoint.service.courses_learning;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.SectionsPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.VideoLearningStatisticsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.SectionsMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.VideoLearningStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
@Service
@Slf4j
public class VideoLearningStatisticsService extends ServiceImpl<VideoLearningStatisticsMapper, VideoLearningStatisticsPO>
        implements IService<VideoLearningStatisticsPO> {

}
