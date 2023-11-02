package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.basic.PermissionPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.GlobalConfigMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PermissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-10-29
 */
@Service
@Slf4j
public class GlobalConfigService extends ServiceImpl<GlobalConfigMapper, GlobalConfigPO> implements IService<GlobalConfigPO>{

}
