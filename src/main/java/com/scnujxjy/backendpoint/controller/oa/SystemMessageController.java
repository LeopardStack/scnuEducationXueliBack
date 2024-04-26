package com.scnujxjy.backendpoint.controller.oa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.scnujxjy.backendpoint.service.oa.SystemMessageService;

@RestController
@RequestMapping("/systemMessages")
public class SystemMessageController {

    @Autowired
    private SystemMessageService systemMessageService;

}
