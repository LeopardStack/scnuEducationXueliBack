package com.scnujxjy.backendpoint.service.core_data;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.constant.enums.TeacherTypeEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.inverter.core_data.TeachInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherQueryArgsVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherSelectVO;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.UserUploadsService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.excelListener.TeacherInformationListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private UserUploadsService userUploadsService;

    @Resource
    private MinioService minioService;

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

        List<TeacherInformationVO> teacherInformationVOList =  getBaseMapper().
                selectTeacherInformationWithAccountInfo(teacherInformationROPageRO.getEntity(),
                        teacherInformationROPageRO.getPageSize(),
                        teacherInformationROPageRO.getPageSize()*(teacherInformationROPageRO.getPageNumber() - 1)
                        );
        PageVO<TeacherInformationVO> pageVO = new PageVO<>();
        pageVO.setCurrent(teacherInformationROPageRO.getPageNumber());
        pageVO.setRecords(teacherInformationVOList);
        pageVO.setTotal(getBaseMapper().selectTeacherInformationWithAccountInfoCount(teacherInformationROPageRO.getEntity()));
        pageVO.setSize(teacherInformationROPageRO.getPageSize());

        return pageVO;
    }

    /**
     * 根据userId更新教师信息
     *
     * @param teacherInformationRO 教师信息
     * @return 更新后的教师信息
     */
    public SaResult editById(TeacherInformationRO teacherInformationRO) {
        try {
            // 显示声明 从写数据库中 读取数据 因为从数据更新有延迟
            HintManager.getInstance().setWriteRouteOnly();
        TeacherInformationPO teacherInformationPO1 = getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getUserId, teacherInformationRO.getUserId()));
        if(teacherInformationPO1 == null){
            return ResultCode.TEACHER_INFORMATION_FAIL7.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(teacherInformationRO.getName())){
            return ResultCode.TEACHER_INFORMATION_FAIL1.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(teacherInformationRO.getTeacherType2())){
            return ResultCode.TEACHER_INFORMATION_FAIL2.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(teacherInformationRO.getIdCardNumber()) && StrUtil.isEmpty(teacherInformationRO.getWorkNumber())){
            return ResultCode.TEACHER_INFORMATION_FAIL3.generateErrorResultInfo();
        }

        TeacherInformationPO teacherInformationPO = new TeacherInformationPO()
                .setName(teacherInformationRO.getName())
                .setGender(teacherInformationRO.getGender())
                .setBirthDate(teacherInformationRO.getBirthDate())
                .setPoliticalStatus(teacherInformationRO.getPoliticalStatus())
                .setEducation(teacherInformationRO.getEducation())
                .setDegree(teacherInformationRO.getDegree())
                .setProfessionalTitle(teacherInformationRO.getProfessionalTitle())
                .setTitleLevel(teacherInformationRO.getTitleLevel())
                .setGraduationSchool(teacherInformationRO.getGraduationSchool())
                .setCurrentPosition(teacherInformationRO.getCurrentPosition())
                .setCollegeId(teacherInformationRO.getCollege())
                .setTeachingPoint(teacherInformationRO.getTeachingPoint())
                .setAdministrativePosition(teacherInformationRO.getAdministrativePosition())
                .setWorkNumber(teacherInformationRO.getWorkNumber())
                .setIdCardNumber(teacherInformationRO.getIdCardNumber())
                .setPhone(teacherInformationRO.getPhone())
                .setEmail(teacherInformationRO.getEmail())
                .setStartTerm(teacherInformationRO.getStartTerm())
                .setTeacherType1(teacherInformationRO.getTeacherType1())
                .setTeacherType2(teacherInformationRO.getTeacherType2())
                .setUserId(teacherInformationRO.getUserId())
                ;

        String username = null;
        if(!StrUtil.isEmpty(teacherInformationPO.getWorkNumber())){
            username = "T" + teacherInformationPO.getWorkNumber();
        }

        if(username == null && !StrUtil.isEmpty(teacherInformationPO.getIdCardNumber())){
            username = "T" + teacherInformationPO.getIdCardNumber();
        }

        if(username == null){
            return ResultCode.TEACHER_INFORMATION_FAIL4.generateErrorResultInfo();
        }
        teacherInformationPO.setTeacherUsername(username);

        if(username.length() < 6){
            return ResultCode.TEACHER_INFORMATION_FAIL6.generateErrorResultInfo();
        }

        int i1 = getBaseMapper().updateById(teacherInformationPO);
        if(i1 <= 0){
            return ResultCode.TEACHER_INFORMATION_FAIL8.generateErrorResultInfo();
        }

        // 为新老师生成平台账户
        String password=teacherInformationPO.getTeacherUsername().substring(teacherInformationPO.getTeacherUsername().length()-6);
        String encryptedPassword = new SM3().digestHex(password);

        PlatformUserPO platformUserPO = new PlatformUserPO()
                .setRoleId(RoleEnum.TEACHER.getRoleId())
                .setUsername(teacherInformationPO.getTeacherUsername())
                .setName(teacherInformationPO.getName())
                .setPassword(encryptedPassword)
                ;


        Integer i = platformUserService.getBaseMapper().selectCount(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, teacherInformationPO1.getTeacherUsername()));
        // 当平台存在教师账号 并且该教师账号也需要修改时 则执行这个
        if(i > 0 && username.equals(teacherInformationPO1.getTeacherUsername())){
            // 说明该教师存在平台账户，先删除账号 再创建新的
            int delete = platformUserService.getBaseMapper().delete(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUsername, teacherInformationPO1.getTeacherUsername()));
            int insert = platformUserService.getBaseMapper().insert(platformUserPO);
            if(insert <= 0){
                return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
            }
        }else{
            int insert = platformUserService.getBaseMapper().insert(platformUserPO);
            if(insert <= 0){
                return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
            }
        }
        } finally {
            HintManager.clear();
        }

        return SaResult.ok("修改教师信息成功");
    }

    /**
     * 根据userId删除教师信息
     *
     * @param userId 教师id
     * @return 删除信息的数量
     */
    public SaResult deleteById(Integer userId) {
        TeacherInformationPO teacherInformationPO = getBaseMapper().selectById(userId);
        int i = getBaseMapper().deleteById(userId);
        int delete = platformUserService.getBaseMapper().delete(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, teacherInformationPO.getTeacherUsername()));

        if(i > 0){
            return SaResult.ok("删除教师信息成功");
        }else if(i == 0){
            return SaResult.ok("该教师已删除 不必重复删除");
        }else{
            return ResultCode.TEACHER_INFORMATION_FAIL10.generateErrorResultInfo();
        }
    }

    /**
     * 解析Excel文件中的数据并导入到数据库中
     *
     * @param file excel文件
     * @return 导入的数据
     */
    public SaResult excelImportTeacherInformation(MultipartFile file, long uploadMsgId, String importBucketName) {
        // 创建监听器实例
        TeacherInformationListener listener = new TeacherInformationListener(getBaseMapper(), platformUserService.getBaseMapper(),
                oldDataSynchronize, userUploadsService, uploadMsgId, minioService, importBucketName);
        processTeacherListToExcel(file, listener, uploadMsgId);

        return SaResult.ok("批量导入师资表成功，请稍后查看系统上传消息处理结果");
    }

    @Async
    protected void processTeacherListToExcel(MultipartFile file, TeacherInformationListener listener, long uploadMsgId){
        listener.setUploadToMinio(true);
        int headRowNumber = 1;

        try {
            // 读取 MultipartFile 的内容
            EasyExcel.read(file.getInputStream(), TeacherInformationExcelImportVO.class, listener)
                    .sheet().headRowNumber(headRowNumber).doRead();
        } catch (IOException e) {
            UserUploadsPO userUploadsPO = userUploadsService.getBaseMapper().selectOne(new LambdaQueryWrapper<UserUploadsPO>()
                    .eq(UserUploadsPO::getId, uploadMsgId));
            log.info("处理上传文件 上传消息ID 为 " + uploadMsgId + " 出现错误 " + e);
            userUploadsPO.setResultDesc("处理上传文件出现错误");
        }
    }

    public List<Object> getTeacherInformation() {
        List<Object> distinctTeacherNames = getBaseMapper().selectObjs(
                new LambdaQueryWrapper<TeacherInformationPO>()
                        .select(TeacherInformationPO::getName)
                        .groupBy(TeacherInformationPO::getName)
        );

        return distinctTeacherNames;
    }

    /**
     * 获取主讲老师信息
     */
    public List<TeacherSelectVO> getMainTeacherInformation(){
        List<TeacherSelectVO> teacherSelectVOList = new ArrayList<>();
        List<TeacherInformationPO> teacherInformationPOS = getBaseMapper().selectList(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherType2, "主讲教师"));
        for(TeacherInformationPO teacherInformationPO: teacherInformationPOS){
            String name = teacherInformationPO.getName();
            String teacherUsername = teacherInformationPO.getTeacherUsername();
            String label = name + " " + teacherUsername;
            TeacherSelectVO teacherSelectVO = new TeacherSelectVO(label, teacherInformationPO.getUserId());
            teacherSelectVOList.add(teacherSelectVO);
        }

        return teacherSelectVOList;
    }

    /**
     * 获取辅导老师信息
     */
    public List<TeacherSelectVO> geTutorInformation(){
        List<TeacherSelectVO> teacherSelectVOList = new ArrayList<>();
        List<TeacherInformationPO> teacherInformationPOS = getBaseMapper().selectList(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherType2, "辅导教师"));
        for(TeacherInformationPO teacherInformationPO: teacherInformationPOS){
            String name = teacherInformationPO.getName();
            String teacherUsername = teacherInformationPO.getTeacherUsername();
            String label = name + " " + teacherUsername;
            TeacherSelectVO teacherSelectVO = new TeacherSelectVO(label, teacherInformationPO.getUserId());
            teacherSelectVOList.add(teacherSelectVO);
        }

        return teacherSelectVOList;
    }

    /**
     *
     * @param teacherInformationRO
     * @return
     */
    public SaResult addNewTeacher(TeacherInformationRO teacherInformationRO) {
        try {
            // 显示声明 从写数据库中 读取数据 因为从数据更新有延迟
            HintManager.getInstance().setWriteRouteOnly();

        if(StrUtil.isEmpty(teacherInformationRO.getName())){
            return ResultCode.TEACHER_INFORMATION_FAIL1.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(teacherInformationRO.getTeacherType2())){
            return ResultCode.TEACHER_INFORMATION_FAIL2.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(teacherInformationRO.getIdCardNumber()) && StrUtil.isEmpty(teacherInformationRO.getWorkNumber())){
            return ResultCode.TEACHER_INFORMATION_FAIL3.generateErrorResultInfo();
        }

        TeacherInformationPO teacherInformationPO = new TeacherInformationPO()
                .setName(teacherInformationRO.getName())
                .setGender(teacherInformationRO.getGender())
                .setBirthDate(teacherInformationRO.getBirthDate())
                .setPoliticalStatus(teacherInformationRO.getPoliticalStatus())
                .setEducation(teacherInformationRO.getEducation())
                .setDegree(teacherInformationRO.getDegree())
                .setProfessionalTitle(teacherInformationRO.getProfessionalTitle())
                .setTitleLevel(teacherInformationRO.getTitleLevel())
                .setGraduationSchool(teacherInformationRO.getGraduationSchool())
                .setCurrentPosition(teacherInformationRO.getCurrentPosition())
                .setCollegeId(teacherInformationRO.getCollege())
                .setTeachingPoint(teacherInformationRO.getTeachingPoint())
                .setAdministrativePosition(teacherInformationRO.getAdministrativePosition())
                .setWorkNumber(teacherInformationRO.getWorkNumber())
                .setIdCardNumber(teacherInformationRO.getIdCardNumber())
                .setPhone(teacherInformationRO.getPhone())
                .setEmail(teacherInformationRO.getEmail())
                .setStartTerm(teacherInformationRO.getStartTerm())
                .setTeacherType1(teacherInformationRO.getTeacherType1())
                .setTeacherType2(teacherInformationRO.getTeacherType2())
                ;

        String username = null;
        if(!StrUtil.isEmpty(teacherInformationPO.getWorkNumber())){
            username = "T" + teacherInformationPO.getWorkNumber();
        }

        if(username == null && !StrUtil.isEmpty(teacherInformationPO.getIdCardNumber())){
            username = "T" + teacherInformationPO.getIdCardNumber();
        }

        if(username == null){
            return ResultCode.TEACHER_INFORMATION_FAIL4.generateErrorResultInfo();
        }
        teacherInformationPO.setTeacherUsername(username);

        if(username.length() < 6){
            return ResultCode.TEACHER_INFORMATION_FAIL6.generateErrorResultInfo();
        }

        int i1 = getBaseMapper().insert(teacherInformationPO);
        if(i1 <= 0){
            return ResultCode.TEACHER_INFORMATION_FAIL9.generateErrorResultInfo();
        }

        // 为新老师生成平台账户
        String password=teacherInformationPO.getTeacherUsername().substring(teacherInformationPO.getTeacherUsername().length()-6);
        String encryptedPassword = new SM3().digestHex(password);

        PlatformUserPO platformUserPO = new PlatformUserPO()
                .setRoleId(RoleEnum.TEACHER.getRoleId())
                .setUsername(teacherInformationPO.getTeacherUsername())
                .setName(teacherInformationPO.getName())
                .setPassword(encryptedPassword)
                ;


        Integer i = platformUserService.getBaseMapper().selectCount(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, platformUserPO.getUsername()));
        if(i > 0){
            return ResultCode.TEACHER_INFORMATION_FAIL5.generateErrorResultInfo();
        }else{
            int insert = platformUserService.getBaseMapper().insert(platformUserPO);
            if(insert <= 0){
                return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
            }
        }
        } finally {
            HintManager.clear();
        }

        return SaResult.ok("新增教师成功");

    }

    /**
     * 获取教师筛选参数
     * @param teacherInformationRO
     * @return
     */
    public TeacherQueryArgsVO getTeacherQueryArgs(TeacherInformationRO teacherInformationRO) {
        Set<String> names = getBaseMapper().getDistincetTeacherNames(teacherInformationRO);
        List<String> teacherTypes = Arrays.stream(TeacherTypeEnum.values())
                .map(TeacherTypeEnum::getType)
                .collect(Collectors.toList());
        TeacherQueryArgsVO teacherQueryArgsVO = new TeacherQueryArgsVO()
                .setTeacherTypes(teacherTypes)
                .setNames(names);
        // 输出结果，验证是否已存储所有枚举值的类型
        return teacherQueryArgsVO;
    }
}
