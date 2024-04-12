package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Redis 键名/键前缀 管理
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RedisKeysEnum {

    STUDENT_STATUS_QUERY_PREFIX("students_status_query_"),
    GRADE_INFO_QUERY_PREFIX("grade_info_query_"),;

    String redisKeyOrPrefix;

    public String generateKey(String suffix){
        return redisKeyOrPrefix + suffix;
    }
}
