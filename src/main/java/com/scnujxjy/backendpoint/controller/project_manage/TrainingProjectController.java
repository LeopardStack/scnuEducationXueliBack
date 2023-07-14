package com.scnujxjy.backendpoint.controller.project_manage;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    /**
     * createTrainingProject
     * 创建新的培训项目
     * @param createProjectInfo 前端传递过来的培训项目 实体信息
     * @return SaResult
     */
    @RequestMapping("create_new_project")
    @SaCheckPermission("project.create")
    @Transactional
    public SaResult createTrainingProject(@RequestBody CreateProjectInfo createProjectInfo) {
        long userID = Long.parseLong((String) StpUtil.getLoginId());

        TrainingProject newTrainingProject = new TrainingProject();
        newTrainingProject.setName(createProjectInfo.getName());
        newTrainingProject.setDescription(createProjectInfo.getDescription());
        newTrainingProject.setCreatedAt(createProjectInfo.getCreatedAt());
        newTrainingProject.setDeadline(createProjectInfo.getDeadline());
        newTrainingProject.setCreditHours(Integer.parseInt(createProjectInfo.getCreditHours()));
        newTrainingProject.setCreatorId(userID);
        logger.info("传输过来的项目信息 " + newTrainingProject.toString());

        try {
            trainingProjectService.save(newTrainingProject);

            // Convert the createProjectInfo object to a JSON object
            JSONObject createProjectInfoJson = (JSONObject) JSON.toJSON(createProjectInfo);
            // Remove the fields we've already set
            createProjectInfoJson.remove("name");
            createProjectInfoJson.remove("description");
            createProjectInfoJson.remove("createdAt");
            createProjectInfoJson.remove("deadline");
            createProjectInfoJson.remove("creditHours");

            ProjectPermissions permissions = new ProjectPermissions();
            permissions.setProjectId(newTrainingProject.getId());
            permissions.setResources(createProjectInfoJson.toJSONString());
            projectPermissionsService.save(permissions);

            ProjectInfo newProject = new ProjectInfo();
            newProject.setId(newTrainingProject.getId());
            newProject.setName(newTrainingProject.getName());
            newProject.setDescription(newTrainingProject.getDescription());
            newProject.setCreatedAt(newTrainingProject.getCreatedAt());
            newProject.setDeadline(newTrainingProject.getDeadline());
            newProject.setCreditHours(newTrainingProject.getCreditHours());
            newProject.setResources(permissions.getResources());
            return SaResult.ok().set("newProject", newProject);
        } catch (Exception e) {
            logger.error(e.toString());
            // If there's any exception, the transaction will be rolled back
            return SaResult.error(ResultCode.CREATE_PROJECT_FAIL.getMessage()).setCode(ResultCode.CREATE_PROJECT_FAIL.getCode());
        }
    }

    /**
     * getAllProjectsInfo
     * 获取所有项目信息
     * @return SaResult
     */
    @RequestMapping("getAllProjectsInfo")
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


    /**
     * deleteProject
     * 按照项目 ID 来删除项目
     * @param project_id 项目 ID
     * @return
     */
    @RequestMapping("deleteProject")
    @Transactional
    public SaResult deleteProject(@RequestParam("project_id") long project_id) {
        logger.info("项目 ID 为 " + project_id);
        TrainingProject trainingProject = trainingProjectService.getById(project_id);
        logger.info("项目为 " + trainingProject.toString());

        try {
            QueryWrapper<ProjectPermissions> projectPermissionsQueryWrapper = new QueryWrapper<>();
            projectPermissionsQueryWrapper.eq("project_id", project_id);
            boolean remove = projectPermissionsService.remove(projectPermissionsQueryWrapper);
            logger.info("项目权限删除结果  " + remove);
            boolean b = trainingProjectService.removeById(project_id);
            logger.info("项目信息删除结果  " + b);
            return SaResult.ok("成功删除该项目 " + trainingProject.getName());
        }catch (Exception e){
            logger.error(e.toString());
        }

        return SaResult.error(ResultCode.CREATE_PROJECT_FAIL3.getMessage()).setCode(ResultCode.CREATE_PROJECT_FAIL3.getCode());
    }


    /**
     * updateProject
     * 修改项目信息
     * @param projectInfo 项目信息
     * @return SaResult
     */
    @RequestMapping("update_project")
    @Transactional
    public SaResult updateProject(@RequestBody ProjectInfo projectInfo) {
        logger.info("即将要修改的项目信息 " + projectInfo.toString());
        return SaResult.ok();
    }

}