package com.scnujxjy.backendpoint.service.basic;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.inverter.basic.PlatformUserInverter;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
     * @param userName 用户登录账号
     * @return 用户信息
     */
    public PlatformUserVO detailByuserName(String userName) {
        // 参数校验
        if (Objects.isNull(userName)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        List<PlatformUserPO> platformUserPOS = baseMapper.selectPlatformUsers1(userName);
        if (platformUserPOS.size() > 1) {
            log.error("该账号存在多名用户 " + userName);
            return null;
        } else if (platformUserPOS.size() == 0) {
            log.error("该账号不存在 " + userName);
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
        if (platformUserRO.getWechatOpenId() != null && platformUserRO.getWechatOpenId().trim().length() != 0) {
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


}