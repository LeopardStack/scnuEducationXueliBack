package com.scnujxjy.backendpoint.service.basic;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.scnujxjy.backendpoint.dao.entity.basic.PermissionPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.basic.RolePermissionPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PermissionMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformRoleMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.RolePermissionMapper;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.basic.PlatformUserInverter;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class PlatformUserService extends ServiceImpl<PlatformUserMapper, PlatformUserPO> implements IService<PlatformUserPO> {
    @Resource
    private PlatformUserInverter platformUserInverter;

    @Resource
    private PlatformRoleService platformRoleService;

    @Value("${wechat.app-secret}")
    private String wechatAppSecret;

    @Value("${wechat.app-id}")
    private String wechatAppId;

    @Value("${wechat.request-url}")
    private String requestUrl;

    @Resource
    private PermissionService permissionService;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PlatformUserMapper platformUserMapper;
    @Autowired
    private PlatformRoleMapper platformRoleMapper;

    /**
     * 根据userId批量更新用户信息
     * <p>目前只支持更新补充角色id集合</p>
     *
     * @param platformUserROS
     * @return
     */
    @Transactional
    public List<PlatformUserVO> updateUser(List<PlatformUserRO> platformUserROS) {
        if (CollUtil.isEmpty(platformUserROS)) {
            throw new BusinessException("传入数组为空");
        }
        return platformUserROS.stream()
                .filter(ele -> Objects.nonNull(ele.getUserId()))
                .map(ele -> {
                    int count = baseMapper.updateUser(PlatformUserPO.builder()
                            .userId(ele.getUserId())
                            .supplementaryRoleIdSet(ele.getSupplementaryRoleIdSet())
                            .build());
                    if (count <= 0) {
                        log.error("更新失败，更新参数为：{}，目前已回滚", ele);
                        throw new BusinessException("更新失败");
                    }
                    return detailById(ele.getUserId());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 根据userId获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    public PlatformUserVO detailById(Long userId) {
        // 参数校验
        if (Objects.isNull(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        PlatformUserPO platformUserPO = baseMapper.selectById(userId);
        // 返回数据
        return platformUserInverter.po2VO(platformUserPO);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户登录账号
     * @return 用户信息
     */
    public PlatformUserVO detailByUsername(String username) {
        // 参数校验
        if (StrUtil.isBlank(username)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        List<PlatformUserPO> platformUserPOS = baseMapper.selectList(Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, username));
        if (platformUserPOS.size() > 1) {
            log.error("该账号存在多名用户 " + username);
            return null;
        } else if (platformUserPOS.isEmpty()) {
            log.error("该账号不存在 " + username);
            return null;
        }
        PlatformUserPO platformUserPO = platformUserPOS.get(0);
        // 返回数据
        return platformUserInverter.po2VO(platformUserPO);
    }

    /**
     * 根据username和password登录
     *
     * @param platformUserRO 登录信息
     * @return true-成功，false-失败
     */
    public PlatformUserPO userLogin(PlatformUserRO platformUserRO) {

        String openId = null;
        if (platformUserRO.getWechatOpenId() != null && StrUtil.isNotBlank(platformUserRO.getWechatOpenId())) {
            // 微信登录用户
            String code = platformUserRO.getWechatOpenId();
            try {
                String url = requestUrl
                        + "?appid=" + wechatAppId
                        + "&secret=" + wechatAppSecret
                        + "&js_code=" + code
                        + "&grant_type=authorization_code";

                RestTemplate restTemplate = new RestTemplate();
                String response = restTemplate.getForObject(url, String.class);

                log.info("请求微信的响应结果 \n" + response);
                // {"errcode":40163,"errmsg":"code been used, rid: 6538ac7d-18c7e09b-572e68c3"}
                // 使用Jackson库解析返回的JSON
                ObjectMapper mapper = new ObjectMapper();
                try {
                    openId = mapper.readTree(response).get("openid").asText();
                    log.info("该用户的 openId 为 " + openId);
                } catch (Exception e) {
                    throw new RuntimeException("解析微信响应失败", e);
                }

                PlatformUserPO platformUserPO = baseMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>().
                        eq(PlatformUserPO::getWechatOpenId, openId));
                if (platformUserPO != null) {
                    return platformUserPO;
                }
            } catch (Exception e) {
                log.error("微信登录失败 " + e.toString());
            }

        }
        // 参数校验
        if (Objects.isNull(platformUserRO) || StrUtil.isBlank(platformUserRO.getUsername()) || StrUtil.isBlank(platformUserRO.getPassword())) {
            log.error("账号密码参数缺失");
            return null;
        }
        SM3 sm3 = new SM3();
        // 密码加密
        platformUserRO.setPassword(sm3.digestHex(platformUserRO.getPassword()));

//        if(platformUserRO.getUsername().contains("m")){
//            return null;
//        }

        PlatformUserPO platformUserPO = baseMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername,
                platformUserRO.getUsername()).eq(PlatformUserPO::getPassword, platformUserRO.getPassword()));
        // 如果无法查询到，则说明密码错误
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        if (platformUserRO.getWechatOpenId() != null && platformUserRO.getWechatOpenId().trim().length() != 0) {
            platformUserPO.setWechatOpenId(openId);
            baseMapper.updateById(platformUserPO);
        }

        StpUtil.login(platformUserPO.getUserId());
        return platformUserPO;

    }

    /**
     * 根据userId返回所有的权限以及资源信息
     *
     * @param userId 用户id
     * @return 权限资源信息
     */
    public UserRolePermissionBO rolePermissionDetailByUserId(Long userId) {

        if (Objects.isNull(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 获取用户详细信息
        PlatformUserVO platformUserVO = detailById(userId);

        if (Objects.isNull(platformUserVO)) {
            log.error("该userId：{} 对应用户不存在", userId);
            return null;
        }

        // 获取权限详情列表
        Long roleId = platformUserVO.getRoleId();
        List<PermissionVO> permissionVOS = platformRoleService.permissionVOSByRoleId(roleId);

        // 参数校验
        if (CollUtil.isEmpty(permissionVOS)) {
            log.error("获取权限详情列表为空，roleId：{}", roleId);
            return null;
        }
        // 获取其他角色对应的资源列表
        List<Long> supplementaryRoleIdSet = platformUserVO.getSupplementaryRoleIdSet();
        if (CollUtil.isNotEmpty(supplementaryRoleIdSet)) {
            supplementaryRoleIdSet
                    .forEach(ele -> permissionVOS.addAll(platformRoleService.permissionVOSByRoleId(ele)));
        }

        // 获取资源列表
        List<String> recourses = permissionVOS.stream()
                .filter(Objects::nonNull)
                .map(PermissionVO::getResource)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        // 返回数据
        return UserRolePermissionBO.builder()
                .userId(userId)
                .roleId(roleId)
                .resources(recourses)
                .permissionVOS(permissionVOS)
                .build();
    }

    /**
     * 批量插入用户信息
     *
     * @param platformUserROS 待插入用户信息列表
     * @return 插入后用户信息列表
     */
    public List<PlatformUserVO> batchCreateUser(List<PlatformUserRO> platformUserROS) {
        // 参数校验
        if (CollUtil.isEmpty(platformUserROS)) {
            log.error("参数缺失");
            return null;
        }
        // 检查好的数据
        List<PlatformUserRO> checkedPlatformUserRO = platformUserROS.stream()
                .filter(this::checkPlatformUser)
                .collect(Collectors.toList());
        // 需要插入的数据
        List<PlatformUserRO> insertPlatformUserROS = checkedPlatformUserRO.stream()
                .filter(ele -> Objects.isNull(ele.getUserId()))
                .collect(Collectors.toList());
        // 不需要插入的数据的userId集合
        Set<Long> existUserIdSet = checkedPlatformUserRO.stream()
                .filter(ele -> Objects.nonNull(ele.getUserId()))
                .map(PlatformUserRO::getUserId)
                .collect(Collectors.toSet());
        // 插入数据
        if (CollUtil.isNotEmpty(insertPlatformUserROS)) {
            List<PlatformUserPO> insertPlatformUserPOS = platformUserInverter.ro2PO(insertPlatformUserROS);
            boolean savedBatch = saveBatch(insertPlatformUserPOS);
            // 插入检测
            if (!savedBatch) {
                log.error("插入失败，已回滚，用户列表为：{}", insertPlatformUserPOS);
                return null;
            }
            // 将插入后的userId放入到集合中
            existUserIdSet.addAll(insertPlatformUserPOS.stream()
                    .filter(Objects::nonNull)
                    .map(PlatformUserPO::getUserId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
        }
        // 用户id列表获取校验
        if (CollUtil.isEmpty(existUserIdSet)) {
            log.error("获取插入用户id失败，用户列表为：{}", insertPlatformUserROS);
            return null;
        }
        // 批量获取用户信息
        List<PlatformUserPO> userPOS = baseMapper.selectBatchIds(existUserIdSet);
        // 获取用户信息校验
        if (CollUtil.isEmpty(userPOS)) {
            log.error("批量获取用户信息失败，用户id列表为：{}", existUserIdSet);
            return null;
        }
        // 返回数据
        return platformUserInverter.po2VO(userPOS);
    }

    /**
     * 检查用户信息符合插入规则，并对其中的数据进行处理
     *
     * @param platformUserRO 用户信息
     * @return true-符合，false-不符合
     */
    private Boolean checkPlatformUser(PlatformUserRO platformUserRO) {
        // 校验参数
        if (Objects.isNull(platformUserRO)) {
            log.error("参数缺失");
            return false;
        }
        // 检查用户角色
        if (Objects.isNull(platformUserRO.getRoleId())) {
            log.error("用户角色缺失，用户信息：{}", platformUserRO);
            return false;
        }
        if (Objects.isNull(platformRoleService.detailById(platformUserRO.getRoleId()))) {
            log.error("对应角色信息不存在，用户信息：{}", platformUserRO);
            return false;
        }

        // 检查登录密码
        if (StrUtil.isBlank(platformUserRO.getPassword())) {
            log.error("用户登录密码缺失，用户信息：{}", platformUserRO);
            return false;
        }

        // 检查登陆账号
        if (StrUtil.isBlank(platformUserRO.getUsername())) {
            log.error("用户登录账号缺失，用户信息：{}", platformUserRO);
            return false;
        }
        // 重复账号跳过，将userId保留下来，方便后续查询信息
        PlatformUserPO platformUserPO = baseMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, platformUserRO.getUsername()));
        if (Objects.nonNull(platformUserPO)) {
            log.error("登陆账号已经被注册，用户信息：{}", platformUserPO);
            platformUserRO.setUserId(platformUserPO.getUserId());
            return true;
        }
        SM3 sm3 = new SM3();
        // 密码加密
        platformUserRO.setPassword(sm3.digestHex(platformUserRO.getPassword()));

        return true;

    }

    /**
     * 根据userId更改用户密码
     *
     * @param userId      用户id
     * @param newPassword 新密码
     * @return true-成功，false-失败
     */
    public Boolean changePassword(Long userId, String newPassword) {
        // 参数校验
        if (Objects.isNull(userId) || StrUtil.isBlank(newPassword)) {
            log.error("参数缺失");
            return false;
        }
        SM3 sm3 = new SM3();
        // 密码加密
        String encryptedPassword = sm3.digestHex(newPassword);

        // 使用MyBatis Plus的UpdateWrapper来构建更新条件和更新内容
        PlatformUserPO updateUser = new PlatformUserPO();
        updateUser.setPassword(encryptedPassword);

        int updateResult = baseMapper.update(updateUser, Wrappers.<PlatformUserPO>lambdaUpdate().eq(PlatformUserPO::getUserId, userId));

        return updateResult > 0;
    }

    /**
     * 根据 username 获取 userId
     *
     * @param username 用户登陆名
     * @return userId
     */
    public Long getUserIdByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        PlatformUserVO platformUserVO = detailByUsername(username);
        if (Objects.isNull(platformUserVO)) {
            return null;
        }
        return platformUserVO.getUserId();
    }

    /**
     * 根据userId批量查询username
     *
     * @param userId 用户编号
     * @return {@link Map} key - userId value - username
     */
    public Map<Long, String> getUsernameByUserId(List<Long> userId) {
        if (CollUtil.isEmpty(userId)) {
            return MapUtil.newHashMap();
        }
        HashSet<Long> userIdSet = CollUtil.newHashSet(userId);
        List<PlatformUserPO> platformUserPOS = baseMapper.selectBatchIds(userIdSet);
        if (CollUtil.isEmpty(platformUserPOS)) {
            return MapUtil.newHashMap();
        }
        return platformUserPOS.stream()
                .filter(Objects::nonNull)
                .filter(user -> StrUtil.isNotBlank(user.getUsername()))
                .collect(Collectors.toMap(PlatformUserPO::getUserId, PlatformUserPO::getUsername));
    }

    /**
     * 根据角色名称查询username
     * <p>会匹配用户的补充角色</p>
     *
     * @param roleNameSet
     * @return
     */
    public Set<String> selectUsernameByRoleName(Set<String> roleNameSet) {
        if (CollUtil.isEmpty(roleNameSet)) {
            return Sets.newHashSet();
        }
        List<PlatformRolePO> platformRolePOS = platformRoleMapper.selectList(Wrappers.<PlatformRolePO>lambdaQuery()
                .in(PlatformRolePO::getRoleName, roleNameSet));
        if (CollUtil.isEmpty(platformRolePOS)) {
            return Sets.newHashSet();
        }
        Set<Long> roleIdSet = platformRolePOS.stream()
                .map(PlatformRolePO::getRoleId)
                .collect(Collectors.toSet());
        List<PlatformUserPO> platformUserPOS = baseMapper.selectPlatformUserList(PlatformUserRO.builder()
                .roleIds(ListUtil.toList(roleIdSet))
                .build());
        if (CollUtil.isEmpty(platformUserPOS)) {
            return Sets.newHashSet();
        }
        return platformUserPOS.stream()
                .map(PlatformUserPO::getUsername)
                .collect(Collectors.toSet());
    }

    /**
     * 根据permission中的resource获取用户名集合
     * <p>会根据用户的补充角色判断</p>
     *
     * @param permissionResources
     * @return
     */
    public Set<String> selectUsernameByPermissionResource(Set<String> permissionResources) {
        List<PermissionPO> permissionPOS = permissionMapper.selectList(Wrappers.<PermissionPO>lambdaQuery()
                .in(PermissionPO::getResource, permissionResources));
        if (CollUtil.isEmpty(permissionPOS)) {
            return Sets.newHashSet();
        }
        Set<Long> permissionIdSet = permissionPOS.stream()
                .map(PermissionPO::getPermissionId)
                .collect(Collectors.toSet());
        List<RolePermissionPO> rolePermissionPOS = rolePermissionMapper.selectList(Wrappers.<RolePermissionPO>lambdaQuery()
                .in(RolePermissionPO::getPermissionId, permissionIdSet));
        if (CollUtil.isEmpty(rolePermissionPOS)) {
            return Sets.newHashSet();
        }
        Set<Long> roleSetId = rolePermissionPOS.stream()
                .map(RolePermissionPO::getRoleId)
                .collect(Collectors.toSet());
        List<Long> roleIds = new ArrayList<>(roleSetId);
        List<PlatformUserPO> platformUserPOS = platformUserMapper.selectPlatformUserList(PlatformUserRO.builder()
                .roleIds(roleIds)
                .build());
        if (CollUtil.isEmpty(platformUserPOS)) {
            return Sets.newHashSet();
        }
        return platformUserPOS.stream()
                .map(PlatformUserPO::getUsername)
                .collect(Collectors.toSet());
    }

    /**
     * 根据 userId 获取用户名称
     *
     * @param userId
     * @return
     */
    public String getNameByUserId(Long userId) {
        if (Objects.isNull(userId)) {
            return null;
        }

        return null;
    }

    public PageVO<AdmissionInformationVO> getPlatformUsersInformation(PageRO<PlatformUserRO> platformUserROPageRO) {
        return null;
    }
}