package com.scnujxjy.backendpoint.service.teaching_process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationCommendation;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationCommendationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationSelectArgs;
import com.scnujxjy.backendpoint.util.excelTemplate.StudentAward;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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


    @Resource
    private StudentAward studentAward;

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

    /**
     * 根据学生 学号获取评优成绩表所需信息
     *
     * @param studentId
     * @return
     */
    public ByteArrayOutputStream exportAwardData(String studentId) {
        List<ScoreInformationCommendation> scoreInformationCommendations = getBaseMapper().scoreInformationAward(studentId);
        ScoreInformationCommendationVO scoreInformationCommendationVO = new ScoreInformationCommendationVO();

        List<ScoreInformationCommendationVO.ScoreInfo> scoreInfoList = new ArrayList<>();
        for(ScoreInformationCommendation scoreInformationCommendation: scoreInformationCommendations){
            BeanUtils.copyProperties(scoreInformationCommendation, scoreInformationCommendationVO);

            ScoreInformationCommendationVO.ScoreInfo scoreInfo = scoreInformationCommendationVO.new ScoreInfo();

            BeanUtils.copyProperties(scoreInformationCommendation, scoreInfo);
            scoreInfoList.add(scoreInfo);
        }
        scoreInformationCommendationVO.setScoreInfoList(scoreInfoList);

        return studentAward.generateExcel(scoreInformationCommendationVO);
    }
}
