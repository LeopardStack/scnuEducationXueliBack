package com.scnujxjy.backendpoint.dao.mapper.platform_message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.platform_message.SystemMessagePO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.SystemMessageRO;
import com.scnujxjy.backendpoint.model.vo.platform_message.SystemMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemMessageMapper extends BaseMapper<SystemMessagePO> {


    List<SystemMessageVO> selectEntities(@Param("entity") SystemMessageRO systemMessageRO, @Param("page") PageRO pageRO);

    Long selectEntitiesCount(@Param("entity") SystemMessageRO systemMessageRO);


}