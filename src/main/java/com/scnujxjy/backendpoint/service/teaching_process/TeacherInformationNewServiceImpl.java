package com.scnujxjy.backendpoint.service.teaching_process;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.model.ro.core_data.PageBeanResult;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class TeacherInformationNewServiceImpl implements TeacherInformationNewService {

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Override
    public SaResult queryTeacherInformation(TeacherInformationRequest teacherInformationRequest) {

        Integer page = teacherInformationRequest.getPage();
        Integer pageSize = teacherInformationRequest.getPageSize();
        Integer offset = (page - 1) * pageSize;
        teacherInformationRequest.setOffset(offset);

        try {
            List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectTeacherInformation(teacherInformationRequest);
            Long count = teacherInformationMapper.selectTeacherInformationCount(teacherInformationRequest);

            PageBeanResult pageBeanResult = new PageBeanResult(count, teacherInformationPOS, teacherInformationRequest.getPage());
            return SaResult.data(pageBeanResult);
        } catch (Exception e) {
            log.error("查询师资库出现异常,入参信息为:{}", teacherInformationRequest, e);
        }
        return SaResult.error("查询师资库异常，请联系管理员");
    }

    @Override
    public SaResult updateTeacherInformation(TeacherInformationPO teacherInformationPO) {
        try {
            int update = teacherInformationMapper.updateById(teacherInformationPO);
            if (update > 0) {
                return SaResult.ok("修改成功");
            }
        }catch (Exception e){
            log.error("修改师资库出现异常,入参信息为:{}", teacherInformationPO, e);
        }
        return  SaResult.error("修改失败，请联系管理员");
    }

    @Override
    public SaResult addTeacherInformation(TeacherInformationPO teacherInformationPO) {

        try {
            int insert = teacherInformationMapper.insert(teacherInformationPO);
            if (insert > 0) {
                return SaResult.ok("添加成功");
            }
        }catch (Exception e){
            log.error("添加师资库出现异常,入参信息为:{}", teacherInformationPO, e);
        }
        return  SaResult.error("添加失败，请联系管理员");
    }

    @Override
    public SaResult delteteTeacherInformation(TeacherInformationRequest teacherInformationRequest) {
        try {
            int i = teacherInformationMapper.deleteById(teacherInformationRequest.getUserId());
            if (i > 0) {
                return SaResult.ok("删除成功");
            }
        }catch (Exception e){
            log.error("删除师资库出现异常,入参信息为:{}", teacherInformationRequest, e);
        }
        return  SaResult.ok("删除失败，请联系管理员");
    }
}
