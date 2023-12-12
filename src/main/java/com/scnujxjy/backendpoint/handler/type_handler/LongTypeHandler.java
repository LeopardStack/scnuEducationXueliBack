package com.scnujxjy.backendpoint.handler.type_handler;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Collection;
import java.util.List;

@MappedTypes(Collection.class)
@MappedJdbcTypes(JdbcType.LONGVARCHAR)
public class LongTypeHandler extends ListTypeHandler<Long> {

    @Override
    public TypeReference<List<Long>> specificType() {
        return new TypeReference<List<Long>>() {
        };
    }
}