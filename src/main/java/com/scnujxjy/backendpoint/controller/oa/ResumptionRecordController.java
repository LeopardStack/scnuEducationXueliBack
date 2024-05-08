package com.scnujxjy.backendpoint.controller.oa;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.oa.ResumptionRecordRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.oa.ResumptionRecordSelectArgs;
import com.scnujxjy.backendpoint.model.vo.oa.ResumptionRecordVO;
import com.scnujxjy.backendpoint.model.vo.oa.ResumptionWithSuspensionVO;
import com.scnujxjy.backendpoint.service.office_automation.approval_result.ResumptionRecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;

/**
 * 复学 Rest 接口
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@RestController
@RequestMapping("/resumption-record")
public class ResumptionRecordController {
    @Resource
    private ResumptionRecordService resumptionRecordService;

    /**
     * 分页查询学生复学审核信息
     *
     * @param resumptionRecordROPageRO 学生复学审核分页查询参数
     * @return 学生复学分页信息
     */
    @PostMapping("/page")
    public SaResult pageQueryResumptionInformation(@RequestBody PageRO<ResumptionRecordRO> resumptionRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(resumptionRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(resumptionRecordROPageRO.getEntity())) {
            resumptionRecordROPageRO.setEntity(new ResumptionRecordRO());
        }
        // 查询数据
        PageVO<ResumptionRecordVO> resumptionInfos = resumptionRecordService.getResumptionInfos(resumptionRecordROPageRO);

        // 返回数据
        return SaResult.data(resumptionInfos).setCode(200);
    }

    /**
     * 查询学生复学审核筛选信息
     *
     * @param resumptionRecordROPageRO 学生复学审核分页查询参数
     * @return 学生复学分页信息
     */
    @PostMapping("/query_resumption_select_args")
    public SaResult queryResumptionInformationSelectArgs(@RequestBody PageRO<ResumptionRecordRO> resumptionRecordROPageRO) {
        // 参数校验
        if (Objects.isNull(resumptionRecordROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(resumptionRecordROPageRO.getEntity())) {
            resumptionRecordROPageRO.setEntity(new ResumptionRecordRO());
        }
        // 查询数据
        ResumptionRecordSelectArgs resumptionRecordSelectArgs = resumptionRecordService.getResumptionInfosSelectArgs(resumptionRecordROPageRO.getEntity());

        // 返回数据
        return SaResult.data(resumptionRecordSelectArgs).setCode(200);
    }


    /**
     * 根据复学的休学 ID 不为空 获取休学信息
     */
    @PostMapping("/get_suspension")
    public SaResult getSuspensionInfo(@RequestBody ResumptionRecordRO resumptionRecordRO){
        ResumptionWithSuspensionVO resumptionInfo = resumptionRecordService.getSuspensionInfo(resumptionRecordRO);
        return SaResult.ok().setData(resumptionInfo);
    }
}

