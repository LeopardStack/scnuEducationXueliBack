package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusTeacherFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentAllStatusInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.WangTiLoginUserVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.filter.TeacherFilter;
import com.scnujxjy.backendpoint.util.filter.TeachingPointFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 学籍信息表
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/student-status")
public class StudentStatusController {

    @Resource
    private StudentStatusService studentStatusService;

    @Resource
    private MinioService minioService;

    @Resource
    private MessageSender messageSender;


    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private TeacherFilter teacherFilter;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private TeachingPointFilter teachingPointFilter;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 根据id查询学籍信息
     *
     * @param id 学籍信息id
     * @return 学籍信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询
        StudentStatusVO studentStatusVO = studentStatusService.detailById(id);

        return SaResult.data(studentStatusVO);
    }

    /**
     * 分页查询学籍信息
     *
     * @param studentStatusROPageRO 分页参数
     * @return 学籍信息列表
     */
    @PostMapping("/page")
    public SaResult pageQueryStudentStatus(@RequestBody PageRO<StudentStatusRO> studentStatusROPageRO) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(studentStatusROPageRO.getEntity())) {
            studentStatusROPageRO.setEntity(new StudentStatusRO());
        }
        // 查询
        PageVO<StudentStatusVO> studentStatusVOPageVO = studentStatusService.pageQueryStudentStatus(studentStatusROPageRO);

        return SaResult.data(studentStatusVOPageVO);
    }

    /**
     * 更新学籍信息
     *
     * @param studentStatusRO 学籍信息
     * @return 更新后的学籍信息
     */
    @PutMapping("/edit")
    public SaResult editById(StudentStatusRO studentStatusRO) {
        // 校验参数
        if (Objects.isNull(studentStatusRO) || Objects.isNull(studentStatusRO.getId())) {
            throw dataMissError();
        }
        // 更新学籍信息
        StudentStatusVO studentStatusVO = studentStatusService.editById(studentStatusRO);
        if (Objects.isNull(studentStatusVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(studentStatusVO);
    }

    /**
     * 删除学籍信息
     *
     * @param id 学籍信息id
     * @return 删除学籍信息的数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataDeleteError();
        }
        // 删除学籍信息
        int count = studentStatusService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

    /**
     * 根据学生用户 id 查询自己的学籍信息
     *
     * @return 学籍信息
     */
    @GetMapping("/query_student_status_information")
    public SaResult queryStudentStatusInformation() {
        Object loginId = StpUtil.getLoginId();
        String studentId = null;
        // 校验参数
        if (Objects.isNull(loginId)) {
            throw dataMissError();
        } else {
            try {
                studentId = (String) loginId;
            } catch (Exception e) {
                throw e;
            }
        }
        // 查询
        List<StudentAllStatusInfoVO> studentAllStatusInfoVOS = studentStatusService.statusInfoByIdNumber(studentId);

        return SaResult.data(studentAllStatusInfoVOS);
    }

    /**
     * 根据 用户名查询学籍信息
     *
     * @return 学籍信息
     */
    @GetMapping("/detail_username")
    public SaResult detailByUserName() {
        String loginId = (String) StpUtil.getLoginId();
        // 校验参数
        if (Objects.isNull(loginId) || loginId.length() == 0) {
            throw dataMissError();
        }
        // 查询
        List<StudentStatusVO> studentStatusVOs = studentStatusService.getBaseMapper().
                selectStudentByidNumber(loginId);

        // 使用流操作找到最大的 grade 值
        Optional<StudentStatusVO> maxGradeStudent = studentStatusVOs.stream()
                .max(Comparator.comparing(StudentStatusVO::getGrade));

        // 检查是否找到最大 grade 值的对象
        StudentStatusVO result = null;
        result = maxGradeStudent.orElseGet(() -> studentStatusVOs.get(0));

        return SaResult.data(result);
    }

    /**
     * 获取入学照片
     *
     * @param grade 年级
     * @return
     */
    @GetMapping("/searchImportPhoto/{grade}")
    public ResponseEntity<byte[]> getImportPhoto(@PathVariable String grade) {
        byte[] photoBytes = studentStatusService.getImportPhoto(grade);

        if (photoBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(photoBytes.length)
                    .body(photoBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取毕业照片
     *
     * @param grade 年级
     * @return
     */
    @GetMapping("/searchExportPhoto/{grade}")
    public ResponseEntity<byte[]> getExportPhoto(@PathVariable String grade) {
        byte[] photoBytes = studentStatusService.getExportPhoto(grade);

        if (photoBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(photoBytes.length)
                    .body(photoBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取学位照片
     *
     * @param payload 学位照片的URL
     * @return
     */
    @PostMapping("/searchDegreePhoto")
    public ResponseEntity<byte[]> getDegreePhoto(@RequestBody Map<String, String> payload) {
        String degreePhotoUrl = payload.get("degreePhotoUrl");

        // 根据 degreePhotoUrl 获取学位照片的字节数据
        byte[] photoBytes = studentStatusService.getDegreePhotoByURL(degreePhotoUrl);

        if (photoBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(photoBytes.length)
                    .body(photoBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * 不同角色获取学生学籍信息，eg.教学点教务员、二级学院教务员、继续教育学院的各个部门的教务员
     *
     * @param studentStatusROPageRO 分页参数
     * @return 学籍信息列表
     */
    @PostMapping("/page_query_student_status")
    public SaResult pageQueryStudentStatusByManager(@RequestBody PageRO<StudentStatusFilterRO> studentStatusROPageRO) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(studentStatusROPageRO.getEntity())) {
            studentStatusROPageRO.setEntity(new StudentStatusFilterRO());
        }

        // 生成缓存键
        String cacheKey = StpUtil.getLoginIdAsString() + "studentStatus:" + studentStatusROPageRO.toString();

        // 从Redis中尝试获取缓存
        PageVO<FilterDataVO> filterDataVO = (PageVO<FilterDataVO>) redisTemplate.opsForValue().get(cacheKey);

        if (filterDataVO == null) {

            List<String> roleList = StpUtil.getRoleList();
//        PageVO<FilterDataVO> filterDataVO = null;
            // 获取访问者 ID
            if (roleList.isEmpty()) {
                return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    // 查询继续教育管理员权限范围内的教学计划
                    FilterDataVO studentStatusFilterDataVO = studentStatusService.allPageQueryStudentStatusFilter(studentStatusROPageRO, collegeAdminFilter);

                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(studentStatusFilterDataVO.getData());
                    filterDataVO.setTotal(studentStatusFilterDataVO.getTotal());
                    filterDataVO.setCurrent(studentStatusROPageRO.getPageNumber());
                    filterDataVO.setSize(studentStatusROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) studentStatusFilterDataVO.getData().size()
                            / studentStatusROPageRO.getPageSize()));

                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                        || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                        || roleList.contains(ADMISSION_ADMIN.getRoleName())
                ) {
                    // 查询继续教育管理员权限范围内的教学计划
                    FilterDataVO studentStatusFilterDataVO = studentStatusService.allPageQueryStudentStatusFilter(studentStatusROPageRO, managerFilter);

                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(studentStatusFilterDataVO.getData());
                    filterDataVO.setTotal(studentStatusFilterDataVO.getTotal());
                    filterDataVO.setCurrent(studentStatusROPageRO.getPageNumber());
                    filterDataVO.setSize(studentStatusROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) studentStatusFilterDataVO.getData().size()
                            / studentStatusROPageRO.getPageSize()));

                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                    FilterDataVO dataVO = studentStatusService.allPageQueryStudentStatusFilter(studentStatusROPageRO, teachingPointFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(dataVO.getData());
                    filterDataVO.setTotal(dataVO.getTotal());
                    filterDataVO.setCurrent(studentStatusROPageRO.getPageNumber());
                    filterDataVO.setSize(studentStatusROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) dataVO.getData().size()
                            / studentStatusROPageRO.getPageSize()));

                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName()) || roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {

                }

                // 如果获取的数据不为空，则放入Redis
                if (filterDataVO != null) {
                    // 设置10小时超时
                    redisTemplate.opsForValue().set(cacheKey, filterDataVO, 10, TimeUnit.HOURS);
                }

            }
        }
        return SaResult.data(filterDataVO);

    }


    /**
     * 教师获取学生信息
     *
     * @param studentStatusROPageRO 分页参数
     * @return 学籍信息列表
     */
    @PostMapping("/get_students_by_teacher")
    public SaResult getStudentStatusInfoByTeacher(@RequestBody PageRO<StudentStatusTeacherFilterRO> studentStatusROPageRO) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(studentStatusROPageRO.getEntity())) {
            studentStatusROPageRO.setEntity(new StudentStatusTeacherFilterRO());
        }

        // 生成缓存键
        String cacheKey = StpUtil.getLoginIdAsString() + "studentStatus:" + studentStatusROPageRO.toString();

        // 从Redis中尝试获取缓存
        PageVO<FilterDataVO> filterDataVO = (PageVO<FilterDataVO>) redisTemplate.opsForValue().get(cacheKey);

        if (filterDataVO == null) {

            List<String> roleList = StpUtil.getRoleList();
//        PageVO<FilterDataVO> filterDataVO = null;
            // 获取访问者 ID
            if (roleList.isEmpty()) {
                return SaResult.error("查询学生失败，角色信息缺失").setCode(2000);
            } else {
                FilterDataVO studentStatusFilterDataVO = studentStatusService.getStudentStatusInfoByTeacher(studentStatusROPageRO, teacherFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(studentStatusFilterDataVO.getData());
                filterDataVO.setTotal(studentStatusFilterDataVO.getTotal());
                filterDataVO.setCurrent(studentStatusROPageRO.getPageNumber());
                filterDataVO.setSize(studentStatusROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) studentStatusFilterDataVO.getData().size()
                        / studentStatusROPageRO.getPageSize()));

                // 如果获取的数据不为空，则放入Redis
                if (filterDataVO != null) {
                    // 设置10小时超时
                    redisTemplate.opsForValue().set(cacheKey, filterDataVO, 10, TimeUnit.HOURS);
                }

            }
        }
        return SaResult.data(filterDataVO);

    }

    /**
     * 根据二级学院教务员获取筛选参数
     *
     * @return 教学计划
     */
    @GetMapping("/get_select_args_admin")
    public SaResult getTeachingPlansArgsByCollege() {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String loginId = StpUtil.getLoginIdAsString();
        StudentStatusSelectArgs studentStatusSelectArgs = null;

        // 生成缓存键
        String cacheKey = StpUtil.getLoginIdAsString() + "studentStatusSelectArgsAdmin:" + loginId;

        // 尝试从Redis中获取数据
        studentStatusSelectArgs = (StudentStatusSelectArgs) redisTemplate.opsForValue().get(cacheKey);

        if (studentStatusSelectArgs == null) {
            if (roleList.isEmpty()) {
                return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    studentStatusSelectArgs = studentStatusService.getStudentStatusArgs(loginId, collegeAdminFilter);
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                        || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                        || roleList.contains(ADMISSION_ADMIN.getRoleName())
                ) {
                    studentStatusSelectArgs = studentStatusService.getStudentStatusArgs(loginId, managerFilter);
                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                    studentStatusSelectArgs = studentStatusService.getStudentStatusArgs(loginId, teachingPointFilter);
                }
                // 如果获取的数据不为空，则放入Redis
                if (studentStatusSelectArgs != null) {
                    // 设置10小时超时
                    redisTemplate.opsForValue().set(cacheKey, studentStatusSelectArgs, 10, TimeUnit.HOURS);
                }
            }
        }

        return SaResult.data(studentStatusSelectArgs);
    }


    /**
     * 采用消息队列来处理数据导出
     * 根据二级学院教务员获取筛选参数
     *
     * @return 教学计划
     */
    @PostMapping("/batch_export_studentstatus_data")
    public SaResult batchExportStudentStatusData(@RequestBody PageRO<StudentStatusFilterRO> studentStatusROPageRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();
        CourseInformationSelectArgs courseInformationSelectArgs = null;
        if (roleList.isEmpty()) {
            return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
                boolean send = messageSender.sendExportMsg(studentStatusROPageRO, managerFilter, userId);
                if (send) {
                    return SaResult.ok("导出学籍数据成功");
                }
            }
        }
        return SaResult.error("导出学籍数据失败！");
    }



    /**
     * 获取学生的学号 和密码
     * 密码： scnu+身份证后六位
     *
     * @return 教学计划
     */
    @GetMapping("/get_student_wangti_user")
    public SaResult getStudentWangTiLoginUser() {
        // 身份证号码
        String username = StpUtil.getLoginIdAsString();

        List<StudentStatusPO> studentStatusPOS = studentStatusService.getBaseMapper().selectList(new LambdaQueryWrapper<StudentStatusPO>()
                .eq(StudentStatusPO::getIdNumber, username));

        // 假设grade是一个可以转换为数字的字符串
        Optional<StudentStatusPO> latestStudentStatus = studentStatusPOS.stream()
                .max(Comparator.comparingInt(s -> Integer.parseInt(s.getGrade())));

        if (latestStudentStatus.isPresent()) {
            StudentStatusPO latestStudent = latestStudentStatus.get();
            WangTiLoginUserVO wangTiLoginUserVO = new WangTiLoginUserVO()
                    .setUsername(latestStudent.getStudentNumber())
                    .setPassword("scnu" + username.substring(username.length() - 6))
                    ;
            return SaResult.ok().setData(wangTiLoginUserVO);
        } else {
            return ResultCode.VIDEO_INFORMATION_FAIL1.generateErrorResultInfo();
        }
    }
}

