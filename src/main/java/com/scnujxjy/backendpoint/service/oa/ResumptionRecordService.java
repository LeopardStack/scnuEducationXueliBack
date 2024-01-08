package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.MajorChangeRecordPO;
import com.scnujxjy.backendpoint.dao.entity.oa.ResumptionRecordPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.oa.SuspensionRecordPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.oa.MajorChangeRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.ResumptionRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.SuspensionRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.ResumptionRecordRO;
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
 * 复学记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class ResumptionRecordService extends ServiceImpl<ResumptionRecordMapper, ResumptionRecordPO> implements IService<ResumptionRecordPO> {

    @Resource
    SuspensionRecordMapper suspensionRecordMapper;
    @Resource
    ClassInformationMapper classInformationMapper;

    public ResumptionRecordSelectArgs getResumptionInfosSelectArgs(ResumptionRecordRO entity) {
        ResumptionRecordSelectArgs resumptionRecordSelectArgs = new ResumptionRecordSelectArgs();

        ExecutorService executor = Executors.newFixedThreadPool(1); // 1 代表你有1个查询

        Future<List<String>> distinctOldGradesFuture = executor.submit(() ->
                getBaseMapper().getDistinctGrades(entity));

        try {
            resumptionRecordSelectArgs.setGrades(distinctOldGradesFuture.get());


        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return resumptionRecordSelectArgs;
    }

    public PageVO<ResumptionRecordVO> getResumptionInfos(PageRO<ResumptionRecordRO> resumptionRecordROPageRO) {
        List<ResumptionRecordVO> resumptionRecordVOS = getBaseMapper().getRetentionInfos(resumptionRecordROPageRO.getEntity(),
                (resumptionRecordROPageRO.getPageNumber()-1) * resumptionRecordROPageRO.getPageSize(),
                resumptionRecordROPageRO.getPageSize());
        long count = getBaseMapper().getRetentionInfosCount(resumptionRecordROPageRO.getEntity());
        PageVO<ResumptionRecordVO> pageVO = new PageVO<ResumptionRecordVO>();
        pageVO.setRecords(resumptionRecordVOS);
        pageVO.setTotal(count);
        pageVO.setCurrent(resumptionRecordROPageRO.getPageNumber());
        return pageVO;
    }

    public ResumptionWithSuspensionVO getSuspensionInfo(ResumptionRecordRO resumptionRecordRO) {
        ResumptionWithSuspensionVO resumptionWithSuspensionVO = new ResumptionWithSuspensionVO();


        ResumptionRecordPO resumptionRecordPO = getBaseMapper().selectOne(new LambdaQueryWrapper<ResumptionRecordPO>()
                .eq(ResumptionRecordPO::getId, resumptionRecordRO.getId()));
        ResumptionRecordWithClassInfoVO resumptionRecordVO = new ResumptionRecordWithClassInfoVO();
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



        BeanUtils.copyProperties(resumptionRecordPO, resumptionRecordVO);
        resumptionWithSuspensionVO.setResumptionRecordVO(resumptionRecordVO);

        List<SuspensionRecordWithClassInfoVO> suspensionRecordVOList = new ArrayList<>();

        String remarkSerialNumber = resumptionRecordPO.getSuspensionSerialNumber();
        if(!StringUtils.isBlank(remarkSerialNumber)){
            log.info("不为空才去查询它的相关信息 " + remarkSerialNumber);
            String replace = remarkSerialNumber.replace("休学", "");
            String grade = replace.substring(0, 4);
            int serialNumber = Integer.parseInt(replace.substring(4));  // Convert the serial number part to int to remove leading zeros

            List<SuspensionRecordPO> suspensionRecordPOS = suspensionRecordMapper.
                    selectList(new LambdaQueryWrapper<SuspensionRecordPO>()
                            .eq(SuspensionRecordPO::getSerialNumber, serialNumber)
                            .eq(SuspensionRecordPO::getCurrentYear, grade)
                    );
            for(SuspensionRecordPO suspensionRecordPO : suspensionRecordPOS){
                SuspensionRecordWithClassInfoVO suspensionRecordVO = new SuspensionRecordWithClassInfoVO();
                ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, suspensionRecordPO.getOldClassIdentifier()));
                BeanUtils.copyProperties(classInformationPO, suspensionRecordVO);

                BeanUtils.copyProperties(suspensionRecordPO, suspensionRecordVO);
                suspensionRecordVOList.add(suspensionRecordVO);
            }
            log.info("\n获取到的休学信息如下 " + suspensionRecordVOList);
        }

        resumptionWithSuspensionVO.setSuspensionRecordVOList(suspensionRecordVOList);
        return resumptionWithSuspensionVO;
    }
}
