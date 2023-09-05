package com.scnujxjy.backendpoint.handler.type_handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@MappedTypes(Collection.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class LongListTypeHandler implements TypeHandler<Collection<Long>> {
    /**
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, Collection<Long> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    /**
     * @param rs
     * @param columnName
     * @return
     * @throws SQLException
     */
    @Override
    public Collection<Long> getResult(ResultSet rs, String columnName) throws SQLException {
        String res = rs.getString(columnName);
        if (StrUtil.isBlank(res)) {
            return null;
        }
        return JSON.parseArray(res, Long.class);
    }

    /**
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public Collection<Long> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String res = rs.getString(columnIndex);
        if (StrUtil.isBlank(res)) {
            return null;
        }
        return JSON.parseArray(res, Long.class);
    }

    /**
     * @param cs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public Collection<Long> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String res = cs.getString(columnIndex);
        if (StrUtil.isBlank(res)) {
            return null;
        }
        return JSON.parseArray(res, Long.class);
    }
}
