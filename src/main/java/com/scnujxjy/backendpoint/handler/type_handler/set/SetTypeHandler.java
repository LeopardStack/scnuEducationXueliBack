package com.scnujxjy.backendpoint.handler.type_handler.set;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnujxjy.backendpoint.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({Set.class})
public abstract class SetTypeHandler<T> extends BaseTypeHandler<Set<T>> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Set<T> ts, JdbcType jdbcType) throws SQLException {
        String content = CollectionUtil.isEmpty(ts) ? null : JSONObject.toJSONString(ts);
        preparedStatement.setString(i, content);
    }

    @Override
    public Set<T> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        try {
            return this.getListByJsonArrayString(resultSet.getString(s));
        } catch (JsonProcessingException e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public Set<T> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        try {
            return this.getListByJsonArrayString(resultSet.getString(i));
        } catch (JsonProcessingException e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public Set<T> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        try {
            return this.getListByJsonArrayString(callableStatement.getString(i));
        } catch (JsonProcessingException e) {
            throw new BusinessException(e);
        }
    }

    private Set<T> getListByJsonArrayString(String content) throws JsonProcessingException {
        return StringUtils.isEmpty(content) ? new HashSet<>() : new ObjectMapper().readValue(content, this.specificType());
    }

    public abstract TypeReference<Set<T>> specificType();

}
