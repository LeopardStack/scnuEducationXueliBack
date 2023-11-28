package com.scnujxjy.backendpoint.service.oa;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.oa.DropoutRecordPO;
import com.scnujxjy.backendpoint.dao.entity.oa.MajorChangeRecordPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.oa.DropoutRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.MajorChangeRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 转专业记录表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Service
@Slf4j
public class  MajorChangeRecordService extends ServiceImpl<MajorChangeRecordMapper, MajorChangeRecordPO> implements IService<MajorChangeRecordPO> {

}
