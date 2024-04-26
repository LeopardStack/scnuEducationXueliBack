//package com.scnujxjy.backendpoint.dao.mapper.oa;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.scnujxjy.backendpoint.dao.entity.oa.SystemPopOutMessagePO;
//import org.apache.ibatis.annotations.Mapper;
//
//@Mapper
//public interface SystemPopOutMessageMapper extends BaseMapper<SystemPopOutMessagePO> {
//    /**
//     * delete by primary key
//     *
//     * @param id primaryKey
//     * @return deleteCount
//     */
//    int deleteByPrimaryKey(Long id);
//
//    /**
//     * insert record to table
//     *
//     * @param record the record
//     * @return insert count
//     */
//    int insert(SystemPopOutMessagePO record);
//
//    /**
//     * insert record to table selective
//     *
//     * @param record the record
//     * @return insert count
//     */
//    int insertSelective(SystemPopOutMessagePO record);
//
//    /**
//     * select by primary key
//     *
//     * @param id primary key
//     * @return object by primary key
//     */
//    SystemPopOutMessagePO selectByPrimaryKey(Long id);
//
//    /**
//     * update record selective
//     *
//     * @param record the updated record
//     * @return update count
//     */
//    int updateByPrimaryKeySelective(SystemPopOutMessagePO record);
//
//    /**
//     * update record
//     *
//     * @param record the updated record
//     * @return update count
//     */
//    int updateByPrimaryKey(SystemPopOutMessagePO record);
//}