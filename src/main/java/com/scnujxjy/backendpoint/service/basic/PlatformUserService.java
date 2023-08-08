package com.scnujxjy.backendpoint.service.basic;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.inverter.basic.PlatformUserInverter;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

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
    private SM3 sm3;

    public PlatformUserVO detailById(Long userId) {

        if (Objects.isNull(userId)) {
            log.error("参数缺失");
            return null;
        }

        PlatformUserPO platformUserPO = baseMapper.selectById(userId);

        return platformUserInverter.po2VO(platformUserPO);
    }

    public Boolean userLogin(PlatformUserRO platformUserRO) {
        // 参数校验
        if (Objects.isNull(platformUserRO) || StrUtil.isBlank(platformUserRO.getUsername()) || StrUtil.isBlank(platformUserRO.getPassword())) {
            log.error("参数缺失");
            return false;
        }
        // 密码加密
        platformUserRO.setPassword(sm3.digestHex(platformUserRO.getPassword()));
        PlatformUserPO platformUserPO = baseMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, platformUserRO.getUsername()).eq(PlatformUserPO::getPassword, platformUserRO.getPassword()));
        // 如果无法查询到，则说明密码错误
        if (Objects.isNull(platformUserPO)) {
            return false;
        }
        StpUtil.login(platformUserPO.getUserId());
        return true;
    }
}
