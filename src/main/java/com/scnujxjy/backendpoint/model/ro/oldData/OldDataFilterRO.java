package com.scnujxjy.backendpoint.model.ro.oldData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 查询旧系统的筛选参数
 * startYear 开始年份
 * endYear 开始年份
 * @author 谢辉龙
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class OldDataFilterRO {
    private String startYear;

    private String endYear;

    /**
     * 数据类型
     * 学籍数据、缴费数据、成绩数据、班级数据、教学计划、学籍异动
     */
    private String dataType;

    /**
     * 强制更新 无论其是否有 redis 内容
     * 但是必须等异步方法执行完
     */
    private Boolean updateAny;
}
