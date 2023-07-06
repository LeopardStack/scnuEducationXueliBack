package com.scnujxjy.backendpoint.controller.project_manage;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.controller.basic.PlatformRoleController;
import com.scnujxjy.backendpoint.dto.CreateProjectInfo;
import com.scnujxjy.backendpoint.dto.ProjectInfo;
import com.scnujxjy.backendpoint.entity.project_manage.ProjectPermissions;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingProject;
import com.scnujxjy.backendpoint.service.basic.*;
import com.scnujxjy.backendpoint.service.project_manage.ProjectPermissionsService;
import com.scnujxjy.backendpoint.service.project_manage.TrainingProjectService;
import com.scnujxjy.backendpoint.util.ResultCode;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * training-project
 *   培训项目创建
 *
 * @author leopard
 * @since 2023-07-02
 */
@RestController
@SaCheckPermission("project.create")
@RequestMapping("/training-project")
public class TrainingProjectController {
    private static final Logger logger = LoggerFactory.getLogger(TrainingProjectController.class);

    @Autowired
    private TrainingProjectService trainingProjectService;
    @Autowired
    private ProjectPermissionsService projectPermissionsService;

    @RequestMapping("create_new_project")
    @Transactional
    public SaResult createTrainingProject(@RequestBody CreateProjectInfo createProjectInfo) {
        TrainingProject newTrainingProject = new TrainingProject();
        newTrainingProject.setName(createProjectInfo.getName());
        newTrainingProject.setDescription(createProjectInfo.getDescription());
        newTrainingProject.setCreatedAt(createProjectInfo.getStartDate());
        newTrainingProject.setDeadline(createProjectInfo.getEndDate());
        newTrainingProject.setCreditHours(Integer.parseInt(createProjectInfo.getHours()));
        logger.info("传输过来的项目信息 " + newTrainingProject.toString());

        try {
            trainingProjectService.save(newTrainingProject);

            // Convert the createProjectInfo object to a JSON object
            JSONObject createProjectInfoJson = (JSONObject) JSON.toJSON(createProjectInfo);
            // Remove the fields we've already set
            createProjectInfoJson.remove("name");
            createProjectInfoJson.remove("description");
            createProjectInfoJson.remove("startDate");
            createProjectInfoJson.remove("endDate");
            createProjectInfoJson.remove("hours");

            ProjectPermissions permissions = new ProjectPermissions();
            permissions.setProjectId(newTrainingProject.getId());
            permissions.setResources(createProjectInfoJson.toJSONString());
            projectPermissionsService.save(permissions);
        } catch (Exception e) {
            logger.error(e.toString());
            // If there's any exception, the transaction will be rolled back
            return SaResult.error(ResultCode.CREATE_PROJECT_FAIL.getMessage()).setCode(ResultCode.CREATE_PROJECT_FAIL.getCode());
        }

        return SaResult.ok();
    }

    /**
     * getAllProjectsInfo
     * 获取所有项目信息
     * @return SaResult
     */
    @RequestMapping("getAllProjectsInfo")
    @Transactional
    public SaResult getAllProjectsInfo() {
        ArrayList<ProjectInfo> projectInfoArrayList = new ArrayList<>();
        try {
            List<TrainingProject> trainingProjects = trainingProjectService.list();
            for (TrainingProject trainingProject : trainingProjects) {
                Long projectId = trainingProject.getId();
                QueryWrapper<ProjectPermissions> projectPermissionsQueryWrapper = new QueryWrapper<>();
                projectPermissionsQueryWrapper.eq("project_id", projectId);

                ProjectPermissions permissions = projectPermissionsService.getOne(projectPermissionsQueryWrapper);
                ProjectInfo projectInfo = new ProjectInfo();
                projectInfo.setId(projectId);
                projectInfo.setName(trainingProject.getName());
                projectInfo.setDescription(trainingProject.getDescription());
                projectInfo.setCreatedAt(trainingProject.getCreatedAt());
                projectInfo.setDeadline(trainingProject.getDeadline());
                projectInfo.setCreditHours(trainingProject.getCreditHours());
                projectInfo.setResources(permissions.getResources());

                projectInfoArrayList.add(projectInfo);
            }
        }catch (Exception e){
            logger.error(e.toString());
            return SaResult.error(ResultCode.CREATE_PROJECT_FAIL2.getMessage()).setCode(ResultCode.CREATE_PROJECT_FAIL2.getCode());
        }

        return SaResult.ok().set("allProjectInfo", projectInfoArrayList);
    }

}

