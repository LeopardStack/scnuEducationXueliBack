package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationSelectArgs;
import com.scnujxjy.backendpoint.service.teaching_process.ScoreInformationService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.filter.TeachingPointFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 成绩信息管理
 *
 * @author leopard
 * @since 2023-09-10
 */
@RestController
@RequestMapping("/score-information")
public class ScoreInformationController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private ScoreInformationService scoreInformationService;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private MessageSender messageSender;

    @Resource
    private TeachingPointFilter teachingPointFilter;


//    @GetMapping("/getGradeInfo")
//    public SaResult getGradeInfo() {
//        String loginId = (String) StpUtil.getLoginId();
//        // 参数校验
//        if (Objects.isNull(loginId) || loginId.trim().length() == 0) {
//            throw dataMissError();
//        }
//        // 查询数据
//        List<ScoreInformationPO> scoreInformationPOS = scoreInformationService.getBaseMapper().getGradeInfo(loginId);
//        if (Objects.isNull(scoreInformationPOS)) {
//            throw dataNotFoundError();
//        }
//        return SaResult.data(scoreInformationPOS);
//    }

    /**
     * 学生获取自己的成绩信息
     *
     * @return
     */
    @GetMapping("/getGradeInfo")
    public SaResult getGradeInfo() {
        String loginId = (String) StpUtil.getLoginId();
        // 参数校验
        if (Objects.isNull(loginId) || loginId.trim().length() == 0) {
            throw dataMissError();
        }

        // 尝试从 Redis 中获取数据
        List<ScoreInformationPO> scoreInformationPOS = (List<ScoreInformationPO>) redisTemplate.opsForValue().get("score:" + loginId);

        // 如果 Redis 中没有数据
        if (Objects.isNull(scoreInformationPOS)) {
            // 从数据库中查询数据
            scoreInformationPOS = scoreInformationService.getBaseMapper().getGradeInfo(loginId);
            if (Objects.isNull(scoreInformationPOS)) {
                throw dataNotFoundError();
            }
            // 将查询结果存入 Redis，设置过期时间为1小时（可以根据实际需求调整）
            redisTemplate.opsForValue().set("score:" + loginId, scoreInformationPOS, 100, TimeUnit.HOURS);
        }

        return SaResult.data(scoreInformationPOS);
    }

    /**
     * 根据不同角色来获取其权限范围内的缴费信息
     *
     * @param scoreInformationFilterROPageRO 缴费信息参数
     * @return 支付信息详情分页列表
     */
    @PostMapping("/get_gradinfo_data")
    public SaResult getGradeInfosByRole(@RequestBody PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        // 参数校验
        if (Objects.isNull(scoreInformationFilterROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(scoreInformationFilterROPageRO.getEntity())) {
            scoreInformationFilterROPageRO.setEntity(new ScoreInformationFilterRO());
        }

        // 生成缓存键
        String cacheKey = StpUtil.getLoginIdAsString() + "gradeInfos:" + scoreInformationFilterROPageRO.toString();

        // 从Redis中尝试获取缓存
        PageVO<FilterDataVO> filterDataVO = (PageVO<FilterDataVO>) redisTemplate.opsForValue().get(cacheKey);

        if (filterDataVO == null) {

            List<String> roleList = StpUtil.getRoleList();
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    //二级学院教务员查询学生成绩信息
                    FilterDataVO gradeInfoVOPageVO = null;
                    gradeInfoVOPageVO = scoreInformationService.
                            allPageQueryGradinfoFilter(scoreInformationFilterROPageRO, collegeAdminFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(gradeInfoVOPageVO.getData());
                    filterDataVO.setTotal(gradeInfoVOPageVO.getTotal());
                    filterDataVO.setCurrent(scoreInformationFilterROPageRO.getPageNumber());
                    filterDataVO.setSize(scoreInformationFilterROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) gradeInfoVOPageVO.getData().size()
                            / scoreInformationFilterROPageRO.getPageSize()));

                    if (Objects.isNull(gradeInfoVOPageVO)) {
                        throw dataNotFoundError();
                    }
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                    // 继续教育学院教务员查询学生成绩信息
                    FilterDataVO gradeInfoVOPageVO = null;
                    gradeInfoVOPageVO = scoreInformationService.
                            allPageQueryGradinfoFilter(scoreInformationFilterROPageRO, managerFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(gradeInfoVOPageVO.getData());
                    filterDataVO.setTotal(gradeInfoVOPageVO.getTotal());
                    filterDataVO.setCurrent(scoreInformationFilterROPageRO.getPageNumber());
                    filterDataVO.setSize(scoreInformationFilterROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) gradeInfoVOPageVO.getData().size()
                            / scoreInformationFilterROPageRO.getPageSize()));

                    if (Objects.isNull(gradeInfoVOPageVO)) {
                        throw dataNotFoundError();
                    }
                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
// 继续教育学院教务员查询学生成绩信息
                    FilterDataVO gradeInfoVOPageVO = null;
                    gradeInfoVOPageVO = scoreInformationService.
                            allPageQueryGradinfoFilter(scoreInformationFilterROPageRO, teachingPointFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(gradeInfoVOPageVO.getData());
                    filterDataVO.setTotal(gradeInfoVOPageVO.getTotal());
                    filterDataVO.setCurrent(scoreInformationFilterROPageRO.getPageNumber());
                    filterDataVO.setSize(scoreInformationFilterROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) gradeInfoVOPageVO.getData().size()
                            / scoreInformationFilterROPageRO.getPageSize()));

                    if (Objects.isNull(gradeInfoVOPageVO)) {
                        throw dataNotFoundError();
                    }
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
     * 根据二级学院教务员获取筛选参数
     *
     * @return 教学计划
     */
    @GetMapping("/get_select_args_admin")
    public SaResult getGradeArgsByCollege() {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String loginId = StpUtil.getLoginIdAsString();
        ScoreInformationSelectArgs studentStatusSelectArgs = null;

        // 生成缓存键
        String cacheKey = "selectGradeInfoArgsAdmin:" + loginId;

        // 尝试从Redis中获取数据
        studentStatusSelectArgs = (ScoreInformationSelectArgs) redisTemplate.opsForValue().get(cacheKey);

        if (studentStatusSelectArgs == null) {
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    studentStatusSelectArgs = scoreInformationService.getStudentStatusArgs(loginId, collegeAdminFilter);
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                    studentStatusSelectArgs = scoreInformationService.getStudentStatusArgs(loginId, managerFilter);
                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                    studentStatusSelectArgs = scoreInformationService.getStudentStatusArgs(loginId, teachingPointFilter);
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
     * 采用消息队列来处理成绩数据导出
     * 根据二级学院教务员获取筛选参数
     *
     * @return 教学计划
     */
    @PostMapping("/batch_export_scoreinformation_data")
    public SaResult batchExportStudentStatusData(@RequestBody PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
                boolean send = messageSender.sendExportMsg(scoreInformationFilterROPageRO, managerFilter, userId);
                if (send) {
                    return SaResult.ok("导出学籍数据成功");
                }
            }
        }
        return SaResult.error("导出学籍数据失败！");
    }


    /**
     * 获取学生评优成绩表
     *
     * @return 成绩表
     */
    @PostMapping("/export_award_data")
    public SaResult exportAwardData(@RequestBody Map<String, String> params) {
        String studentId = params.get("studentId");
        if (studentId == null || studentId.isEmpty()) {
            throw dataMissError();
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = scoreInformationService.exportAwardData(studentId);
            if (byteArrayOutputStream == null) {
                return SaResult.error("导出评优成绩单失败！");
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
//            String base64Encoded = Base64.getEncoder().encodeToString(bytes);
            // 这里将字节流作为Base64字符串返回
            return SaResult.ok().setData(bytes);
        } catch (Exception e) {
            return SaResult.error("导出评优成绩单失败！" + e.toString());
        }
    }


    /**
     * 下载评优成绩单
     *
     * @param params
     * @return
     */
    @PostMapping("/export_award_data1")
    public ResponseEntity<byte[]> exportAwardData1(@RequestBody Map<String, String> params) {
        String studentId = params.get("studentId");
        if (studentId == null || studentId.isEmpty()) {
            throw dataMissError();
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = scoreInformationService.exportAwardData(studentId);
            if (byteArrayOutputStream == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String encodedFilename = URLEncoder.encode(studentId + "_评优成绩单.xlsx", "UTF-8");
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(encodedFilename).build());
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}

