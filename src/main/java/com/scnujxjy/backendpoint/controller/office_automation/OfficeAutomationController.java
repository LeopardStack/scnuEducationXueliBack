package com.scnujxjy.backendpoint.controller.office_automation;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalTypePO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalTypeAllInformation;
import com.scnujxjy.backendpoint.service.office_automation.OfficeAutomationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;

@RestController
@RequestMapping("/office-automation")
public class OfficeAutomationController {

    @Resource
    private OfficeAutomationService officeAutomationService;

    @PostMapping("/trigger")
    public SaResult trigger() {
        officeAutomationService.trigger();
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
        PageVO<ApprovalTypeAllInformation> approvalTypeAllInformationPageVO = officeAutomationService.pageQuery(approvalTypePOPageRO);
        if (Objects.isNull(approvalTypeAllInformationPageVO)) {
            return SaResult.error("查询失败");
        }
        return SaResult.data(approvalTypeAllInformationPageVO);
    }


}
