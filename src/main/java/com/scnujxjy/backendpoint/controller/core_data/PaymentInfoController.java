package com.scnujxjy.backendpoint.controller.core_data;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.service.core_data.PaymentInfoService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.filter.TeachingPointFilter;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 缴费信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/payment-info")
public class PaymentInfoController {

    @Resource
    private PaymentInfoService paymentInfoService;

    @Resource
    private StudentStatusService studentStatusService;
    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MessageSender messageSender;

    @Resource
    private TeachingPointFilter teachingPointFilter;

    /**
     * 通过id查询详情
     *
     * @param id 主键id
     * @return 支付信息详情
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        PaymentInfoVO paymentInfoVO = paymentInfoService.detailById(id);
        if (Objects.isNull(paymentInfoVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(paymentInfoVO);
    }

    @GetMapping("/make_payment")
    public ResponseEntity<String> makePayment(){
        try{
            ResponseEntity<String> response = paymentInfoService.makePayment();
            return response;
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed: " +
                     e.getMessage());
        }
    }

    /**
     * 分页查询、批量查询支付信息详情列表
     *
     * @param paymentInfoROPageRO 支付信息分页参数
     * @return 支付信息详情分页列表
     */
    @PostMapping("/page")
    public SaResult pageQueryPaymentInfo(@RequestBody PageRO<PaymentInfoRO> paymentInfoROPageRO) {
        // 参数校验

        if (Objects.isNull(paymentInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(paymentInfoROPageRO.getEntity())) {
            paymentInfoROPageRO.setEntity(new PaymentInfoRO());
        }
        // 数据查询
        PageVO<PaymentInfoVO> paymentInfoVOPageVO = paymentInfoService.pageQueryPaymentInfo(paymentInfoROPageRO);
        if (Objects.isNull(paymentInfoVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(paymentInfoVOPageVO);
    }

    /**
     * 根据id更新支付信息
     *
     * @param paymentInfoRO 支付信息
     * @return 更新后的支付信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody PaymentInfoRO paymentInfoRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoRO) || Objects.isNull(paymentInfoRO.getId())) {
            throw dataMissError();
        }
        // 数据更新
        PaymentInfoVO paymentInfoVO = paymentInfoService.editById(paymentInfoRO);
        if (Objects.isNull(paymentInfoVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(paymentInfoVO);
    }

    /**
     * 根据id删除支付信息
     *
     * @param id 主键id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询
        Integer count = paymentInfoService.deleteById(id);
        if (Objects.isNull(count)) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

    /**
     * 学生获取自己的缴费信息
     *
     * @return
     */
    @GetMapping("/getPaymentInfo")
    public SaResult getPaymentInfo() {
        String loginId = (String) StpUtil.getLoginId();
        // 参数校验
        if (Objects.isNull(loginId) || loginId.trim().length() == 0) {
            throw dataMissError();
        }

        // 判断以下是否属于新生  也就是 学籍信息是否存在
        // 查询数据
        List<PaymentInfoPO> paymentInfoPOS = paymentInfoService.getBaseMapper().getStudentPayInfo(loginId);
        if (Objects.isNull(paymentInfoPOS)) {
            throw dataNotFoundError();
        }
        return SaResult.data(paymentInfoPOS);
    }


    /**
     * 根据不同角色来获取其权限范围内的缴费信息
     *
     * @param paymentInfoFilterROPageRO 缴费信息参数
     * @return 支付信息详情分页列表
     */
    @PostMapping("/get_pagyinfo_data")
    public SaResult getPayInfoByRole(@RequestBody PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoFilterROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(paymentInfoFilterROPageRO.getEntity())) {
            paymentInfoFilterROPageRO.setEntity(new PaymentInfoFilterRO());
        }

        // 生成缓存键
        String cacheKey = StpUtil.getLoginIdAsString() + "paymentInfos:" + paymentInfoFilterROPageRO.toString();

        // 从Redis中尝试获取缓存
        PageVO<FilterDataVO> filterDataVO = (PageVO<FilterDataVO>) redisTemplate.opsForValue().get(cacheKey);

        if (filterDataVO == null) {

            List<String> roleList = StpUtil.getRoleList();
            // 获取访问者 ID
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    // 查询二级学院管理员权限范围内的教学计划
                    FilterDataVO paymentInfoVOPageVO = null;
                    paymentInfoVOPageVO = paymentInfoService.
                            allPageQueryPayInfoFilter(paymentInfoFilterROPageRO, collegeAdminFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(paymentInfoVOPageVO.getData());
                    filterDataVO.setTotal(paymentInfoVOPageVO.getTotal());
                    filterDataVO.setCurrent(paymentInfoFilterROPageRO.getPageNumber());
                    filterDataVO.setSize(paymentInfoFilterROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) paymentInfoVOPageVO.getData().size()
                            / paymentInfoFilterROPageRO.getPageSize()));

                    // 数据校验
                    if (Objects.isNull(filterDataVO)) {
                        throw dataNotFoundError();
                    }
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                        || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                        || roleList.contains(ADMISSION_ADMIN.getRoleName())
                ) {
                    // 查询继续教育管理员权限范围内的教学计划
                    FilterDataVO paymentInfoVOPageVO = null;
                    paymentInfoVOPageVO = paymentInfoService.
                            allPageQueryPayInfoFilter(paymentInfoFilterROPageRO, managerFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(paymentInfoVOPageVO.getData());
                    filterDataVO.setTotal(paymentInfoVOPageVO.getTotal());
                    filterDataVO.setCurrent(paymentInfoFilterROPageRO.getPageNumber());
                    filterDataVO.setSize(paymentInfoFilterROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) paymentInfoVOPageVO.getData().size()
                            / paymentInfoFilterROPageRO.getPageSize()));

                    // 数据校验
                    if (Objects.isNull(filterDataVO)) {
                        throw dataNotFoundError();
                    }
                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                    // 查询继续教育管理员权限范围内的教学计划
                    FilterDataVO paymentInfoVOPageVO = null;
                    paymentInfoVOPageVO = paymentInfoService.
                            allPageQueryPayInfoFilter(paymentInfoFilterROPageRO, teachingPointFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(paymentInfoVOPageVO.getData());
                    filterDataVO.setTotal(paymentInfoVOPageVO.getTotal());
                    filterDataVO.setCurrent(paymentInfoFilterROPageRO.getPageNumber());
                    filterDataVO.setSize(paymentInfoFilterROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) paymentInfoVOPageVO.getData().size()
                            / paymentInfoFilterROPageRO.getPageSize()));

                    // 数据校验
                    if (Objects.isNull(filterDataVO)) {
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
     * 根据不同角色来获取其权限范围内的缴费信息
     *
     * @param paymentInfoFilterROPageRO 缴费信息参数
     * @return 支付信息详情分页列表
     */
    @PostMapping("/get_paymentInfo_data_new_student")
    public SaResult getNewStudentPayInfoByRole(@RequestBody PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoFilterROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(paymentInfoFilterROPageRO.getEntity())) {
            paymentInfoFilterROPageRO.setEntity(new PaymentInfoFilterRO());
        }

        // 生成缓存键
        String cacheKey = StpUtil.getLoginIdAsString() + "newPaymentInfos:" + paymentInfoFilterROPageRO;

        // 从Redis中尝试获取缓存
        PageVO<FilterDataVO> filterDataVO = (PageVO<FilterDataVO>) redisTemplate.opsForValue().get(cacheKey);

        if (filterDataVO == null) {
            FilterDataVO paymentInfoVOPageVO = null;
            List<String> roleList = StpUtil.getRoleList();
            // 获取访问者 ID
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    // 查询二级学院管理员权限范围内的教学计划
                    paymentInfoVOPageVO = paymentInfoService.
                            allPageQueryNewStudentPayInfoFilter(paymentInfoFilterROPageRO, collegeAdminFilter);
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                        || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                        || roleList.contains(ADMISSION_ADMIN.getRoleName())
                ) {
                    // 查询继续教育管理员权限范围内的教学计划
                    paymentInfoVOPageVO = paymentInfoService.
                            allPageQueryNewStudentPayInfoFilter(paymentInfoFilterROPageRO, managerFilter);
                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                    // 查询继续教育管理员权限范围内的教学计划
                    paymentInfoVOPageVO = paymentInfoService.
                            allPageQueryNewStudentPayInfoFilter(paymentInfoFilterROPageRO, teachingPointFilter);
                }
                // 创建并返回分页信息
                filterDataVO = new PageVO<>(paymentInfoVOPageVO.getData());
                filterDataVO.setTotal(paymentInfoVOPageVO.getTotal());
                filterDataVO.setCurrent(paymentInfoFilterROPageRO.getPageNumber());
                filterDataVO.setSize(paymentInfoFilterROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) paymentInfoVOPageVO.getData().size()
                        / paymentInfoFilterROPageRO.getPageSize()));

                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
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
     * 根据教务员获取筛选缴费参数
     *
     * @return 缴费参数
     */
    @GetMapping("/get_select_args_admin")
    public SaResult getPaymentInformationArgsByCollege() {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        PaymentInformationSelectArgs paymentInformationSelectArgs = null;

        // 生成缓存键
        String cacheKey = "paymentInformationSelectArgsAdmin:" + loginId.toString();

        // 尝试从Redis中获取数据
        paymentInformationSelectArgs = (PaymentInformationSelectArgs) redisTemplate.opsForValue().get(cacheKey);

        if (paymentInformationSelectArgs == null) {
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    paymentInformationSelectArgs = paymentInfoService.getStudentStatusArgs((String) loginId, collegeAdminFilter);
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                        || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                        || roleList.contains(ADMISSION_ADMIN.getRoleName())
                ) {
                    paymentInformationSelectArgs = paymentInfoService.getStudentStatusArgs((String) loginId, managerFilter);
                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                    paymentInformationSelectArgs = paymentInfoService.getStudentStatusArgs((String) loginId, teachingPointFilter);
                }

                // 如果获取的数据不为空，则放入Redis
                if (paymentInformationSelectArgs != null) {
                    // 设置10小时超时
                    redisTemplate.opsForValue().set(cacheKey, paymentInformationSelectArgs, 10, TimeUnit.HOURS);
                }
            }
        }

        return SaResult.data(paymentInformationSelectArgs);
    }

    /**
     * 根据新生缴费数据筛选参数
     *
     * @return 缴费参数
     */
    @PostMapping("/get_new_student_payment_info_select_args_admin")
    public SaResult getNewStudentPaymentInformationArgsByCollege(@RequestBody PaymentInfoFilterRO paymentInfoFilterRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        PaymentInformationSelectArgs paymentInformationSelectArgs = null;

        // 生成缓存键
        String cacheKey = "newStudentPaymentInformationSelectArgsAdmin:" + loginId.toString();

        // 尝试从Redis中获取数据
        paymentInformationSelectArgs = (PaymentInformationSelectArgs) redisTemplate.opsForValue().get(cacheKey);

        if (paymentInformationSelectArgs == null) {
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    paymentInformationSelectArgs = paymentInfoService.getNewStudentPaymentInfoArgs(paymentInfoFilterRO, collegeAdminFilter);
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                        || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                        || roleList.contains(ADMISSION_ADMIN.getRoleName())
                ) {
                    paymentInformationSelectArgs = paymentInfoService.getNewStudentPaymentInfoArgs(paymentInfoFilterRO, managerFilter);
                } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                    paymentInformationSelectArgs = paymentInfoService.getNewStudentPaymentInfoArgs(paymentInfoFilterRO, teachingPointFilter);
                }

                // 如果获取的数据不为空，则放入Redis
                if (paymentInformationSelectArgs != null) {
                    // 设置10小时超时
                    redisTemplate.opsForValue().set(cacheKey, paymentInformationSelectArgs, 10, TimeUnit.HOURS);
                }
            }
        }

        return SaResult.data(paymentInformationSelectArgs);
    }

    /**
     * 采用消息队列来处理数据导出
     * 根据二级学院教务员获取筛选参数
     *
     * @return 缴费数据
     */
    @PostMapping("/batch_export_payment_data")
    public SaResult batchExportPaymentData(@RequestBody PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();
        CourseInformationSelectArgs courseInformationSelectArgs = null;
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
                boolean send = messageSender.sendExportMsg(paymentInfoFilterROPageRO, managerFilter, userId);
                if (send) {
                    return SaResult.ok("导出缴费数据成功");
                }
            }
        }
        return SaResult.error("导出缴费数据失败！");
    }

    /**
     * 导出新生缴费数据
     * @return 缴费数据
     */
    @PostMapping("/batch_export_new_student_payment_data")
    public SaResult batchExportNewStudentPaymentData(@RequestBody PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
                if(platformMessagePO != null){
                    paymentInfoFilterROPageRO.getEntity().setCollege(scnuXueliTools.getUserBelongCollege().getCollegeName());

                    paymentInfoService.exportNewStudentFeeData(paymentInfoFilterROPageRO.getEntity(),
                            platformMessagePO, StpUtil.getLoginIdAsString());
                    return SaResult.ok("导出新生缴费数据成功，请在消息中查看");
                }
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                    || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                    || roleList.contains(ADMISSION_ADMIN.getRoleName())
            ) {
                // 继续教育学院管理员
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
                if(platformMessagePO != null){
                    paymentInfoService.exportNewStudentFeeData(paymentInfoFilterROPageRO.getEntity(),
                            platformMessagePO, StpUtil.getLoginIdAsString());
                    return SaResult.ok("导出新生缴费数据成功，请在消息中查看");
                }
            }else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                // 继续教育学院管理员
                paymentInfoFilterROPageRO.getEntity().setTeachingPoint(scnuXueliTools.getUserBelongTeachingPoint().getTeachingPointName());
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
                if(platformMessagePO != null){
                    paymentInfoService.exportNewStudentFeeData(paymentInfoFilterROPageRO.getEntity(),
                            platformMessagePO, StpUtil.getLoginIdAsString());
                    return SaResult.ok("导出新生缴费数据成功，请在消息中查看");
                }
            }
        }
        return SaResult.error("导出新生缴费数据失败！");
    }

    /**
     * 导出新生未缴费数据
     * @return 缴费数据
     */
    @PostMapping("/batch_export_new_student_not_pay_data")
    public SaResult batchExportNewStudentNotPayData(@RequestBody PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
                if(platformMessagePO != null){
                    paymentInfoFilterROPageRO.getEntity().setCollege(scnuXueliTools.getUserBelongCollege().getCollegeName());
                    paymentInfoService.exportNewStudentNotPayData(paymentInfoFilterROPageRO.getEntity(),
                            platformMessagePO, StpUtil.getLoginIdAsString());
                    return SaResult.ok("导出新生未缴费数据成功，请在消息中查看");
                }
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())
                    || roleList.contains(CAIWUBU_ADMIN.getRoleName())
                    || roleList.contains(ADMISSION_ADMIN.getRoleName())
            ) {
                // 继续教育学院管理员
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
                if(platformMessagePO != null){
                    paymentInfoService.exportNewStudentNotPayData(paymentInfoFilterROPageRO.getEntity(),
                            platformMessagePO, StpUtil.getLoginIdAsString());
                    return SaResult.ok("导出新生未缴费数据成功，请在消息中查看");
                }
            }else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                // 继续教育学院管理员
                paymentInfoFilterROPageRO.getEntity().setTeachingPoint(scnuXueliTools.getUserBelongTeachingPoint().getTeachingPointName());
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
                if(platformMessagePO != null){
                    paymentInfoService.exportNewStudentNotPayData(paymentInfoFilterROPageRO.getEntity(),
                            platformMessagePO, StpUtil.getLoginIdAsString());
                    return SaResult.ok("导出新生未缴费数据成功，请在消息中查看");
                }
            }
        }
        return SaResult.error("导出新生缴费数据失败！");
    }
}

