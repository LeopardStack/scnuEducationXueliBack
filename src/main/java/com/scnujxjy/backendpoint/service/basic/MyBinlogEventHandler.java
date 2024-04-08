package com.scnujxjy.backendpoint.service.basic;

import com.alibaba.fastjson.JSON;
import com.gitee.Jmysy.binlog4j.core.BinlogEvent;
import com.gitee.Jmysy.binlog4j.core.IBinlogEventHandler;
import com.gitee.Jmysy.binlog4j.springboot.starter.annotation.BinlogSubscriber;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@BinlogSubscriber(clientName = "master")
public class MyBinlogEventHandler implements IBinlogEventHandler<Object> {
    @Override
    public void onInsert(BinlogEvent<Object> binlogEvent) {
        log.info("onInsert==" + JSON.toJSONString(binlogEvent));
    }

    @Override
    public void onUpdate(BinlogEvent<Object> binlogEvent) {
        log.info("onUpdate==" + JSON.toJSONString(binlogEvent));
    }

    @Override
    public void onDelete(BinlogEvent<Object> binlogEvent) {
        log.info("onDelete==" + JSON.toJSONString(binlogEvent));
    }

    @Override
    public boolean isHandle(String s, String s1) {
        log.info("All==" + s + "  " + s1);
        return true;
    }
}
