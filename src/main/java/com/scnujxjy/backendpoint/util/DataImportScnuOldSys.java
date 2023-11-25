package com.scnujxjy.backendpoint.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Data
public class DataImportScnuOldSys {
    /**
     * grade 为年级，即你想抽取哪个年级的学生信息
     * @param grade
     * @return
     */
    public static ArrayList<HashMap<String, String>> getStudentInfos(String grade){
        // 获取开始时间
        long startTime = System.nanoTime();
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = null;
        try {
            scnuxljyDatabase1 = new SCNUXLJYDatabase();
            String query = "SELECT COUNT(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ = '" + grade + "'";
            int total = (int) scnuxljyDatabase1.getValue(query);


            // 线程数量和每个线程处理的数据数量
            int threadCount = 10;
            int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

            ArrayList<MyThread> threads = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                // 每个线程处理的数据范围
                int from = i * sizePerThread + 1;
                int to = (i + 1) * sizePerThread;
                // 避免最后一个线程的数据范围超出总数
                if (to > total) {
                    to = total;
                }

                // 创建SQL查询语句
                String sql = "SELECT * FROM STUDENT_VIEW_WITHPIC WHERE NJ = '" + grade + "' ROWS " + from + " TO " + to;
                MyThread thread = new MyThread(sql);
                threads.add(thread);
            }

            // 启动所有线程
            for (MyThread thread : threads) {
                thread.start();
            }

            // 等待所有线程完成
            for (MyThread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 此时所有线程已经完成，可以从每个线程中获取数据
            for (MyThread thread : threads) {
                allData.addAll(thread.getData());
            }
            log.info("旧系统总获取学生数据：" + allData.size());
            if(!allData.isEmpty()) {
                log.info("旧系统获取的学生数据第一条 " + String.valueOf(allData.get(0)));
            }

            // 获取结束时间
            long endTime = System.nanoTime();

            // 计算运行时间
            long duration = endTime - startTime;

            // 转换为秒并打印
            double durationInSeconds = duration / 1_000_000_000.0;
            log.info("SQL运行时间：" + durationInSeconds + " 秒");


        }catch (Exception e){

        }finally {
            if(scnuxljyDatabase1 != null){
                scnuxljyDatabase1.close();
            }
        }

        return allData;
    }

    /**
     * 获取学生成绩信息
     * @param grade
     * @return
     */

    public static ArrayList<HashMap<String, String>> getGradeInfos(String grade){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM RESULT_VIEW_FULL WHERE NJ = '" + grade + "'";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM RESULT_VIEW_FULL WHERE NJ = '" + grade + "' ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取成绩数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的成绩数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }


    /**
     * 获取学生学费信息
     * @param grade
     * @return
     */
    public static ArrayList<HashMap<String, String>> getStudentFees(String grade){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM CWPAY_VIEW WHERE NJ = '" + grade + "'";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM CWPAY_VIEW WHERE NJ = '" + grade + "' ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取学费数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的学费数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }

    /**
     * 获取学生的学籍异动信息
     * @return
     */

    public static ArrayList<HashMap<String, String>> getStudentXJYDs(){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM STUCHANGE_VIEW";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM STUCHANGE_VIEW ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取学籍异动数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的学籍异动数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }

    /**
     * 获取学生的录取信息
     * @param grade
     * @return
     */

    public static ArrayList<HashMap<String, String>> getStudentLuqus(int grade){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM luqudata" + grade;
        if(grade == -1){
            query = "SELECT COUNT(*) FROM luqudata";
        }
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM luqudata" + grade +" ROWS " + from + " TO " + to;
            if(grade == -1){
                sql = "SELECT * FROM luqudata" +" ROWS " + from + " TO " + to;
            }
            MyThread thread = new MyThread(sql);
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取学生录取数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的学生录取数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }


    /**
     * 获取指定年级的所有教学计划
     * @param grade
     * @return
     */
    public static ArrayList<HashMap<String, String>> getTeachingPlans(String grade){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM course_view WHERE NJ = '" + grade + "'";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM course_view WHERE NJ = '" + grade + "' ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取教学计划数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的教学计划数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }

    /**
     * 获取学位信息
     * @return
     */

    public static ArrayList<HashMap<String, String>> getDegreedatas(){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM xwdata";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM xwdata ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            MyThread.functionSelect = 1;
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取学生学位数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的学生学位数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }


    /**
     * 获取所有班级数据
     * @return
     */
    public static ArrayList<HashMap<String, String>> getClassDatas(){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM classdata";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM classdata ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            MyThread.functionSelect = 2;
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取学生班级数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的学生班级数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }


    /**
     * 获取旧系统中所有的教学计划
     * @return
     */
    public static ArrayList<HashMap<String, String>> getTeachingPlans(){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM courseDATA";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "SELECT * FROM courseDATA ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            MyThread.functionSelect = 2;
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取教学计划数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的教学计划数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }

    /**
     * 获取所有学籍异动数据
     * @return
     */
    public static ArrayList<HashMap<String, String>> getStudentStatusData(){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "select count(*) from classdata c1,classdata c2,stuchangedata , " +
                "studentdata where (substr(xhao,1,2)>='07') and (substr(stuchangedata.xhao,1,1)<>'9') " +
                "and (oldbshi=c1.bshi) and (newbshi=c2.bshi) and (stuchangedata.xhao=studentdata.xhao) ";
        int total = (int) scnuxljyDatabase1.getValue(query);

        // 线程数量和每个线程处理的数据数量
        int threadCount = 10;
        int sizePerThread = total % threadCount > 0 ? (int)total / threadCount + 1 : (int)total / threadCount;

        ArrayList<MyThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据范围
            int from = i * sizePerThread + 1;
            int to = (i + 1) * sizePerThread;
            // 避免最后一个线程的数据范围超出总数
            if (to > total) {
                to = total;
            }

            // 创建SQL查询语句
            String sql = "select stuchangedata.updatetime,stuchangedata.from1,stuchangedata.to1,stuchangedata.from2,stuchangedata.to2," +
                    "stuchangedata.reason, stuchangedata.about, studentdata.sfzh," +
                    "ctype,idnum,studentdata.zkzh ,stuchangedata.xhao,stuchangedata.xm,c1.bshi " +
                    "as old_bshi, c1.nj as old_nj,c1.xshi as old_xshi,c1.zhy as old_zhy,c2.bshi as new_bshi,c2.nj " +
                    "as new_nj, c2.xshi as new_xshi,c2.zhy as new_zhy from classdata c1,classdata c2,stuchangedata , " +
                    "studentdata where (substr(xhao,1,2)>='07') and (substr(stuchangedata.xhao,1,1)<>'9') " +
                    "and (oldbshi=c1.bshi) and (newbshi=c2.bshi) and (stuchangedata.xhao=studentdata.xhao) " +
                    " ROWS " + from + " TO " + to;
            MyThread thread = new MyThread(sql);
            MyThread.functionSelect = 2;
            threads.add(thread);
        }

        // 启动所有线程
        for (MyThread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 此时所有线程已经完成，可以从每个线程中获取数据
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        log.info("旧系统总获取学籍异动数据：" + allData.size());
        if(!allData.isEmpty()) {
            log.info("旧系统获取的学籍异动数据第一条 " + String.valueOf(allData.get(0)));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        log.info("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }
}
