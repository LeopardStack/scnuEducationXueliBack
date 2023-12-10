package com.scnujxjy.backendpoint.controller.NewStudent;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.mapper.NewStudent.NewStudentMapper;
import com.scnujxjy.backendpoint.model.ro.NewStudent.NewStudentInfoRo;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.NewStudentAdmissionInformationVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/newStudent")
public class NewStudentInfo {


    @Resource
    private NewStudentMapper newStudentMapper;
    /**
    * @Version：1.0.0
    * @Description：分頁查詢新生信息
    * @Author：3304393868@qq.com
    * @Date：2023/12/8-23:33
    */

    @GetMapping("/list")
    public SaResult list(PageRO<NewStudentInfoRo> pageRO){
    List<NewStudentAdmissionInformationVo> list = newStudentMapper.selectNewStudentInfo(pageRO);
        return SaResult.data(list);
    }
}
