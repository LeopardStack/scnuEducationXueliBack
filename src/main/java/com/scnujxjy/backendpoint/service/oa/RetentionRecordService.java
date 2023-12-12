package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.ResumptionRecordPO;
import com.scnujxjy.backendpoint.dao.entity.oa.RetentionRecordPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.oa.ResumptionRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.RetentionRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 留级记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class RetentionRecordService extends ServiceImpl<RetentionRecordMapper, RetentionRecordPO> implements IService<RetentionRecordPO>  {

}
