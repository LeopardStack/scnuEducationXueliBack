package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceAttachmentEnum;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.constant.enums.SystemEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AnnouncementMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.basic.OnlineCount;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.model.vo.basic.UserLoginVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AnnouncementMessageVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AttachmentVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformPopupMsgVO;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.platform_message.AnnouncementMessageService;
import com.scnujxjy.backendpoint.service.platform_message.AttachmentService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.annotations.CheckIPWhiteList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.util.ResultCode.USER_LOGIN_ERROR;
import static com.scnujxjy.backendpoint.util.ResultCode.USER_LOGIN_FAIL;

/**
 * 用户登录控制
 *
 * @author leopard
 * @since 2023-08-02
 */
@Slf4j
@RestController
@RequestMapping("/platform-user")
public class PlatformUserController {

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private GlobalConfigService globalConfigService;

    @Resource
    private AdmissionInformationService admissionInformationService;

    @Resource
    private StudentStatusService studentStatusService;

    @Resource
    private PlatformMessageService platformMessageService;

    @Resource
    private AnnouncementMessageService announcementMessageService;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;


    /**
     * 用户登录接口
     *
     * @param platformUserRO 用户登录信息
     * @return false-登陆失败，true-登陆成功
     * <p>token在Header中</p>
     */
    @PostMapping("/login")
    public SaResult userLogin(@RequestBody PlatformUserRO platformUserRO, HttpServletRequest request) {


        // 参数校验，登录名、密码不可或缺
        if (Objects.isNull(platformUserRO) || StrUtil.isBlank(platformUserRO.getUsername()) || StrUtil.isBlank(platformUserRO.getPassword())) {
            return SaResult.error("账户、密码不允许为空，登录失败");
        }
        // 登录
        PlatformUserPO isLogin = platformUserService.userLogin(platformUserRO);
        // 返回
        if (isLogin != null) {
            // Satoken 注册登录
            StpUtil.login(platformUserRO.getUsername());
            Object tokenInfo = StpUtil.getTokenInfo();
            List<String> permissionList = StpUtil.getPermissionList();
            List<String> roleList = StpUtil.getRoleList();
            if (roleList.isEmpty()) {
                return SaResult.error(USER_LOGIN_FAIL.getMessage()).setCode(USER_LOGIN_FAIL.getCode());
            }
            // 第一个是主要权限人
            String roleName = roleList.get(0);
            String tmp = "管理员";
            if (roleName.contains(tmp)) {
                UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, tmp, roleName, (String) StpUtil.getLoginId(),
                        isLogin.getName(), isLogin.getWechatOpenId(), roleList);


                // 更新角色在线人数和总在线人数
                updateOnlineCounts(roleList.get(0), true);
                return SaResult.data("成功登录 " + platformUserRO.getUsername())
                        .set("userInfo", userLoginVO);
            }

            UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, roleName, roleName, (String) StpUtil.getLoginId(),
                    isLogin.getName(), isLogin.getWechatOpenId(), roleList);
            // 更新角色在线人数和总在线人数
            updateOnlineCounts(roleList.get(0), true);
            return SaResult.data("成功登录 " + platformUserRO.getUsername()).set("userInfo", userLoginVO);
        } else {
            return SaResult.error(USER_LOGIN_ERROR.getMessage()).setCode(USER_LOGIN_ERROR.getCode());
        }
    }

    /**
     * 根据userId批量更新用户信息
     * <p>目前只支持更新补充角色id集合</p>
     *
     * @param platformUserROS
     * @return
     */
    @PostMapping("/batch-update-user")
    @SaCheckRole("超级管理员")
    public SaResult batchUpdateUser(@RequestBody List<PlatformUserRO> platformUserROS) {
        if (CollUtil.isEmpty(platformUserROS)) {
            throw dataMissError();
        }
        List<PlatformUserVO> platformUserVOS = platformUserService.updateUser(platformUserROS);
        if (CollUtil.isEmpty(platformUserVOS)) {
            return SaResult.error("更新失败");
        }
        return SaResult.data(platformUserVOS);
    }

    @GetMapping("/logout")
    public SaResult logOut() {
        // 获取当前用户角色
        if (!StpUtil.getRoleList().isEmpty()) {
            String roleName = StpUtil.getRoleList().get(0);

            // 更新角色在线人数和总在线人数
            updateOnlineCounts(StpUtil.getRoleList().get(0), false);
        }

        StpUtil.logout();
        return SaResult.ok();
    }

    /**
     * 用户采用微信登录
     *
     * @param platformUserRO 用户登录信息
     * @return false-登陆失败，true-登陆成功
     * <p>token在Header中</p>
     */
    @PostMapping("/wechat-login")
    public SaResult wechatUserLogin(@RequestBody PlatformUserRO platformUserRO) {
        // 参数校验，登录名、密码不可或缺
        if (Objects.isNull(platformUserRO) || (StrUtil.isBlank(platformUserRO.getUsername()) &&
                StrUtil.isBlank(platformUserRO.getPassword()) && StrUtil.isBlank(platformUserRO.getWechatOpenId()))) {
            return SaResult.error("账户密码 和 微信登录ID不能同时为空，登录失败");
        }
        // 登录
        PlatformUserPO isLogin = platformUserService.userLogin(platformUserRO);
        // 返回
        if (isLogin != null) {
            // Satoken 注册登录
            StpUtil.login(isLogin.getUsername());
            Object tokenInfo = StpUtil.getTokenInfo();
            List<String> permissionList = StpUtil.getPermissionList();
            List<String> roleList = StpUtil.getRoleList();
            if (roleList.isEmpty()) {
                return SaResult.error(USER_LOGIN_FAIL.getMessage()).setCode(USER_LOGIN_FAIL.getCode());
            }
            // 第一个是主要权限
            String roleName = roleList.get(0);
            String tmp = "管理员";
            if (roleName.contains(tmp)) {
                UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, tmp, roleName, (String) StpUtil.getLoginId(),
                        isLogin.getName(), isLogin.getWechatOpenId(), roleList);
                // 更新角色在线人数和总在线人数
                updateOnlineCounts(roleList.get(0), true);
                return SaResult.data("成功登录 " + isLogin.getUsername()).set("userInfo", userLoginVO);
            }

            UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, roleName, roleName, (String) StpUtil.getLoginId(),
                    isLogin.getName(), isLogin.getWechatOpenId(), roleList);
            // 更新角色在线人数和总在线人数
            updateOnlineCounts(roleList.get(0), true);
            return SaResult.data("成功登录 " + isLogin.getUsername()).set("userInfo", userLoginVO);
        } else {
            return SaResult.error(USER_LOGIN_ERROR.getMessage()).setCode(USER_LOGIN_ERROR.getCode());
        }
    }

    /**
     * 根据用户id查询
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping("/detail")
    public SaResult detailByUserId(Long userId) {
        // 参数校验
        if (Objects.isNull(userId)) {
            throw dataMissError();
        }
        // 查询数据
        PlatformUserVO platformUserVO = platformUserService.detailById(userId);
        // 返回数据
        return SaResult.data(platformUserVO);
    }

    /**
     * 根据用户id查询数据 用户的基础数据 比如 未读消息总数
     * 是否有弹框消息
     *
     * @return 用户信息
     */
    @GetMapping("/detailUser")
    public SaResult detail() {
        String userName = StpUtil.getLoginIdAsString();
        String cacheKey = "userDetail_" + userName;

        // 尝试从缓存中获取数据
        PlatformUserVO cachedPlatformUserVO = (PlatformUserVO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedPlatformUserVO != null) {
            return SaResult.data(cachedPlatformUserVO);
        }

        List<String> roleList = StpUtil.getRoleList();
        boolean isNewStudent = false;
        // 查询数据
        PlatformUserVO platformUserVO = platformUserService.detailByUsername(userName);

        // 查询未读消息
//        List<PlatformMessagePO> platformMessagePOList = platformMessageService.getBaseMapper().selectList(new LambdaQueryWrapper<PlatformMessagePO>()
//                .eq(PlatformMessagePO::getUserId, platformUserVO.getUserId())
//                .eq(PlatformMessagePO::getIsRead, Boolean.FALSE)
//        );
//        platformUserVO.setUnReadMsgCount(platformMessagePOList.size());
//
//        List<PlatformPopupMsgVO> platformPopupMsgVOList = new ArrayList<>();
//        for(PlatformMessagePO platformMessagePO : platformMessagePOList){
//            // 公告消息 如果是弹框需要加入进来
//            AnnouncementMessageVO announcementMessageVO = new AnnouncementMessageVO();
//            PlatformPopupMsgVO platformPopupMsgVO = new PlatformPopupMsgVO();
//            BeanUtils.copyProperties(platformMessagePO, platformPopupMsgVO);
//
//            if(platformMessagePO.getIsPopup() == null || platformMessagePO.getIsPopup().equals("N")){
//                // 非弹框消息直接跳过
//                continue;
//            }
//            if(platformMessagePO.getMessageType().equals(MessageEnum.ANNOUNCEMENT_MSG.getMessageName())){
//
//                AnnouncementMessagePO announcementMessagePO = announcementMessageService.getBaseMapper().selectOne(new LambdaQueryWrapper<AnnouncementMessagePO>()
//                        .eq(AnnouncementMessagePO::getId, platformMessagePO.getRelatedMessageId()));
//
//                BeanUtils.copyProperties(announcementMessagePO, announcementMessageVO);
//
//                List<Long> attachmentIds = announcementMessagePO.getAttachmentIds();
//                List<AttachmentVO> attachmentVOList = new ArrayList<>();
//                if(attachmentIds != null && !attachmentIds.isEmpty()) {
//                    for(Long attachmentId : attachmentIds){
//                        AttachmentPO attachmentPO = attachmentService.getBaseMapper().selectOne(new LambdaQueryWrapper<AttachmentPO>()
//                                .eq(AttachmentPO::getId, attachmentId));
//                        AttachmentVO attachmentVO = new AttachmentVO()
//                                .setId(attachmentPO.getId())
//                                .setRelatedId(attachmentPO.getRelatedId())
//                                .setAttachmentType(attachmentPO.getAttachmentType())
//                                .setAttachmentOrder(attachmentPO.getAttachmentOrder())
//                                .setAttachmentMinioPath(attachmentPO.getAttachmentMinioPath())
//                                .setAttachmentName(attachmentPO.getAttachmentName())
//                                .setAttachmentSize(attachmentPO.getAttachmentSize())
//                                .setUsername(attachmentPO.getUsername())
//                                ;
//                        attachmentVOList.add(attachmentVO);
//
//                    }
//
//                }
//                announcementMessageVO.setAttachmentVOS(attachmentVOList);
//            }
//
//            platformPopupMsgVO.setMsgBody(announcementMessageVO);
//
//            platformPopupMsgVOList.add(platformPopupMsgVO);
//        }
//        platformUserVO.setPlatformPopupMsgVOList(platformPopupMsgVOList);

        if(roleList.contains(RoleEnum.STUDENT.getRoleName())){
            // 学生需要区分是否是新生
            String systemArg = SystemEnum.NOW_NEW_STUDENT_GRADE.getSystemArg();
            AdmissionInformationPO admissionInformationPO = admissionInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<AdmissionInformationPO>()
                    .eq(AdmissionInformationPO::getGrade, systemArg).eq(AdmissionInformationPO::getIdCardNumber, userName));
            // 不仅仅是新生信息是否有它 还需要看他是否有学籍 即学号
            Integer i = studentStatusService.getBaseMapper().selectCount(new LambdaQueryWrapper<StudentStatusPO>()
                    .eq(StudentStatusPO::getGrade, systemArg)
                    .eq(StudentStatusPO::getIdNumber, userName)
            );

            if(admissionInformationPO != null && i == 0){
                isNewStudent = true;
                // 进一步判断是否需要将公告的 URL 给他
                if(admissionInformationPO.getIsConfirmed().equals(0)){
                    // 为0 代表 未读新生录取公告
                    GlobalConfigPO globalConfigPO = globalConfigService.getBaseMapper().selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                            .eq(GlobalConfigPO::getConfigKey, AnnounceAttachmentEnum.NOW_NEW_STUDENT_ADMISSION.getSystemArg()));
                    platformUserVO.setNewStudentAnnouncement(globalConfigPO.getConfigValue());
                }
            }
        }
        // 返回数据
        platformUserVO.setIsNewStudent(isNewStudent);

        // 将结果存入 Redis 缓存，并设置适当的过期时间
        redisTemplate.opsForValue().set(cacheKey, platformUserVO, 10, TimeUnit.HOURS);

        return SaResult.data(platformUserVO);
    }

    /**
     * 根据用户id查询角色权限资源信息
     *
     * @param userId 用户id
     * @return 角色权限信息
     */
    @GetMapping("/role-permission/detail")
    public SaResult rolePermissionDetailByUserId(Long userId) {
        // 参数校验
        if (Objects.isNull(userId)) {
            throw dataMissError();
        }
        // 查询数据
        UserRolePermissionBO userRolePermissionBO = platformUserService.rolePermissionDetailByUserId(userId);
        // 返回数据
        return SaResult.data(userRolePermissionBO);
    }


    /**
     * 请求某个用户是否已经登录，并返回其 token 剩余有效时间
     *
     * @param username 用户名
     * @return SaResult 包含登录状态和token剩余有效时间
     */
    @GetMapping("/checkLogin")
    public SaResult checkUserLoginStatus(@RequestParam String username) {
        String tokenValue = StpUtil.getTokenValue();
        if (StrUtil.isBlank(tokenValue)) {
            return SaResult.data("User not logged in");
        }

        String currentUsername = (String) StpUtil.getLoginIdByToken(tokenValue);
        if (username.equals(currentUsername)) {
            // 获取token的剩余有效时间
            long tokenTimeout = StpUtil.getTokenTimeout();
            SaTokenInfo tokenInfo1 = StpUtil.getTokenInfo();

            return SaResult.data(tokenInfo1);
        } else {
            return SaResult.data("Username does not match the current session").setCode(551);
        }
    }

    /**
     * 获取平台实时数据
     * 比如每个班的直播情况 上课数据 系统在线人数等等
     *
     * @return
     */
    @GetMapping("/getPlatformBasicInfo")
    public SaResult getPlatFormInfo() {
        try {
            // 从 Redis 中获取在线人数统计对象
            OnlineCount onlineCount = (OnlineCount) redisTemplate.opsForValue().get("onlineCount");
            if (onlineCount == null) {
                onlineCount = new OnlineCount(); // 如果 Redis 中没有数据，则创建一个新的 OnlineCount 对象
            }

            // 从 OnlineCount 对象中获取角色的在线人数和总在线人数
            Map<String, Integer> roleLoginCount = onlineCount.getRoleCounts();
            int totalOnlineCount = onlineCount.getTotalOnlineCount();

            // 将结果放入返回对象
            HashMap<String, Object> result = new HashMap<>();
            result.put("roleLoginCount", roleLoginCount);
            result.put("totalOnlineCount", totalOnlineCount);

            return SaResult.ok().setData(result);
        } catch (Exception e) {
            log.error("Error getting platform basic info: ", e);
            return SaResult.error("无法获取平台基本信息");
        }
    }


    private void updateOnlineCounts(String roleName, boolean increment) {
        // 获取当前的在线人数统计
        OnlineCount onlineCount = (OnlineCount) redisTemplate.opsForValue().get("onlineCount");
        if (onlineCount == null) {
            onlineCount = new OnlineCount();
        }

        // 更新统计
        onlineCount.updateCount(roleName, increment);

        // 将更新后的统计信息存回 Redis
        redisTemplate.opsForValue().set("onlineCount", onlineCount, 100, TimeUnit.HOURS);
    }

    /**
     * 平台用户信息管理
     * @param platformUserROPageRO
     * @return
     */
    @PostMapping("/get_platform_users_information")
    @CheckIPWhiteList
    public SaResult pageQueryAdmissionInformation(@RequestBody PageRO<PlatformUserRO> platformUserROPageRO) {
        // 参数校验
        if (Objects.isNull(platformUserROPageRO)) {
            return SaResult.error("获取平台用户信息失败").setCode(2001);
        }
        if (Objects.isNull(platformUserROPageRO.getEntity())) {
            platformUserROPageRO.setEntity(new PlatformUserRO());
        }
        // 查询数据
        PageVO<AdmissionInformationVO> admissionInformationVOPageVO = platformUserService.getPlatformUsersInformation(platformUserROPageRO);

        // 返回数据
        return SaResult.data(admissionInformationVOPageVO);
    }


    /**
     * 修改平台用户密码
     * @param platformUserRO
     * @return
     */
    @PostMapping("/platform_change_password")
    @SaCheckPermission("平台基础信息.修改密码")
    public SaResult changePassword(@RequestBody PlatformUserRO platformUserRO) {
        // 参数校验
        if (Objects.isNull(platformUserRO)) {
            return ResultCode.USER_LOGIN_FAIL3.generateErrorResultInfo();
        }

        PlatformUserVO platformUserVO = platformUserService.detailByUsername(platformUserRO.getUsername());

        // 检查 platformUserVO 是否为空
        if (Objects.isNull(platformUserVO)) {
            return ResultCode.USER_NOT_EXIST.generateErrorResultInfo();
        }

        String password = platformUserRO.getPassword();

        // 密码强度校验
        String passwordValidationError = validatePasswordStrength(password);
        if (passwordValidationError != null) {
            return ResultCode.USER_LOGIN_FAIL4.generateErrorResultInfo().setMsg(passwordValidationError);
        }

        Boolean aBoolean = platformUserService.changePassword(platformUserVO.getUserId(), password);
        if(aBoolean){
            return SaResult.ok("修改密码成功");
        }

        // 返回数据
        return ResultCode.USER_LOGIN_FAIL5.generateErrorResultInfo();
    }

    /**
     * Validates the password strength and returns null if the password is strong enough.
     * If the password is not strong enough, it returns a validation error message.
     */
    private String validatePasswordStrength(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            return "密码必须为 6 到 20 位";
        }
        boolean hasUpperCase = false, hasLowerCase = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        // 密码必须包含大小写字母和数字
        if (!hasUpperCase || !hasLowerCase || !hasDigit) {
            return "密码必须由字母的大小写和数字组成";
        }
        return null; // 密码强度符合要求
    }
}

