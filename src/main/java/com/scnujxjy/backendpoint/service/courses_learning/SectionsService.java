package com.scnujxjy.backendpoint.service.courses_learning;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.RetakeStudentsPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.SectionsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.RetakeStudentsMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.SectionsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
@Service
@Slf4j
public class SectionsService extends ServiceImpl<SectionsMapper, SectionsPO>
        implements IService<SectionsPO> {

}
