package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.scnujxjy.backendpoint.constant.enums.StudentStatusChangeEnum;
import com.scnujxjy.backendpoint.dao.entity.oa.DropoutRecordPO;
import com.scnujxjy.backendpoint.dao.entity.oa.MajorChangeRecordPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.oa.DropoutRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.MajorChangeRecordMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.MajorChangeDBFInfoRO;
import com.scnujxjy.backendpoint.model.ro.oa.MajorChangeRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionSelectArgs;
import com.scnujxjy.backendpoint.model.vo.oa.MajorChangeRecordVO;
import com.scnujxjy.backendpoint.model.vo.oa.MajorChangeSelectArgs;
import com.scnujxjy.backendpoint.util.dbf.DbfRecord;
import com.scnujxjy.backendpoint.util.dbf.DbfWriterUtil;
import com.scnujxjy.backendpoint.util.dbf.StudentStatusChangeRecord;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
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
 * 转专业记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class  MajorChangeRecordService extends ServiceImpl<MajorChangeRecordMapper, MajorChangeRecordPO> implements IService<MajorChangeRecordPO> {

    @Resource
    private ScnuXueliTools scnuXueliTools;

    /**
     * 不同角色获取学生转专业信息
     * @param majorChangeRecordROPageRO
     * @return
     */
    public PageVO<MajorChangeRecordVO> getMajorChangeInfos(PageRO<MajorChangeRecordRO> majorChangeRecordROPageRO) {
        List<MajorChangeRecordVO> majorChangeRecordVOS = getBaseMapper().getMajorChangeInfos(
                majorChangeRecordROPageRO.getEntity(), (majorChangeRecordROPageRO.getPageNumber()-1)*majorChangeRecordROPageRO.getPageSize(),
                majorChangeRecordROPageRO.getPageSize());
        long count = getBaseMapper().getMajorChangeInfosCount(majorChangeRecordROPageRO.getEntity());
        PageVO<MajorChangeRecordVO> pageVO = new PageVO<MajorChangeRecordVO>();
        pageVO.setRecords(majorChangeRecordVOS);
        pageVO.setTotal(count);
        pageVO.setCurrent(majorChangeRecordROPageRO.getPageNumber());
        return pageVO;
    }

    /**
     * 获取转专业的筛选参数
     * @param entity
     * @return
     */
    public MajorChangeSelectArgs getMajorChangeInfosSelectArgs(MajorChangeRecordRO entity) {

        MajorChangeSelectArgs majorChangeSelectArgs = new MajorChangeSelectArgs();

        ExecutorService executor = Executors.newFixedThreadPool(2); // 2 代表你有2个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> getBaseMapper().getDistinctGrades(entity));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> getBaseMapper().getDistinctRemarks(entity));

        try {
            majorChangeSelectArgs.setGrades(distinctGradesFuture.get());

            majorChangeSelectArgs.setRemarks(distinctCollegeNamesFuture.get());


        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return majorChangeSelectArgs;
    }

    public byte[] generateMajorChangeInformationDBF(MajorChangeDBFInfoRO majorChangeDBFInfoRO) {
        // 定义 DBF 文件的字段
        DBFField[] fields = {
                scnuXueliTools.createField("KSH", DBFDataType.CHARACTER, 18),
                scnuXueliTools.createField("XM", DBFDataType.CHARACTER, 40),
                scnuXueliTools.createField("YDLX", DBFDataType.CHARACTER, 8),
                scnuXueliTools.createField("PZRQ", DBFDataType.CHARACTER, 8),
                scnuXueliTools.createField("WH", DBFDataType.CHARACTER, 50),
                scnuXueliTools.createField("YY", DBFDataType.CHARACTER, 50),
                scnuXueliTools.createField("SM", DBFDataType.CHARACTER, 200),
                scnuXueliTools.createField("ZYDM", DBFDataType.CHARACTER, 8),
                scnuXueliTools.createField("ZYMC", DBFDataType.CHARACTER, 50),
                scnuXueliTools.createField("XZ", DBFDataType.CHARACTER, 3),
                scnuXueliTools.createField("DQSZJ", DBFDataType.CHARACTER, 4),
                scnuXueliTools.createField("XH", DBFDataType.CHARACTER, 15),
                scnuXueliTools.createField("FY", DBFDataType.CHARACTER, 24),
                scnuXueliTools.createField("XSH", DBFDataType.CHARACTER, 24),
                scnuXueliTools.createField("BH", DBFDataType.CHARACTER, 24),
                scnuXueliTools.createField("YJBYRQ", DBFDataType.CHARACTER, 8)
        };
        List<MajorChangeRecordVO> majorChangeRecordVOS = getBaseMapper().getMajorChangeInfosForGenerateDBF(majorChangeDBFInfoRO);
        // 准备数据
        List<DbfRecord> records = new ArrayList<>();
        for(MajorChangeRecordVO majorChangeRecordVO: majorChangeRecordVOS){
            records.add(new StudentStatusChangeRecord(majorChangeRecordVO.getExamRegistrationNumber(),
                    majorChangeRecordVO.getStudentName(), StudentStatusChangeEnum.MAJOR_CHANGE.getChangeType(),
                    "20220101", majorChangeDBFInfoRO.getDocumentNumber(), majorChangeRecordVO.getReason(),
                    "说明1", "专业代码1",
                    "专业名称1", "3", "2022", "学号1", "分院1",
                    "系所1", "班号1", "20230601"));
        }

        // 写入 DBF 文件
        return DbfWriterUtil.writeDbfToByteArray(fields, records);
    }
}
