package com.scnujxjy.backendpoint.controller.office_automation;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.service.office_automation.OfficeAutomationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}
