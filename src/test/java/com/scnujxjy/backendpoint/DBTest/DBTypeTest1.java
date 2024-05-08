package com.scnujxjy.backendpoint.DBTest;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
@Slf4j
public class DBTypeTest1 {

    @Resource
    private DataSource dataSource;

    @Test
    public void test1(){
        try{
            //看一下默认数据源
            System.out.println(dataSource.getClass());
            //获得连接
            Connection connection = dataSource.getConnection();
            System.out.println(connection);
        }catch (Exception e){
            log.error("查看数据源信息失败 " + e);
        }

    }

    /**
     * SQLSelectStatement包含一个SQLSelect，SQLSelect包含一个SQLSelectQuery。
     * SQLSelectQuery有主要的两个派生类，分别是SQLSelectQueryBlock(单表sql查询)和SQLUnionQuery(联合查询)。
     */
    @Test
    public void SQLSelectQuery() {
        printSQLSelectQueryAST("select * from platform_user");
        printSQLSelectQueryAST("select name from platform_user union select name from other_table");
    }

    public void printSQLSelectQueryAST(String sql) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        // 打印语法树
        String json = JSON.toJSONString(sqlStatement, true);
        System.out.println(json);
    }

}
