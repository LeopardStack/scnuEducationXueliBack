package com.scnujxjy.backendpoint.dao.mapper.oa;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.oa.SystemMessagePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemMessageMapper extends BaseMapper<SystemMessagePO> {
    /**
     * delete by primary key
     *
     * @param id primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Long id);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(SystemMessagePO record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(SystemMessagePO record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    SystemMessagePO selectByPrimaryKey(Long id);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(SystemMessagePO record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(SystemMessagePO record);
}