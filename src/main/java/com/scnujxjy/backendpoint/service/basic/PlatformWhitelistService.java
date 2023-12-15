package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformWhitelistPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformWhitelistMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 平台白名单表 服务实现类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-15
 */
@Service
public class PlatformWhitelistService extends ServiceImpl<PlatformWhitelistMapper, PlatformWhitelistPO> implements IService<PlatformWhitelistPO> {

}
