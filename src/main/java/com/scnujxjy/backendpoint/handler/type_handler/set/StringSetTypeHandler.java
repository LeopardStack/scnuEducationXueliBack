package com.scnujxjy.backendpoint.handler.type_handler.set;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Collection;
import java.util.Set;

@MappedTypes(Collection.class)
@MappedJdbcTypes(JdbcType.LONGVARCHAR)
public class StringSetTypeHandler extends SetTypeHandler<String> {
    @Override
    public TypeReference<Set<String>> specificType() {
        return new TypeReference<Set<String>>() {
        };
    }
}
