package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.basic.UserActionLogPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.UserActionLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户操作行为日志表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-07
 */
@Service
@Slf4j
public class UserActionLogService extends ServiceImpl<UserActionLogMapper, UserActionLogPO> implements IService<UserActionLogPO> {

}
