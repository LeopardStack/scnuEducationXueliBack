package com.scnujxjy.backendpoint.service.teaching_point;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.inverter.teaching_point.TeachingPointInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationQueryArgsVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointMangerInfoVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 教学点基础信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class TeachingPointInformationService extends ServiceImpl<TeachingPointInformationMapper, TeachingPointInformationPO> implements IService<TeachingPointInformationPO> {

    @Resource
    private TeachingPointInformationInverter teachingPointInformationInverter;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private TeachingPointAdminInformationService teachingPointAdminInformationService;

    /**
     * 根据teachingPointId查询教学点基础信息
     *
     * @param teachingPointId 教学点代码
     * @return 教学点基础信息
     */
    public TeachingPointInformationVO detailById(String teachingPointId) {
        // 参数校验
        if (StrUtil.isBlank(teachingPointId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询
        TeachingPointInformationPO teachingPointInformationPO = baseMapper.selectById(teachingPointId);
        // 转换类型并返回
        return teachingPointInformationInverter.po2VO(teachingPointInformationPO);
    }

    /**
     * 分页查询教学点基础信息
     *
     * @param teachingPointInformationROPageRO 分页查询教学点基础信息参数
     * @return 分页查询教学点基础信息数据
     */
    public PageVO<TeachingPointInformationVO> pageQueryTeachingPointInformation(PageRO<TeachingPointInformationRO> teachingPointInformationROPageRO) {
        // 数据校验
        if (Objects.isNull(teachingPointInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        TeachingPointInformationRO entity = teachingPointInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new TeachingPointInformationRO();
        }
        // 构造查询参数
        LambdaQueryWrapper<TeachingPointInformationPO> wrapper = Wrappers.<TeachingPointInformationPO>lambdaQuery()
                .eq(StrUtil.isNotBlank(entity.getTeachingPointId()), TeachingPointInformationPO::getTeachingPointId, entity.getTeachingPointId())
                .like(StrUtil.isNotBlank(entity.getTeachingPointName()), TeachingPointInformationPO::getTeachingPointName, entity.getTeachingPointName())
                .eq(StrUtil.isNotBlank(entity.getPhone()), TeachingPointInformationPO::getPhone, entity.getPhone())
                .like(StrUtil.isNotBlank(entity.getAddress()), TeachingPointInformationPO::getAddress, entity.getAddress())
                .eq(StrUtil.isNotBlank(entity.getQualificationId()), TeachingPointInformationPO::getQualificationId, entity.getQualificationId())
                .eq(StrUtil.isNotBlank(entity.getAlias()), TeachingPointInformationPO::getAlias, entity.getAlias())
                .last(StrUtil.isNotBlank(teachingPointInformationROPageRO.getOrderBy()), teachingPointInformationROPageRO.lastOrderSql());

        // 列表查询 或 分页查询 并返回结果
        if (Objects.equals(true, teachingPointInformationROPageRO.getIsAll())) {
            List<TeachingPointInformationPO> teachingPointInformationPOS = baseMapper.selectList(wrapper);
            Integer total = baseMapper.selectCount(wrapper);
            return new PageVO<>(teachingPointInformationInverter.po2VO(teachingPointInformationPOS)).setTotal(Long.valueOf(total));
        } else {
            Page<TeachingPointInformationPO> teachingPointInformationPOPage = baseMapper.selectPage(teachingPointInformationROPageRO.getPage(), wrapper);
            Integer total = baseMapper.selectCount(wrapper);
            return new PageVO<>(teachingPointInformationPOPage, teachingPointInformationInverter.po2VO(teachingPointInformationPOPage.getRecords()))
                    .setTotal(Long.valueOf(total));
        }
    }

    /**
     * 根绝teachingPointId更新教学点基本信息
     *
     * @param teachingPointInformationRO 更新的教学点基本信息
     * @return 更新后的教学点基本信息
     */
    public TeachingPointInformationVO editById(TeachingPointInformationRO teachingPointInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointInformationRO) || StrUtil.isBlank(teachingPointInformationRO.getTeachingPointId())) {
            log.error("参数缺失");
            return null;
        }
        // 转换类型
        TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationInverter.ro2PO(teachingPointInformationRO);
        // 更新数据
        int count = baseMapper.updateById(teachingPointInformationPO);
        // 校验更新数据
        if (count <= 0) {
            log.error("更新失败，数据：{}", teachingPointInformationPO);
            return null;
        }
        // 返回数据
        return detailById(teachingPointInformationRO.getTeachingPointId());
    }

    /**
     * 根据teachingPointId删除教学点基础信息
     *
     * @param teachingPointId 教学点id
     * @return 删除的数量
     */
    public Integer deleteById(String teachingPointId) {
        // 参数校验
        if (StrUtil.isBlank(teachingPointId)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(teachingPointId);
        // 删除校验
        if (count <= 0) {
            log.error("删除失败，teachingPointId：{}", teachingPointId);
            return null;
        }
        // 返回数据
        return count;
    }

    /**
     * 添加新的教学点
     * @param teachingPointInformationRO
     * @return
     */
    public SaResult addTeachingPoint(TeachingPointInformationRO teachingPointInformationRO) {
        // 对传入进来的教学点名称进行校验
        if(teachingPointInformationRO.getTeachingPointName().isEmpty()){
            return ResultCode.TEACHING_POINT_FAIL1.generateErrorResultInfo();
        }

        if(teachingPointInformationRO.getAlias().isEmpty()){
            return ResultCode.TEACHING_POINT_FAIL2.generateErrorResultInfo();
        }

        Integer exist = getBaseMapper().selectCount(new LambdaQueryWrapper<TeachingPointInformationPO>()
                .eq(TeachingPointInformationPO::getTeachingPointName, teachingPointInformationRO.getTeachingPointName()));
        if(exist > 0){
            // 说明已经存在了
            return ResultCode.TEACHING_POINT_FAIL3.generateErrorResultInfo();
        }

        // 构造一个 teachingPointId
        Long maxTeachingPointId = getBaseMapper().selectMaxTeachingPointId();

        TeachingPointInformationPO teachingPointInformationPO = new TeachingPointInformationPO()
                .setTeachingPointId(String.valueOf(maxTeachingPointId + 1))
                .setTeachingPointName(teachingPointInformationRO.getTeachingPointName())
                .setPhone(teachingPointInformationRO.getPhone())
                .setAddress(teachingPointInformationRO.getAddress())
                .setAlias(teachingPointInformationRO.getAlias());
        int insert = getBaseMapper().insert(teachingPointInformationPO);
        if(insert <= 0){
            return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
        }

        return ResultCode.generateOKInfo("成功添加教学点 " + teachingPointInformationPO.getTeachingPointName());
    }

    /**
     * 根据 教学点 ID 获取其 管理人员信息
     * @param teachingPointId
     * @return
     */
    public SaResult getTeachingPointMangerInfos(String teachingPointId) {
        TeachingPointInformationPO teachingPointInformationPO = getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
        if(teachingPointInformationPO == null){
            return ResultCode.TEACHING_POINT_FAIL4.generateErrorResultInfo();
        }

        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                .eq(TeachingPointAdminInformationPO::getTeachingPointId, teachingPointInformationPO.getTeachingPointId()));

        List<TeachingPointMangerInfoVO> teachingPointMangerInfoVOList = new ArrayList<>();

        for(TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS){
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, teachingPointAdminInformationPO.getUserId()));

            TeachingPointMangerInfoVO teachingPointMangerInfoVO = new TeachingPointMangerInfoVO()
                    .setMangerType(teachingPointAdminInformationPO.getIdentity())
                    .setName(teachingPointAdminInformationPO.getName())
                    .setUsername(platformUserPO.getUsername())
                    .setIdNumber(teachingPointAdminInformationPO.getIdCardNumber())
                    .setUserId(teachingPointAdminInformationPO.getUserId())
                    ;
            teachingPointMangerInfoVOList.add(teachingPointMangerInfoVO);
        }



        return SaResult.ok().setData(teachingPointMangerInfoVOList);
    }

    /**
     *
     * @param     teachingPointAdminInformationRO
     * @return
     */
    public SaResult addManager(TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        if(StrUtil.isEmpty(teachingPointAdminInformationRO.getTeachingPointId())){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL1.generateErrorResultInfo();
        }
        TeachingPointInformationPO teachingPointInformationPO = getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointAdminInformationRO.getTeachingPointId()));
        if(teachingPointInformationPO == null){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL2.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(teachingPointAdminInformationRO.getPhone()) &&
                StrUtil.isEmpty(teachingPointAdminInformationRO.getIdCardNumber())){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL3.generateErrorResultInfo();
        }

        String username = "";
        if(!StrUtil.isEmpty(teachingPointAdminInformationRO.getPhone())){
            username = "M" + teachingPointAdminInformationRO.getPhone();
        }else if(!StrUtil.isEmpty(teachingPointAdminInformationRO.getIdCardNumber())){
            username = "M" + teachingPointAdminInformationRO.getIdCardNumber();
        }

        if(username.length() < 6){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL4.generateErrorResultInfo();
        }

        // 为新教学点管理人员生成平台账户
        String password=username.substring(username.length()-6);
        String encryptedPassword = new SM3().digestHex(password);

        PlatformUserPO platformUserPO = new PlatformUserPO()
                .setRoleId(RoleEnum.TEACHING_POINT_ADMIN.getRoleId())
                .setName(teachingPointAdminInformationRO.getName())
                .setUsername(username)
                .setPassword(encryptedPassword)
                ;

        PlatformUserPO platformUserPO1 = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, username));
        if(platformUserPO1 != null){
            // 存在老账号 删除
            PlatformUserVO platformUserVO = platformUserService.detailById(platformUserPO1.getUserId());
        }
        int insert = platformUserService.getBaseMapper().insert(platformUserPO);

        TeachingPointAdminInformationPO teachingPointAdminInformationPO = new TeachingPointAdminInformationPO()
                .setUserId(String.valueOf(platformUserPO.getUserId()))
                .setTeachingPointId(teachingPointAdminInformationRO.getTeachingPointId())
                .setPhone(teachingPointAdminInformationRO.getPhone())
                .setIdCardNumber(teachingPointAdminInformationRO.getIdCardNumber())
                .setIdentity(teachingPointAdminInformationRO.getIdentity())
                .setName(teachingPointAdminInformationRO.getName())
                ;
        int insert1 = teachingPointAdminInformationService.getBaseMapper().insert(teachingPointAdminInformationPO);
        if(insert1 <= 0){
            return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
        }

        return SaResult.ok("新增教学点教务员成功");
    }

    /**
     *
     * @param teachingPointAdminInformationRO
     * @return
     */
    public SaResult updateManager(TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        // 校验 userId 的平台账户是否存在
        TeachingPointAdminInformationPO teachingPointAdminInformationPO1 = teachingPointAdminInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                .eq(TeachingPointAdminInformationPO::getUserId, teachingPointAdminInformationRO.getUserId()));
        if(teachingPointAdminInformationPO1 == null){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL5.generateErrorResultInfo();
        }


        if(StrUtil.isEmpty(teachingPointAdminInformationRO.getTeachingPointId())){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL1.generateErrorResultInfo();
        }
        TeachingPointInformationPO teachingPointInformationPO = getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointAdminInformationRO.getTeachingPointId()));
        if(teachingPointInformationPO == null){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL2.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(teachingPointAdminInformationRO.getPhone()) &&
                StrUtil.isEmpty(teachingPointAdminInformationRO.getIdCardNumber())){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL3.generateErrorResultInfo();
        }

        String username = "";
        if(!StrUtil.isEmpty(teachingPointAdminInformationRO.getPhone())){
            username = "M" + teachingPointAdminInformationRO.getPhone();
        }else if(!StrUtil.isEmpty(teachingPointAdminInformationRO.getIdCardNumber())){
            username = "M" + teachingPointAdminInformationRO.getIdCardNumber();
        }

        if(username.length() < 6){
            return ResultCode.TEACHINGPOINT_INFORMATION_FAIL4.generateErrorResultInfo();
        }

        // 为新教学点管理人员生成平台账户
        String password=username.substring(username.length()-6);
        String encryptedPassword = new SM3().digestHex(password);

        PlatformUserPO platformUserPO = new PlatformUserPO()
                .setRoleId(RoleEnum.TEACHING_POINT_ADMIN.getRoleId())
                .setName(teachingPointAdminInformationRO.getName())
                .setUsername(username)
                .setPassword(encryptedPassword)
                ;

        PlatformUserPO platformUserPO1 = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUserId, teachingPointAdminInformationRO.getUserId()));
        if(platformUserPO1 != null){
            // 存在老账号 删除
            int delete = platformUserService.getBaseMapper().delete(
                    new LambdaQueryWrapper<PlatformUserPO>().eq(PlatformUserPO::getUserId, platformUserPO1.getUserId())
            );
        }
        int insert = platformUserService.getBaseMapper().insert(platformUserPO);

        TeachingPointAdminInformationPO teachingPointAdminInformationPO = new TeachingPointAdminInformationPO()
                .setUserId(String.valueOf(platformUserPO.getUserId()))
                .setTeachingPointId(teachingPointAdminInformationRO.getTeachingPointId())
                .setPhone(teachingPointAdminInformationRO.getPhone())
                .setIdCardNumber(teachingPointAdminInformationRO.getIdCardNumber())
                .setIdentity(teachingPointAdminInformationRO.getIdentity())
                .setName(teachingPointAdminInformationRO.getName())
                ;
        // 删除原来的
        int delete = teachingPointAdminInformationService.getBaseMapper().delete(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                .eq(TeachingPointAdminInformationPO::getUserId, teachingPointAdminInformationRO.getUserId()));

        int insert1 = teachingPointAdminInformationService.getBaseMapper().insert(teachingPointAdminInformationPO);
        if(insert1 <= 0){
            return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
        }

        return SaResult.ok("修改教学点教务员成功");

    }

    /**
     * 根据传递过来的 userId 来删除管理人员记录 和平台账号记录
     * @param teachingPointAdminInformationRO
     * @return
     */
    public SaResult deleteManager(TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        int delete = platformUserService.getBaseMapper().delete(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUserId, teachingPointAdminInformationRO.getUserId()));
        int delete1 = teachingPointAdminInformationService.getBaseMapper().delete(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                .eq(TeachingPointAdminInformationPO::getUserId, teachingPointAdminInformationRO.getUserId()));
        if(delete1 < 0){
            return ResultCode.DATABASE_DELETE_ERROR2.generateErrorResultInfo();
        }else if(delete1 == 0){
            return ResultCode.DATABASE_DELETE_ERROR.generateErrorResultInfo();
        }else{
            return SaResult.ok("删除成功");
        }
    }

    /**
     * 获取教学点的筛选参数项
     * @param teachingPointInformationRO
     * @return
     */
    public TeachingPointInformationQueryArgsVO getQueryTeachingPointInformationArgs(TeachingPointInformationRO teachingPointInformationRO) {
        List<TeachingPointInformationPO> teachingPointInformationPOS = getBaseMapper().selectList(null);

        List<String> allAliases = new ArrayList<>(teachingPointInformationPOS.stream()
                .map(TeachingPointInformationPO::getAlias) // 提取每个对象的alias字段
                .filter(Objects::nonNull) // 过滤掉null值
                .collect(Collectors.toSet())); // 先收集到Set中去重，然后转换为List



        TeachingPointInformationQueryArgsVO teachingPointInformationQueryArgsVO = new TeachingPointInformationQueryArgsVO();
        teachingPointInformationQueryArgsVO.setAlias(allAliases);
        return teachingPointInformationQueryArgsVO;
    }
}
