package com.scnujxjy.backendpoint.service.core_data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.handler.excel.TeacherInformationExcelListener;
import com.scnujxjy.backendpoint.inverter.core_data.TeachInformationInverter;
import com.scnujxjy.backendpoint.model.bo.TeacherInformationExcelBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 教师信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class TeacherInformationService extends ServiceImpl<TeacherInformationMapper, TeacherInformationPO> implements IService<TeacherInformationPO> {

    @Resource
    private TeachInformationInverter teachInformationInverter;

    /**
     * 根据id查询教师信息
     *
     * @param userId 教师id
     * @return 教师详细信息
     */
    public TeacherInformationVO detailById(int userId) {

        // 查询数据
        TeacherInformationPO teacherInformationPO = baseMapper.selectById(userId);
        return teachInformationInverter.po2VO(teacherInformationPO);
    }

    /**
     * 分页查询教师信息
     *
     * @param teacherInformationROPageRO 筛选查询分页参数
     * @return 分页查询的教师详细信息
     */
    public PageVO<TeacherInformationVO> pageQueryTeacherInformation(PageRO<TeacherInformationRO> teacherInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        TeacherInformationRO entity = teacherInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new TeacherInformationRO();
        }
        // 构造查询参数
        LambdaQueryWrapper<TeacherInformationPO> wrapper = Wrappers.<TeacherInformationPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getUserId()), TeacherInformationPO::getUserId, entity.getUserId())
                .like(StrUtil.isNotBlank(entity.getName()), TeacherInformationPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getGender()), TeacherInformationPO::getGender, entity.getGender())
                .eq(Objects.nonNull(entity.getBirthDate()), TeacherInformationPO::getBirthDate, entity.getBirthDate())
                .eq(StrUtil.isNotBlank(entity.getPoliticalStatus()), TeacherInformationPO::getPoliticalStatus, entity.getPoliticalStatus())
                .eq(StrUtil.isNotBlank(entity.getEducation()), TeacherInformationPO::getEducation, entity.getEducation())
                .eq(StrUtil.isNotBlank(entity.getDegree()), TeacherInformationPO::getDegree, entity.getDegree())
                .eq(StrUtil.isNotBlank(entity.getProfessionalTitle()), TeacherInformationPO::getProfessionalTitle, entity.getProfessionalTitle())
                .eq(StrUtil.isNotBlank(entity.getTitleLevel()), TeacherInformationPO::getTitleLevel, entity.getTitleLevel())
                .eq(StrUtil.isNotBlank(entity.getGraduationSchool()), TeacherInformationPO::getGraduationSchool, entity.getGraduationSchool())
                .eq(StrUtil.isNotBlank(entity.getCurrentPosition()), TeacherInformationPO::getCurrentPosition, entity.getCurrentPosition())
                .eq(StrUtil.isNotBlank(entity.getCollegeId()), TeacherInformationPO::getCollegeId, entity.getCollegeId())
                .eq(StrUtil.isNotBlank(entity.getTeachingPoint()), TeacherInformationPO::getTeachingPoint, entity.getTeachingPoint())
                .eq(StrUtil.isNotBlank(entity.getAdministrativePosition()), TeacherInformationPO::getAdministrativePosition, entity.getAdministrativePosition())
                .eq(StrUtil.isNotBlank(entity.getWorkNumber()), TeacherInformationPO::getWorkNumber, entity.getWorkNumber())
                .eq(StrUtil.isNotBlank(entity.getIdCardNumber()), TeacherInformationPO::getIdCardNumber, entity.getIdCardNumber())
                .eq(StrUtil.isNotBlank(entity.getPhone()), TeacherInformationPO::getPhone, entity.getPhone())
                .eq(StrUtil.isNotBlank(entity.getEmail()), TeacherInformationPO::getEmail, entity.getEmail())
                .eq(StrUtil.isNotBlank(entity.getStartTerm()), TeacherInformationPO::getStartTerm, entity.getStartTerm())
                .eq(StrUtil.isNotBlank(entity.getTeacherType1()), TeacherInformationPO::getTeacherType1, entity.getTeacherType1())
                .eq(StrUtil.isNotBlank(entity.getTeacherType2()), TeacherInformationPO::getTeacherType2, entity.getTeacherType2())
                .last(StrUtil.isNotBlank(teacherInformationROPageRO.getOrderBy()), teacherInformationROPageRO.lastOrderSql());

        // 区分列表查询还是分页查询，并返回结果
        if (Objects.equals(true, teacherInformationROPageRO.getIsAll())) {
            List<TeacherInformationPO> teacherInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(teachInformationInverter.po2VO(teacherInformationPOS));
        } else {
            Page<TeacherInformationPO> teacherInformationPOPage = baseMapper.selectPage(teacherInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(teacherInformationPOPage, teachInformationInverter.po2VO(teacherInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据userId更新教师信息
     *
     * @param teacherInformationRO 教师信息
     * @return 更新后的教师信息
     */
    public TeacherInformationVO editById(TeacherInformationRO teacherInformationRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationRO) || Objects.isNull(teacherInformationRO.getUserId())) {
            log.error("参数错误");
            return null;
        }
        // 更新数据
        TeacherInformationPO teacherInformationPO = teachInformationInverter.ro2PO(teacherInformationRO);
        int count = baseMapper.updateById(teacherInformationPO);
        if (count <= 0) {
            log.error("更新失败，数据：{}", teacherInformationPO);
            return null;
        }
        return detailById(teacherInformationRO.getUserId());
    }

    /**
     * 根据userId删除教师信息
     *
     * @param userId 教师id
     * @return 删除信息的数量
     */
    public Integer deleteById(String userId) {
        // 参数校验
        if (Objects.isNull(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(userId);
        if (count <= 0) {
            log.error("删除失败，userId：{}", userId);
            return null;
        }
        return count;
    }

    /**
     * 解析Excel文件中的数据并导入到数据库中
     *
     * @param file excel文件
     * @return 导入的数据
     */
    public List<TeacherInformationVO> excelImportTeacherInformation(MultipartFile file) {
        TeacherInformationExcelListener listener = new TeacherInformationExcelListener();
        try (InputStream inputStream = file.getInputStream();
             ExcelReader reader = EasyExcel.read(inputStream, TeacherInformationExcelBO.class, listener).build();) {
            // 读取第一张表即可，后面有字段说明
            reader.read(EasyExcel.readSheet().sheetNo(0).build());
            List<TeacherInformationExcelBO> dataList = listener.getDataList();
            if (CollUtil.isNotEmpty(dataList)) {
                return teachInformationInverter.excelBO2VO(dataList);
            }
        } catch (Exception e) {
            log.error("解析表格出现错误", e);
            return null;
        }
        return null;
    }
}
