package com.scnujxjy.backendpoint.controller.office_automation;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalTypePO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordAllInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalTypeAllInformation;
import com.scnujxjy.backendpoint.service.office_automation.OfficeAutomationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.model.vo.PageVO.isPageVONull;

@RestController
@RequestMapping("/office-automation")
public class OfficeAutomationController {

    @Resource
    private OfficeAutomationService officeAutomationService;

    @PostMapping("/trigger")
    public SaResult trigger() {
        officeAutomationService.trigger(OfficeAutomationHandlerType.COMMON);
        return SaResult.ok();
    }

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
}
