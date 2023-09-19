package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 成绩信息表 服务类
 * </p>
 *
 * @author leopard
 * @since 2023-09-10
 */
@Service
@Slf4j
public class ScoreInformationService extends ServiceImpl<ScoreInformationMapper, ScoreInformationPO> implements IService<ScoreInformationPO> {

}
