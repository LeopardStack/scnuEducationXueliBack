package com.scnujxjy.backendpoint.controller.oa;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.oa.MajorChangeDBFInfoRO;
import com.scnujxjy.backendpoint.model.ro.oa.MajorChangeRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.oa.MajorChangeRecordVO;
import com.scnujxjy.backendpoint.model.vo.oa.MajorChangeSelectArgs;
import com.scnujxjy.backendpoint.service.oa.MajorChangeRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 转专业 Rest 接口
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@RestController
@RequestMapping("/major-change-record")
public class MajorChangeRecordController {
    @Resource
    private MajorChangeRecordService majorChangeRecordService;

    /**
     * 分页查询学生转专业审核信息
     *
     * @param majorChangeRecordROPageRO 学生转专业审核分页查询参数
     * @return 学生转专业分页信息
     */
    @PostMapping("/page")
    public SaResult pageQueryMajorChangeInformation(@RequestBody PageRO<MajorChangeRecordRO> majorChangeRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(majorChangeRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(majorChangeRecordROPageRO.getEntity())) {
            majorChangeRecordROPageRO.setEntity(new MajorChangeRecordRO());
        }
        // 查询数据
        PageVO<MajorChangeRecordVO> admissionInformationVOPageVO = majorChangeRecordService.getMajorChangeInfos(majorChangeRecordROPageRO);
//        // 校验数据
        if (Objects.isNull(admissionInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(admissionInformationVOPageVO).setCode(200);
    }

    /**
     * 查询学生转专业审核筛选信息
     *
     * @param majorChangeRecordROPageRO 学生转专业审核分页查询参数
     * @return 学生转专业分页信息
     */
    @PostMapping("/query_major_change_select_args")
    public SaResult queryMajorChangeInformationSelectArgs(@RequestBody PageRO<MajorChangeRecordRO> majorChangeRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(majorChangeRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(majorChangeRecordROPageRO.getEntity())) {
            majorChangeRecordROPageRO.setEntity(new MajorChangeRecordRO());
        }
        // 查询数据
        MajorChangeSelectArgs majorChangeInfosSelectArgs = majorChangeRecordService.getMajorChangeInfosSelectArgs(majorChangeRecordROPageRO.getEntity());
//        // 校验数据
        if (Objects.isNull(majorChangeInfosSelectArgs)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(majorChangeInfosSelectArgs).setCode(200);
    }

    /**
     * 生成指定筛选参数的学生转专业信息所对应的模板信息
     *
     * @param majorChangeDBFInfoRO 学生转专业审核分页查询参数
     * @return 学生转专业分页信息
     */
    @PostMapping("/generate_major_change_dbf")
    public SaResult generateMajorChangeInformationDBF(@RequestBody MajorChangeDBFInfoRO majorChangeDBFInfoRO) {
        // 参数校验
        if (Objects.isNull(majorChangeDBFInfoRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(majorChangeDBFInfoRO)) {
            majorChangeDBFInfoRO = new MajorChangeDBFInfoRO();
        }
        // 查询数据
        byte[] bytes = majorChangeRecordService.generateMajorChangeInformationDBF(majorChangeDBFInfoRO);

        // Generate filename with date suffix
        String dateSuffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = "学生转专业_" + dateSuffix + ".dbf";

        // Return data
        return SaResult.ok().setData(bytes).set("fileName", fileName);
    }

}

