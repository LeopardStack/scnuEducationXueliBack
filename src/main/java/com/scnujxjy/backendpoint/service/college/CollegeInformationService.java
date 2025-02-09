package com.scnujxjy.backendpoint.service.college;

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
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.inverter.college.CollegeInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeAdminInformationRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeAdminInformationVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeInformationVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 学院基础信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class CollegeInformationService extends ServiceImpl<CollegeInformationMapper, CollegeInformationPO> implements IService<CollegeInformationPO> {
    @Resource
    private CollegeInformationInverter collegeInformationInverter;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    @Resource
    private PlatformUserService platformUserService;

    /**
     * 根据collegeId查询学院信息
     *
     * @param collegeId 学院id
     * @return 学院信息
     */
    public CollegeInformationVO detailById(String collegeId) {
        // 参数校验
        if (StrUtil.isBlank(collegeId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        CollegeInformationPO collegeInformationPO = baseMapper.selectById(collegeId);
        // 转换数据并返回
        return collegeInformationInverter.po2VO(collegeInformationPO);
    }

    /**
     * 分页查询学院信息
     *
     * @param collegeInformationROPageRO 分页查询信息
     * @return 学院分页查询信息
     */
    public PageVO<CollegeInformationVO> pageQueryCollegeInformation(PageRO<CollegeInformationRO> collegeInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(collegeInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        CollegeInformationRO entity = collegeInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new CollegeInformationRO();
        }
        // 构建查询参数
        LambdaQueryWrapper<CollegeInformationPO> wrapper = Wrappers.<CollegeInformationPO>lambdaQuery()
                .eq(StrUtil.isNotBlank(entity.getCollegeId()), CollegeInformationPO::getCollegeId, entity.getCollegeId())
                .like(StrUtil.isNotBlank(entity.getCollegeName()), CollegeInformationPO::getCollegeName, entity.getCollegeName())
                .like(StrUtil.isNotBlank(entity.getCollegeAddress()), CollegeInformationPO::getCollegeAddress, entity.getCollegeAddress())
                .eq(StrUtil.isNotBlank(entity.getCollegePhone()), CollegeInformationPO::getCollegePhone, entity.getCollegePhone())
                .last(StrUtil.isNotBlank(collegeInformationROPageRO.getOrderBy()), collegeInformationROPageRO.lastOrderSql());
        // 列表查询 或 分页查询 并返回数据
        if (Objects.equals(true, collegeInformationROPageRO.getIsAll())) {
            List<CollegeInformationPO> collegeInformationPOS = baseMapper.selectList(wrapper);
            Integer total = baseMapper.selectCount(wrapper);
            return new PageVO<>(collegeInformationInverter.po2VO(collegeInformationPOS)).setTotal(Long.valueOf(total));
        } else {
            Page<CollegeInformationPO> collegeInformationPOPage = baseMapper.selectPage(collegeInformationROPageRO.getPage(), wrapper);
            Integer total = baseMapper.selectCount(wrapper);
            return new PageVO<>(collegeInformationPOPage, collegeInformationInverter.po2VO(collegeInformationPOPage.getRecords()))
                    .setTotal(Long.valueOf(total));
        }
    }

    /**
     * 根据collegeId更新学院信息
     *
     * @param collegeInformationRO 更新的学院信息
     * @return 更新后的学院信息
     */
    public CollegeInformationVO editById(CollegeInformationRO collegeInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeInformationRO) || StrUtil.isBlank(collegeInformationRO.getCollegeId())) {
            log.error("参数缺失");
            return null;
        }
        // 转换数据
        CollegeInformationPO collegeInformationPO = collegeInformationInverter.ro2PO(collegeInformationRO);
        // 更新数据
        int count = baseMapper.updateById(collegeInformationPO);
        // 校验更新结果
        if (count <= 0) {
            log.error("更新失败，数据：{}", collegeInformationPO);
            return null;
        }
        // 返回数据
        return detailById(collegeInformationRO.getCollegeId());
    }

    /**
     * 根据collegeId删除学院信息
     *
     * @param collegeId 学院id
     * @return 删除的数量
     */
    public Integer deleteById(String collegeId) {
        // 参数校验
        if (StrUtil.isBlank(collegeId)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(collegeId);
        // 删除校验
        if (count <= 0) {
            log.error("删除失败，collegeId：{}", collegeId);
            return null;
        }
        // 返回数据
        return count;
    }


    /**
     * 添加单个二级学院信息
     * @param collegeInformationRO 前端传递过来的新的二级学院信息
     * @return
     */
    public SaResult addCollegeInfo(CollegeInformationRO collegeInformationRO) {
        CollegeInformationPO collegeInformationPO = new CollegeInformationPO()
                .setCollegeName(collegeInformationRO.getCollegeName())
                .setCollegePhone(collegeInformationRO.getCollegePhone())
                .setCollegeAddress(collegeInformationRO.getCollegeAddress());

        List<CollegeInformationPO> collegeInformationPOS = getBaseMapper().selectList(null);


        // 使用stream处理，先转换为int比较，找到最大的collegeId
        Optional<CollegeInformationPO> maxCollegeInformation = collegeInformationPOS.stream()
                .max(Comparator.comparingInt(c -> Integer.parseInt(c.getCollegeId())));

        // 检查是否找到最大的collegeId
        if (maxCollegeInformation.isPresent()) {
            String maxCollegeId = String.valueOf(Integer.parseInt(maxCollegeInformation.get().getCollegeId())+1);
            collegeInformationPO.setCollegeId(maxCollegeId);

            Integer i = getBaseMapper().selectCount(new LambdaQueryWrapper<CollegeInformationPO>()
                    .eq(CollegeInformationPO::getCollegeName, collegeInformationPO.getCollegeName()));
            if(i > 0){
                return ResultCode.COLLEGE_FAIL2.generateErrorResultInfo();
            }

            int insert = getBaseMapper().insert(collegeInformationPO);
            if(insert > 0){
                return SaResult.ok("新增成功");
            }else{
                return ResultCode.COLLEGE_FAIL3.generateErrorResultInfo();
            }

        } else {
            return ResultCode.COLLEGE_FAIL1.generateErrorResultInfo();
        }

    }


    /**
     * 查询二级学院管理员信息
     * @param collegeInformationRO
     * @return
     */
    public SaResult queryCollegeAdminInfo(CollegeInformationRO collegeInformationRO) {
        CollegeInformationPO collegeInformationPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                .eq(CollegeInformationPO::getCollegeId, collegeInformationRO.getCollegeId()));
        if(collegeInformationPO == null){
            return ResultCode.COLLEGE_FAIL4.generateErrorResultInfo();
        }else{
            List<CollegeAdminInformationVO> collegeAdminInformationPOS = collegeAdminInformationService.getBaseMapper()
                    .selectCollegeAdminInfos(new CollegeAdminInformationRO().setCollegeId(collegeInformationPO.getCollegeId()));
//            new LambdaQueryWrapper<CollegeAdminInformationPO>()
//                    .eq(CollegeAdminInformationPO::getCollegeId, collegeInformationPO.getCollegeId()));

            return SaResult.ok().setData(collegeAdminInformationPOS);
        }
    }


    /**
     * 根据userId更新学院教务员信息
     *
     * @param collegeAdminInformationRO
     * @return
     */
    public SaResult editById(CollegeAdminInformationRO collegeAdminInformationRO) {
        // 参数校验
        if(StrUtil.isEmpty(collegeAdminInformationRO.getName())){
            return ResultCode.COLLEGE_INFORMATION_FAIL5.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(collegeAdminInformationRO.getWorkNumber()) &&
                StrUtil.isEmpty(collegeAdminInformationRO.getIdNumber())){
            return ResultCode.COLLEGE_INFORMATION_FAIL6.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(collegeAdminInformationRO.getCollegeId())){
            return ResultCode.COLLEGE_INFORMATION_FAIL7.generateErrorResultInfo();
        }

        CollegeInformationPO collegeInformationPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                .eq(CollegeInformationPO::getCollegeId, collegeAdminInformationRO.getCollegeId()));
        if(collegeInformationPO == null){
            return ResultCode.COLLEGE_INFORMATION_FAIL8.generateErrorResultInfo();
        }
        CollegeAdminInformationPO collegeAdminInformationPO = new CollegeAdminInformationPO()
                .setName(collegeAdminInformationRO.getName())
                .setIdNumber(collegeAdminInformationRO.getIdNumber())
                .setWorkNumber(collegeAdminInformationRO.getWorkNumber())
                .setCollegeId(collegeAdminInformationRO.getCollegeId())
                .setPhone(collegeAdminInformationRO.getPhone())
                ;

        String username = "";
        if(!StrUtil.isEmpty(collegeAdminInformationRO.getWorkNumber()) ){
            username= "M" + collegeAdminInformationRO.getWorkNumber();
        }else{
            username= "M" + collegeAdminInformationRO.getIdNumber();
        }

        // 为新二级学院管理人员生成平台账户
        String password=username.substring(username.length()-6);
        String encryptedPassword = new SM3().digestHex(password);

        PlatformUserPO platformUserPO = new PlatformUserPO()
                .setName(collegeAdminInformationRO.getName())
                .setUsername(username)
                .setPassword(encryptedPassword)
                .setRoleId(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleId())
                ;
        // 先把它原来的账号给删除
        PlatformUserPO platformUserPO1 = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUserId, collegeAdminInformationRO.getUserId()));
        if(platformUserPO1 != null){
            int i = platformUserService.getBaseMapper().delete(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, platformUserPO1.getUserId()));
            // 删除账号后 删除二级学院管理员信息
            int delete = collegeAdminInformationService.getBaseMapper().delete(new LambdaQueryWrapper<CollegeAdminInformationPO>()
                    .eq(CollegeAdminInformationPO::getUserId, collegeAdminInformationRO.getUserId()));
        }
        int insert1 = platformUserService.getBaseMapper().insert(platformUserPO);


        collegeAdminInformationPO.setUserId(String.valueOf(platformUserPO.getUserId()));
        int insert = collegeAdminInformationService.getBaseMapper().insert(collegeAdminInformationPO);
        if(insert <= 0){
            return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
        }
        // 返回数据
        return SaResult.ok("更新成功");
    }

    /**
     * 根据userId删除学院教务员信息
     *
     * @param userId
     * @return
     */
    public SaResult deleteCollegeAdminInfo(String userId) {
        PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUserId, userId));
        if(platformUserPO != null){
            int i = platformUserService.getBaseMapper().delete(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, userId));
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeAdminInformationPO>()
                .eq(CollegeAdminInformationPO::getUserId, userId));
        if(collegeAdminInformationPO != null){
            int delete = collegeAdminInformationService.getBaseMapper().delete(new LambdaQueryWrapper<CollegeAdminInformationPO>()
                    .eq(CollegeAdminInformationPO::getUserId, userId));
            if(delete <= 0){
                return ResultCode.DATABASE_DELETE_ERROR2.generateErrorResultInfo();
            }else{
                return SaResult.ok("删除成功");
            }
        }else{
            return SaResult.ok("已删除，请勿重复删除");
        }

    }


    /**
     * 为二级学院添加新的教务员
     * @param collegeAdminInformationRO
     * @return
     */
    public SaResult addNewManager( CollegeAdminInformationRO collegeAdminInformationRO) {
        if(StrUtil.isEmpty(collegeAdminInformationRO.getName())){
            return ResultCode.COLLEGE_INFORMATION_FAIL1.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(collegeAdminInformationRO.getWorkNumber()) &&
                StrUtil.isEmpty(collegeAdminInformationRO.getIdNumber())){
            return ResultCode.COLLEGE_INFORMATION_FAIL2.generateErrorResultInfo();
        }

        if(StrUtil.isEmpty(collegeAdminInformationRO.getCollegeId())){
            return ResultCode.COLLEGE_INFORMATION_FAIL3.generateErrorResultInfo();
        }

        CollegeInformationPO collegeInformationPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                .eq(CollegeInformationPO::getCollegeId, collegeAdminInformationRO.getCollegeId()));
        if(collegeInformationPO == null){
            return ResultCode.COLLEGE_INFORMATION_FAIL4.generateErrorResultInfo();
        }

        CollegeAdminInformationPO collegeAdminInformationPO = new CollegeAdminInformationPO()
                .setName(collegeAdminInformationRO.getName())
                .setIdNumber(collegeAdminInformationRO.getIdNumber())
                .setWorkNumber(collegeAdminInformationRO.getWorkNumber())
                .setCollegeId(collegeAdminInformationRO.getCollegeId())
                .setPhone(collegeAdminInformationRO.getPhone())
                ;

        String username = "";
        if(!StrUtil.isEmpty(collegeAdminInformationRO.getWorkNumber()) ){
            username= "M" + collegeAdminInformationRO.getWorkNumber();
        }else{
            username= "M" + collegeAdminInformationRO.getIdNumber();
        }

        // 为新老师生成平台账户
        String password=username.substring(username.length()-6);
        String encryptedPassword = new SM3().digestHex(password);

        PlatformUserPO platformUserPO = new PlatformUserPO()
                .setName(collegeAdminInformationRO.getName())
                .setUsername(username)
                .setPassword(encryptedPassword)
                .setRoleId(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleId())
                ;
        int insert1 = platformUserService.getBaseMapper().insert(platformUserPO);


        collegeAdminInformationPO.setUserId(String.valueOf(platformUserPO.getUserId()));
        int insert = collegeAdminInformationService.getBaseMapper().insert(collegeAdminInformationPO);
        if(insert <= 0){
            return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
        }
        return SaResult.ok("新增教务员成功");
    }
}
