package com.scnujxjy.backendpoint.controller.office_automation;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalTypePO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordAllInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalTypeAllInformation;
import com.scnujxjy.backendpoint.service.office_automation.OfficeAutomationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;
import static com.scnujxjy.backendpoint.model.vo.PageVO.isPageVONull;

@RestController
@RequestMapping("/office-automation")
public class OfficeAutomationController {

    @Resource
    private OfficeAutomationService officeAutomationService;

    /**
     * 分页查询所有OA类型
     * <p>包括 OA 其中的步骤</p>
     *
     * @param approvalTypePOPageRO
     * @return
     */
    @PostMapping("/page-approval-type")
    public SaResult pageApprovalTypeStepInformation(@RequestBody PageRO<ApprovalTypePO> approvalTypePOPageRO) {
        if (Objects.isNull(approvalTypePOPageRO)) {
            throw dataMissError();
        }
        PageVO<ApprovalTypeAllInformation> approvalTypeAllInformationPageVO = officeAutomationService.pageQueryApprovalTypeAllInformation(approvalTypePOPageRO);
        if (isPageVONull(approvalTypeAllInformationPageVO)) {
            return SaResult.error("查询数据为空");
        }
        return SaResult.data(approvalTypeAllInformationPageVO);
    }

    /**
     * 分页查询OA记录信息、步骤信息
     *
     * @param approvalRecordPOPageRO 分页查询参数
     * @return
     */
    @PostMapping("/page-approval-record")
    public SaResult pageApprovalRecordAllInformation(@RequestBody PageRO<ApprovalRecordPO> approvalRecordPOPageRO) {
        if (Objects.isNull(approvalRecordPOPageRO)) {
            throw dataMissError();
        }
        PageVO<ApprovalRecordAllInformation> approvalRecordAllInformationPageVO = officeAutomationService.pageQueryApprovalRecordAllInformation(approvalRecordPOPageRO);
        if (isPageVONull(approvalRecordAllInformationPageVO)) {
            return SaResult.error("查询数据为空");
        }
        return SaResult.data(approvalRecordAllInformationPageVO);
    }

    /**
     * 根据审核记录id查询审核记录、步骤信息
     *
     * @param approvalId 审核记录id
     * @return
     */
    @GetMapping("/detail-approval-record")
    public SaResult approvalRecordAllInformationDetail(Long approvalId) {
        if (Objects.isNull(approvalId)) {
            throw dataMissError();
        }
        ApprovalRecordAllInformation approvalRecordAllInformation = officeAutomationService.approvalRecordDetail(approvalId);
        if (Objects.isNull(officeAutomationService)) {
            throw dataNotFoundError();
        }
        return SaResult.data(approvalRecordAllInformation);
    }

    @PostMapping("/create-approval-record")
    public SaResult createApprovalRecord(@RequestBody ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getApprovalTypeId())) {
            return SaResult.error("审批参数为空");
        }
        Boolean created = officeAutomationService.createApprovalRecord(approvalRecordPO);
        return SaResult.data(JSONObject.of("created", created));
    }

    @PostMapping("/process-approval-step-record")
    public SaResult processApprovalStepRecord(@RequestBody ApprovalStepRecordPO approvalStepRecordPO) {
        if (Objects.isNull(approvalStepRecordPO)) {
            throw dataMissError();
        }
        Boolean processed = officeAutomationService.processApproval(approvalStepRecordPO);
        if (!processed) {
            return SaResult.code(2000).setMsg("审批失败");
        }
        return SaResult.data(processed);
    }

    @GetMapping("/delete-approval-record")
    public SaResult deleteApprovalRecord(Long approvalId) {
        if (Objects.isNull(approvalId)) {
            throw dataMissError();
        }
        officeAutomationService.deleteApprovalRecord(approvalId);
        return SaResult.ok();
    }

    @PostMapping("/create-document")
    public SaResult createDocument(@RequestBody Map<String, Object> map) {
        if (Objects.isNull(map) || map.containsKey("typeId") == false) {
            return SaResult.error("插入信息缺失");
        }
        String id = officeAutomationService.insertDocument(map, Long.valueOf((String) map.get("typeId")));
        return SaResult.data(JSONObject.of("id", id));
    }
}
