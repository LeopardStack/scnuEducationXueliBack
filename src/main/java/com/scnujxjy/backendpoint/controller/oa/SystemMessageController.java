package com.scnujxjy.backendpoint.controller.oa;

import com.scnujxjy.backendpoint.service.oa.SystemMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/systemMessages")
public class SystemMessageController {

    @Autowired
    private SystemMessageService systemMessageService;

}
