package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.SECOND_COLLEGE_ADMIN;
import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.XUELIJIAOYUBU_ADMIN;
import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 班级信息管理
 *
 * @author leopard
 * @since 2023-08-14
 */
@RestController
@RequestMapping("/class-information")
public class ClassInformationController {

    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private MessageSender messageSender;


    /**
     * 根据id查询班级信息
     *
     * @param id 班级信息id
     * @return 班级详细信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 校验信息
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        ClassInformationVO classInformationVO = classInformationService.detailById(id);
        // 数据校验
        if (Objects.isNull(classInformationVO)) {
            throw dataNotFoundError();
        }
        // 转换数据并返回
        return SaResult.data(classInformationVO);
    }

    /**
     * 分页查询班级信息
     *
     * @param classInformationROPageRO 班级信息分页查询参数
     * @return 班级信息分页查询结果
     */
    @PostMapping("/page")
    public SaResult pageQueryClassInformation(@RequestBody PageRO<ClassInformationRO> classInformationROPageRO) {
        // 校验信息
        if (Objects.isNull(classInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(classInformationROPageRO.getEntity())) {
            classInformationROPageRO.setEntity(new ClassInformationRO());
        }
        // 分页查询
        PageVO<ClassInformationVO> classInformationVOPage = classInformationService.pageQueryClassInformation(classInformationROPageRO);
        // 数据校验
        if (Objects.isNull(classInformationVOPage)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(classInformationVOPage);
    }

    /**
     * 根据id更新班级信息
     *
     * @param classInformationRO 班级信息
     * @return
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody ClassInformationRO classInformationRO) {
        // 数据校验
        if (Objects.isNull(classInformationRO) || Objects.isNull(classInformationRO.getId())) {
            throw dataMissError();
        }
        // 更新数据
        ClassInformationVO classInformationVO = classInformationService.editById(classInformationRO);
        // 检查更新结果
        if (Objects.isNull(classInformationVO)) {
            throw dataUpdateError();
        }
        // 返回更新后的数据
        return SaResult.data(classInformationVO);
    }

    /**
     * 根据id删除班级信息
     *
     * @param id 班级信息id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 校验信息
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除数据
        int count = classInformationService.deleteById(id);
        // 检查删除结果
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回删除结果
        return SaResult.data(count);
    }

    /**
     * 根据不同角色来获取其权限范围内的班级信息
     *
     * @param classInformationFilterROPageRO 班级信息参数
     * @return 支付信息详情分页列表
     */
    @PostMapping("/get_class_information_data")
    public SaResult getClassInfoByRole(@RequestBody PageRO<ClassInformationFilterRO> classInformationFilterROPageRO) {
        // 参数校验
        if (Objects.isNull(classInformationFilterROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(classInformationFilterROPageRO.getEntity())) {
            classInformationFilterROPageRO.setEntity(new ClassInformationFilterRO());
        }

        // 生成缓存键
        String cacheKey = "classInfos:" + classInformationFilterROPageRO.toString();

        // 从Redis中尝试获取缓存
        PageVO<FilterDataVO> filterDataVO = (PageVO<FilterDataVO>) redisTemplate.opsForValue().get(cacheKey);

        if (filterDataVO == null) {

            List<String> roleList = StpUtil.getRoleList();
            // 获取访问者 ID
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {

                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                    // 查询继续教育管理员权限范围内的班级信息
                    FilterDataVO filterDataVO1 = null;
                    filterDataVO1 = classInformationService.allPageQueryPayInfoFilter(classInformationFilterROPageRO, managerFilter);
                    // 创建并返回分页信息
                    filterDataVO = new PageVO<>(filterDataVO1.getData());
                    filterDataVO.setTotal(filterDataVO1.getTotal());
                    filterDataVO.setCurrent(classInformationFilterROPageRO.getPageNumber());
                    filterDataVO.setSize(classInformationFilterROPageRO.getPageSize());
                    filterDataVO.setPages((long) Math.ceil((double) filterDataVO1.getData().size()
                            / classInformationFilterROPageRO.getPageSize()));

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
     * 根据教务员获取筛选缴费参数
     *
     * @return 教学计划
     */
    @GetMapping("/get_select_args_admin")
    public SaResult getPaymentInformationArgsByCollege() {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        ClassInformationSelectArgs classInformationSelectArgs = null;

        // 生成缓存键
        String cacheKey = "classInformationSelectArgsAdmin:" + loginId.toString();

        // 尝试从Redis中获取数据
        classInformationSelectArgs = (ClassInformationSelectArgs) redisTemplate.opsForValue().get(cacheKey);

        if (classInformationSelectArgs == null) {
            if (roleList.isEmpty()) {
                throw dataNotFoundError();
            } else {
                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                    classInformationSelectArgs = classInformationService.getClassInformationArgs((String) loginId, collegeAdminFilter);
                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                    classInformationSelectArgs = classInformationService.getClassInformationArgs((String) loginId, managerFilter);
                }

                // 如果获取的数据不为空，则放入Redis
                if (classInformationSelectArgs != null) {
                    // 设置10小时超时
                    redisTemplate.opsForValue().set(cacheKey, classInformationSelectArgs, 10, TimeUnit.HOURS);
                }
            }
        }

        return SaResult.data(classInformationSelectArgs);
    }


    /**
     * 采用消息队列来处理班级数据导出
     * 根据二级学院教务员获取筛选参数
     *
     * @return 班级数据的 excel
     */
    @PostMapping("/batch_export_class_information_data")
    public SaResult batchExportClassInformationData(@RequestBody PageRO<ClassInformationFilterRO> classInformationFilterROPageRO) {
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
                boolean send = messageSender.sendExportMsg(classInformationFilterROPageRO, managerFilter, userId);
                if(send){
                    return SaResult.ok("导出班级数据成功");
                }
            }
        }
        return SaResult.error("导出班级数据失败！");
    }

}

