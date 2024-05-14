package com.scnujxjy.backendpoint.service.office_automation.approval_result;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval_result.ResumptionRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval_result.SuspensionRecordPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval_result.ResumptionRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval_result.SuspensionRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.SuspensionRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.oa.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>
 * 休学记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class SuspensionRecordService extends ServiceImpl<SuspensionRecordMapper, SuspensionRecordPO> implements IService<SuspensionRecordPO> {

    @Resource
    ResumptionRecordMapper resumptionRecordMapper;

    @Resource
    ClassInformationMapper classInformationMapper;

    public PageVO<SuspensionRecordVO> getSuspensionInfos(PageRO<SuspensionRecordRO> suspensionRecordROPageRO) {
        List<SuspensionRecordVO> suspensionRecordVOS = getBaseMapper().getRetentionInfos(suspensionRecordROPageRO.getEntity(),
                (suspensionRecordROPageRO.getPageNumber()-1) * suspensionRecordROPageRO.getPageSize(),
                suspensionRecordROPageRO.getPageSize());
        long count = getBaseMapper().getRetentionInfosCount(suspensionRecordROPageRO.getEntity());
        PageVO<SuspensionRecordVO> pageVO = new PageVO<SuspensionRecordVO>();
        pageVO.setRecords(suspensionRecordVOS);
        pageVO.setTotal(count);
        pageVO.setCurrent(suspensionRecordROPageRO.getPageNumber());
        return pageVO;
    }

    public SuspensionSelectArgs getSuspensionInfosSelectArgs(SuspensionRecordRO entity) {
        SuspensionSelectArgs suspensionSelectArgs = new SuspensionSelectArgs();

        ExecutorService executor = Executors.newFixedThreadPool(1); // 1 代表你有1个查询

        Future<List<String>> distinctOldGradesFuture = executor.submit(() ->
                getBaseMapper().getDistinctGrades(entity));

        try {
            suspensionSelectArgs.setGrades(distinctOldGradesFuture.get());


        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return suspensionSelectArgs;
    }

    public SuspensionWithResumptionVO getResumptionInfo(SuspensionRecordRO suspensionRecordRO) {
        SuspensionWithResumptionVO suspensionWithResumptionVO = new SuspensionWithResumptionVO();


        SuspensionRecordPO suspensionRecordPO = getBaseMapper().selectOne(new LambdaQueryWrapper<SuspensionRecordPO>()
                .eq(SuspensionRecordPO::getId, suspensionRecordRO.getId()));
        SuspensionRecordWithClassInfoVO suspensionRecordVO = new SuspensionRecordWithClassInfoVO();
        BeanUtils.copyProperties(suspensionRecordPO, suspensionRecordVO);
        ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                .eq(ClassInformationPO::getClassIdentifier, suspensionRecordPO.getOldClassIdentifier()));
        BeanUtils.copyProperties(classInformationPO, suspensionRecordVO);
        suspensionWithResumptionVO.setSuspensionRecordVO(suspensionRecordVO);

        List<ResumptionRecordWithClassInfoVO> resumptionRecordVOList = new ArrayList<>();

        String remarkSerialNumber = suspensionRecordPO.getRemarkSerialNumber();
        if(!StringUtils.isBlank(remarkSerialNumber)){
            log.info("不为空才去查询它的相关信息 " + remarkSerialNumber);
            String replace = remarkSerialNumber.replace("复学", "");
            String grade = replace.substring(0, 4);
            int serialNumber = Integer.parseInt(replace.substring(4));  // Convert the serial number part to int to remove leading zeros

            List<ResumptionRecordPO> resumptionRecordPOS = resumptionRecordMapper.
                    selectList(new LambdaQueryWrapper<ResumptionRecordPO>()
                    .eq(ResumptionRecordPO::getSerialNumber, serialNumber)
                    .eq(ResumptionRecordPO::getCurrentYear, grade)
                    );
            for(ResumptionRecordPO resumptionRecordPO : resumptionRecordPOS){
                ResumptionRecordWithClassInfoVO resumptionRecordVO = new ResumptionRecordWithClassInfoVO();
                BeanUtils.copyProperties(resumptionRecordPO, resumptionRecordVO);
                ClassInformationPO classInformationPO1 = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, resumptionRecordPO.getOldClassIdentifier()));
                ClassInformationPO classInformationPO2 = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, resumptionRecordPO.getNewClassIdentifier()));
                BeanUtils.copyProperties(classInformationPO1, resumptionRecordVO);
                resumptionRecordVO.setNewClassName(classInformationPO2.getClassName());
                resumptionRecordVO.setNewGrade(classInformationPO2.getGrade());
                resumptionRecordVO.setNewCollege(classInformationPO2.getCollege());
                resumptionRecordVO.setNewMajorCode(classInformationPO2.getMajorCode());
                resumptionRecordVO.setNewTuition(classInformationPO2.getTuition());
                resumptionRecordVO.setNewMajorName(classInformationPO2.getMajorName());
                resumptionRecordVO.setNewStudyForm(classInformationPO2.getStudyForm());
                resumptionRecordVO.setNewStudentStatus(classInformationPO2.getStudentStatus());
                resumptionRecordVOList.add(resumptionRecordVO);
            }
            log.info("\n获取到的复学信息如下 " + resumptionRecordPOS);
        }

        suspensionWithResumptionVO.setResumptionRecordVOList(resumptionRecordVOList);
        return suspensionWithResumptionVO;
    }

    public SuspensionWithResumptionVO getSingleResumptionInfo(SuspensionRecordRO suspensionRecordRO) {
        SuspensionWithResumptionVO suspensionWithResumptionVO = new SuspensionWithResumptionVO();


        SuspensionRecordPO suspensionRecordPO = getBaseMapper().selectOne(new LambdaQueryWrapper<SuspensionRecordPO>()
                .eq(SuspensionRecordPO::getIdNumber, suspensionRecordRO.getIdNumber()));
        SuspensionRecordWithClassInfoVO suspensionRecordVO = new SuspensionRecordWithClassInfoVO();
        BeanUtils.copyProperties(suspensionRecordPO, suspensionRecordVO);
        ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                .eq(ClassInformationPO::getClassIdentifier, suspensionRecordPO.getOldClassIdentifier()));
        BeanUtils.copyProperties(classInformationPO, suspensionRecordVO);
        suspensionWithResumptionVO.setSuspensionRecordVO(suspensionRecordVO);

        List<ResumptionRecordWithClassInfoVO> resumptionRecordVOList = new ArrayList<>();

        String remarkSerialNumber = suspensionRecordPO.getRemarkSerialNumber();
        if(!StringUtils.isBlank(remarkSerialNumber)){
            log.info("不为空才去查询它的相关信息 " + remarkSerialNumber);
            String replace = remarkSerialNumber.replace("复学", "");
            String grade = replace.substring(0, 4);
            int serialNumber = Integer.parseInt(replace.substring(4));  // Convert the serial number part to int to remove leading zeros

            List<ResumptionRecordPO> resumptionRecordPOS = resumptionRecordMapper.
                    selectList(new LambdaQueryWrapper<ResumptionRecordPO>()
                            .eq(ResumptionRecordPO::getSerialNumber, serialNumber)
                            .eq(ResumptionRecordPO::getCurrentYear, grade)
                    );
            for(ResumptionRecordPO resumptionRecordPO : resumptionRecordPOS){
                ResumptionRecordWithClassInfoVO resumptionRecordVO = new ResumptionRecordWithClassInfoVO();
                BeanUtils.copyProperties(resumptionRecordPO, resumptionRecordVO);
                ClassInformationPO classInformationPO1 = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, resumptionRecordPO.getOldClassIdentifier()));
                ClassInformationPO classInformationPO2 = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, resumptionRecordPO.getNewClassIdentifier()));
                BeanUtils.copyProperties(classInformationPO1, resumptionRecordVO);
                resumptionRecordVO.setNewClassName(classInformationPO2.getClassName());
                resumptionRecordVO.setNewGrade(classInformationPO2.getGrade());
                resumptionRecordVO.setNewCollege(classInformationPO2.getCollege());
                resumptionRecordVO.setNewMajorCode(classInformationPO2.getMajorCode());
                resumptionRecordVO.setNewTuition(classInformationPO2.getTuition());
                resumptionRecordVO.setNewMajorName(classInformationPO2.getMajorName());
                resumptionRecordVO.setNewStudyForm(classInformationPO2.getStudyForm());
                resumptionRecordVO.setNewStudentStatus(classInformationPO2.getStudentStatus());
                resumptionRecordVOList.add(resumptionRecordVO);
            }
            log.info("\n获取到的复学信息如下 " + resumptionRecordPOS);
        }

        suspensionWithResumptionVO.setResumptionRecordVOList(resumptionRecordVOList);
        return suspensionWithResumptionVO;
    }
}
