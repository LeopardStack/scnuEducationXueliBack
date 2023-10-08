package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationSelectArgs;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
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

    /**
     * 根据筛选参数 和角色获取成绩信息
     * @param scoreInformationFilterROPageRO 成绩筛选参数
     * @param filter 角色参数
     * @return
     */
    public FilterDataVO allPageQueryGradinfoFilter(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO, AbstractFilter filter) {
        return filter.filterGradeInfo(scoreInformationFilterROPageRO);
    }

    /**
     *
     * @param loginId
     * @param filter
     * @return
     */
    public ScoreInformationSelectArgs getStudentStatusArgs(String loginId, AbstractFilter filter) {
        return filter.filterScoreInformationSelectArgs();

    }
}
