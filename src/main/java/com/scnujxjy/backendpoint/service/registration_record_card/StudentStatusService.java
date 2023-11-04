package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.*;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.*;
import com.scnujxjy.backendpoint.inverter.registration_record_card.StudentStatusInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusTeacherFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.*;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.TeacherFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Abs;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 学籍信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
@Slf4j
public class StudentStatusService extends ServiceImpl<StudentStatusMapper, StudentStatusPO> implements IService<StudentStatusPO> {
    @Resource
    private StudentStatusInverter studentStatusInverter;

    @Resource
    private StudentStatusMapper studentStatusMapper;
    @Resource
    private OriginalEducationInfoMapper originalEducationInfoMapper;
    @Resource
    private PersonalInfoMapper personalInfoMapper;
    @Resource
    private DegreeInfoMapper degreeInfoMapper;
    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Resource
    private MinioService minioService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据id查询学籍信息
     *
     * @param id 学籍信息id
     * @return 学籍信息
     */
    public StudentStatusVO detailById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询
        StudentStatusPO studentStatusPO = baseMapper.selectById(id);
        return studentStatusInverter.po2VO(studentStatusPO);
    }

    /**
     * 分页查询学籍信息
     *
     * @param studentStatusROPageRO 分页参数
     * @return 学籍信息列表
     */
    public PageVO<StudentStatusVO> pageQueryStudentStatus(PageRO<StudentStatusRO> studentStatusROPageRO) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        StudentStatusRO entity = studentStatusROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new StudentStatusRO();
        }
        // 构建查询参数
        LambdaQueryWrapper<StudentStatusPO> wrapper = Wrappers.<StudentStatusPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), StudentStatusPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getStudentNumber()), StudentStatusPO::getStudentNumber, entity.getStudentNumber())
                .eq(StrUtil.isNotBlank(entity.getGrade()), StudentStatusPO::getGrade, entity.getGrade())
                .eq(StrUtil.isNotBlank(entity.getCollege()), StudentStatusPO::getCollege, entity.getCollege())
                .eq(StrUtil.isNotBlank(entity.getTeachingPoint()), StudentStatusPO::getTeachingPoint, entity.getTeachingPoint())
                .eq(StrUtil.isNotBlank(entity.getMajorName()), StudentStatusPO::getMajorName, entity.getMajorName())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), StudentStatusPO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getLevel()), StudentStatusPO::getLevel, entity.getLevel())
                .eq(StrUtil.isNotBlank(entity.getStudyDuration()), StudentStatusPO::getStudyDuration, entity.getStudyDuration())
                .eq(StrUtil.isNotBlank(entity.getAdmissionNumber()), StudentStatusPO::getAdmissionNumber, entity.getAdmissionNumber())
                .eq(StrUtil.isNotBlank(entity.getAcademicStatus()), StudentStatusPO::getAcademicStatus, entity.getAcademicStatus())
                .eq(Objects.nonNull(entity.getEnrollmentDate()), StudentStatusPO::getEnrollmentDate, entity.getEnrollmentDate())
                .last(StrUtil.isNotBlank(studentStatusROPageRO.getOrderBy()), studentStatusROPageRO.lastOrderSql());

        // 列表查询 或 分页查询 并返回结果
        if (Objects.equals(true, studentStatusROPageRO.getIsAll())) {
            List<StudentStatusPO> studentStatusPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(studentStatusInverter.po2VO(studentStatusPOS));
        } else {
            Page<StudentStatusPO> studentStatusPOPage = baseMapper.selectPage(studentStatusROPageRO.getPage(), wrapper);
            return new PageVO<>(studentStatusPOPage, studentStatusInverter.po2VO(studentStatusPOPage.getRecords()));
        }
    }

    /**
     * 更新学籍信息
     *
     * @param studentStatusRO 学籍信息
     * @return 更新后的学籍信息
     */
    public StudentStatusVO editById(StudentStatusRO studentStatusRO) {
        // 校验参数
        if (Objects.isNull(studentStatusRO) || Objects.isNull(studentStatusRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新学籍信息
        StudentStatusPO studentStatusPO = studentStatusInverter.ro2PO(studentStatusRO);
        int count = baseMapper.updateById(studentStatusPO);
        if (count <= 0) {
            log.error("更新失败，数据：{}", studentStatusPO);
            return null;
        }

        return detailById(studentStatusRO.getId());
    }

    /**
     * 删除学籍信息
     *
     * @param id 学籍信息id
     * @return 删除学籍信息的数量
     */
    public Integer deleteById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除学籍信息
        int count = baseMapper.deleteById(id);
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        return count;
    }

    /**
     * 根据学生的身份证号码查询其学籍信息，包含：
     * 班级信息、学籍信息、原学历信息、毕业信息、学位信息、入学照片与毕业照片
     * @param studentId 学生证件号码
     * @return
     */
    public List<StudentAllStatusInfoVO> statusInfoByIdNumber(String studentId) {
        // 尝试从Redis中获取数据
        String redisKey = "studentStatus:" + studentId;
        List<StudentAllStatusInfoVO> cachedData = (List<StudentAllStatusInfoVO>)redisTemplate.opsForValue().get(redisKey);

        if (cachedData != null) {
            // 如果Redis中有数据，直接返回

            return cachedData;
        }



        List<StudentAllStatusInfoVO> studentAllStatusInfoVOS = new ArrayList<>();

        List<StudentStatusVO> studentStatusPOS = studentStatusMapper.selectStudentByidNumber(studentId);
        for(StudentStatusVO studentStatusVO: studentStatusPOS){
            String grade = studentStatusVO.getGrade();
            List<PersonalInfoVO> personalInfoVOS = personalInfoMapper.
                    selectInfoByGradeAndIdNumber(grade, studentId);
            if(personalInfoVOS.size() > 1){
                throw new RuntimeException("该学生的个人信息在 同一个年级中出现了两条记录 " + studentId);
            }else{
                List<OriginalEducationInfoVO> originalEducationInfoVOS = originalEducationInfoMapper.
                        selectInfoByGradeAndIdNumber(grade, studentId);
                if(originalEducationInfoVOS.size() > 1){
                    throw new RuntimeException("该学生的原学历信息在 同一个年级中出现了两条记录 " + studentId);
                }else {
                    List<GraduationInfoVO> graduationInfoVOS = graduationInfoMapper.
                            selectInfoByGradeAndIdNumber(grade, studentId);
                    if (graduationInfoVOS.size() > 1) {
                        throw new RuntimeException("该学生的毕业信息在 同一个年级中出现了两条记录 " + studentId);
                    } else {
                        List<DegreeInfoVO> degreeInfoVOS = degreeInfoMapper.
                                selectInfoByGradeAndIdNumber(studentStatusVO.getStudentNumber());
                        if (degreeInfoVOS.size() > 1) {
                            throw new RuntimeException("该学生的学位信息在 同一个年级中出现了两条记录 " + studentId);
                        } else {
                            List<AdmissionInformationPO> admissionInformationPOS =
                                    admissionInformationMapper.selectInfoByGradeAndIdNumber(grade, studentId);
                            if (admissionInformationPOS.size() > 1) {
                                throw new RuntimeException("该学生的新生录取信息在 同一个年级中出现了两条记录 " + studentId);
                            } else {
                                StudentAllStatusInfoVO studentAllStatusInfoVO = new StudentAllStatusInfoVO();
                                studentAllStatusInfoVO.setStudentStatusVO(studentStatusVO);
                                studentAllStatusInfoVO.setPersonalInfoVO(
                                        personalInfoVOS.size() > 0 ? personalInfoVOS.get(0) : null);
                                studentAllStatusInfoVO.setOriginalEducationInfoVO(
                                        originalEducationInfoVOS.size() > 0 ? originalEducationInfoVOS.get(0) : null);
                                studentAllStatusInfoVO.setGraduationInfoVO(
                                        graduationInfoVOS.size() > 0 ? graduationInfoVOS.get(0) : null);
                                studentAllStatusInfoVO.setDegreeInfoVO(
                                        degreeInfoVOS.size() > 0 ? degreeInfoVOS.get(0) : null);

                                AdmissionInformationVO admissionInformationVO = new AdmissionInformationVO();
                                if (admissionInformationPOS.size() > 0) {
                                    // 获取录取信息
                                    BeanUtils.copyProperties(admissionInformationPOS.get(0), admissionInformationVO);
                                    studentAllStatusInfoVO.setAdmissionInformationVO(admissionInformationVO);
                                } else {
                                    studentAllStatusInfoVO.setAdmissionInformationVO(null);
                                }

                                List<ClassInformationPO> classInformationPOS = classInformationMapper.selectClassByclassIdentifier(studentStatusVO.getClassIdentifier());
                                if(classInformationPOS.size() > 0){
                                    // 获取班级信息
                                    ClassInformationVO classInformationVO = new ClassInformationVO();
                                    BeanUtils.copyProperties(classInformationPOS.get(0), classInformationVO);
                                    studentAllStatusInfoVO.setClassInformationVO(classInformationVO);
                                }


                                studentAllStatusInfoVOS.add(studentAllStatusInfoVO);



                            }
                        }

                    }
                }
            }
        }

        // 将查询结果存入Redis，设置过期时间为1小时（可根据需要调整）
        redisTemplate.opsForValue().set(redisKey, studentAllStatusInfoVOS, 100, TimeUnit.HOURS);

        return studentAllStatusInfoVOS;
    }

    public byte[] getImportPhoto(String grade){
        String userName = (String) StpUtil.getLoginId();
        List<PersonalInfoVO> personalInfoVOS = personalInfoMapper.selectInfoByGradeAndIdNumber(grade, userName);
        if(personalInfoVOS.size() == 0){
            return null;
        }else if(personalInfoVOS.size() == 1){
            PersonalInfoVO personalInfoVO = personalInfoVOS.get(0);
            String entrancePhoto = personalInfoVO.getEntrancePhoto();
            String bucketName = minioService.getBucketName();
            // 去掉桶名
            entrancePhoto = entrancePhoto.replace("./" + bucketName + "/", "");

            // 现在你可以使用修改后的entrancePhoto路径
            return minioService.getImageAsBytes(entrancePhoto);
        }else{
            throw new RuntimeException("出现了多张入学照片 " + grade + " 证件号码 " + userName);
        }
    }

    public byte[] getExportPhoto(String grade){
        String userName = (String) StpUtil.getLoginId();
        List<GraduationInfoVO> graduationInfoVOS = graduationInfoMapper.selectInfoByGradeAndIdNumber(grade, userName);
        if(graduationInfoVOS.size() == 0){
            return null;
        }else if(graduationInfoVOS.size() == 1){
            GraduationInfoVO graduationInfoVO = graduationInfoVOS.get(0);
            String entrancePhoto = graduationInfoVO.getGraduationPhoto();
            String bucketName = minioService.getBucketName();
            // 去掉桶名
            entrancePhoto = entrancePhoto.replace("./" + bucketName + "/", "");

            // 现在你可以使用修改后的entrancePhoto路径
            return minioService.getImageAsBytes(entrancePhoto);
        }else{
            throw new RuntimeException("出现了多张入学照片 " + grade + " 证件号码 " + userName);
        }
    }

    /**
     * 根据 Minio 路径获取文件
     * @param degreePhotoUrl 学位图片文件地址
     * @return
     */
    public byte[] getDegreePhotoByURL(String degreePhotoUrl) {
        return minioService.getFileFromMinio(degreePhotoUrl);
    }

    /**
     * 根据传入的筛选参数 和角色筛选器来获取对应的学籍数据
     * @param studentStatusROPageRO 筛选参数
     * @param filter 角色筛选器
     * @return
     */
    public FilterDataVO allPageQueryStudentStatusFilter(PageRO<StudentStatusFilterRO> studentStatusROPageRO, AbstractFilter filter) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            log.error("参数缺失");
            return null;
        }


        return filter.filterStudentStatus(studentStatusROPageRO);
    }

    /**
     * 为不同角色导出学籍数据
     * @param pageRO
     * @param filter
     */
    public void generateBatchStudentStatusData(PageRO<StudentStatusFilterRO> pageRO, AbstractFilter filter, String userId) {
        // 校验参数
        if (Objects.isNull(pageRO)) {
            log.error("导出学籍数据参数缺失");

        }


        filter.exportStudentStatusData(pageRO, userId);
    }

    /**
     * 获取学籍数据的筛选参数
     * @param loginId 登录用户名
     * @param filter 筛选参数（如果是其他用户则需要额外的限制参数，二级学院、教师、教学点）
     * @return
     */
    public StudentStatusSelectArgs getStudentStatusArgs(String loginId, AbstractFilter filter) {
        return filter.filterStudentStatusSelectArgs();
    }

    /**
     * 为不同角色导出成绩数据
     * @param pageRO
     * @param filter
     */
    public void generateBatchScoreInformationData(PageRO<ScoreInformationFilterRO> pageRO, AbstractFilter filter, String userId) {
        // 校验参数
        if (Objects.isNull(pageRO)) {
            log.error("导出学籍数据参数缺失");

        }


        filter.exportScoreInformationData(pageRO, userId);
    }

    public FilterDataVO getStudentStatusInfoByTeacher(PageRO<StudentStatusTeacherFilterRO> studentStatusROPageRO, AbstractFilter filter) {
        return filter.getStudentStatusInfoByTeacher(studentStatusROPageRO);
    }
}
