package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.DropoutRecordPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.oa.DropoutRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退学记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class DropoutRecordService extends ServiceImpl<DropoutRecordMapper, DropoutRecordPO> implements IService<DropoutRecordPO> {

}
