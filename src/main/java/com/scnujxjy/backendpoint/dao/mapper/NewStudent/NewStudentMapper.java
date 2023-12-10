package com.scnujxjy.backendpoint.dao.mapper.NewStudent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.ro.NewStudent.NewStudentInfoRo;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.NewStudentAdmissionInformationVo;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NewStudentMapper {

    public List<NewStudentAdmissionInformationVo> selectNewStudentInfo(PageRO<NewStudentInfoRo> pageRO);
}
