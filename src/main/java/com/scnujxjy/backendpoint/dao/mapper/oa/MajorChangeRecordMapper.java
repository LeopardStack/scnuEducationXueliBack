package com.scnujxjy.backendpoint.dao.mapper.oa;

import com.scnujxjy.backendpoint.dao.entity.oa.MajorChangeRecordPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.ro.oa.MajorChangeDBFInfoRO;
import com.scnujxjy.backendpoint.model.ro.oa.MajorChangeRecordRO;
import com.scnujxjy.backendpoint.model.vo.oa.MajorChangeRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 转专业记录表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
public interface MajorChangeRecordMapper extends BaseMapper<MajorChangeRecordPO> {

    List<MajorChangeRecordVO> getMajorChangeInfos(@Param("entity")MajorChangeRecordRO entity,
                                                  @Param("pageNumber")Long pageNumber,
                                                  @Param("pageSize")Long pageSize);

    long getMajorChangeInfosCount(@Param("entity")MajorChangeRecordRO entity);

    List<String> getDistinctGrades(@Param("entity")MajorChangeRecordRO entity);

    List<String> getDistinctRemarks(@Param("entity")MajorChangeRecordRO entity);

    List<MajorChangeRecordVO> getMajorChangeInfosForGenerateDBF(@Param("entity")MajorChangeDBFInfoRO majorChangeDBFInfoRO);

    int deleteDiy(@Param("entity")MajorChangeRecordRO entity);
    int deleteNe(@Param("entity")MajorChangeRecordRO entity);

    List<MajorChangeRecordVO> getSingleMajorChangeInfos(@Param("entity")MajorChangeRecordRO entity);
}
