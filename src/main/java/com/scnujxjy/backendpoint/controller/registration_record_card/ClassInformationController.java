package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * <p>
 * 班级信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@RestController
@RequestMapping("/class-information")
public class ClassInformationController {

    @Resource
    private ClassInformationService classInformationService;

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
}

