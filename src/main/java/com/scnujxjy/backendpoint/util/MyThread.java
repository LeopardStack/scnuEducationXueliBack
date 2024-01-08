package com.scnujxjy.backendpoint.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Data
public class MyThread extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(MyThread.class);
    public SCNUXLJYDatabase scnuxljyDatabase;
    String query;
    public static String batch = "";
    public static String type = "";
    public static String key = "";
    public static List<String> values = new ArrayList<>();

    public static int functionSelect=0;
    private ArrayList<HashMap<String, String>> dataDIY;
    public MyThread(String query){
        this.scnuxljyDatabase = new SCNUXLJYDatabase();
        this.query = query;
    }

    @Override
    public void run() {
        try {
            long startTime1 = System.nanoTime();
            log.info(query + " " + Thread.currentThread().getName());
            if(functionSelect == 0) {
                dataDIY = scnuxljyDatabase.getDataDIY(query);
            }
            else if(functionSelect == 1){
                dataDIY = scnuxljyDatabase.getDegreeData(query);
            }
            else if(functionSelect == 2){
                // 获取没有照片信息的数据
                dataDIY = scnuxljyDatabase.getNonPicData(query);

            }
            else if(functionSelect == 3){
                // 获取没有照片信息的数据
                dataDIY = scnuxljyDatabase.getCentainStudentsPhoto(query, batch);

            }
            else if(functionSelect == 5){
                // 获取指定的学生信息 不需要照片信息
                dataDIY = scnuxljyDatabase.getCertainStudents(query);

            }else if(functionSelect == 6){
                // 获取指定的学生信息 不需要照片信息
                dataDIY = scnuxljyDatabase.getStudentsPhotoByDiySql(query, batch, type);

            }else if(functionSelect == 7){
                // 获取退学、转学、休学的缴费信息
                dataDIY = scnuxljyDatabase.getDataDIYWithDict(query, key, values);

            }

            log.info(Thread.currentThread().getName() + " 记录总条目 " + dataDIY.size() + "\n" + dataDIY.get(0));

            // 获取结束时间
            long endTime1 = System.nanoTime();
            // 计算运行时间
            long duration1 = endTime1 - startTime1;

            // 转换为秒并打印
            double durationInSeconds1 = duration1 / 1_000_000_000.0;
            log.info(Thread.currentThread().getName() + " 数据处理时间：" + durationInSeconds1 + " 秒");
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            if(scnuxljyDatabase != null){
                scnuxljyDatabase.close();
            }
        }
    }

    public ArrayList<HashMap<String, String>> getData() {
        return dataDIY;
    }

}
