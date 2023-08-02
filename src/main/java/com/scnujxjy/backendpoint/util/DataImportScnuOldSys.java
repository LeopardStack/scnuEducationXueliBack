package com.scnujxjy.backendpoint.util;

import java.util.ArrayList;
import java.util.HashMap;

public class DataImportScnuOldSys {
    /**
     * grade 为年级，即你想抽取哪个年级的学生信息
     * @param grade
     * @return
     */
    public static ArrayList<HashMap<String, String>> getStudentInfos(String grade){
        // 获取开始时间
        long startTime = System.nanoTime();

        // 获取总数
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
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
        ArrayList<HashMap<String, String>> allData = new ArrayList<>();
        for (MyThread thread : threads) {
            allData.addAll(thread.getData());
        }
        System.out.println("总获取数据：" + allData.size());
        if(allData.size() > 0) {
            System.out.println(allData.get(0));
            System.out.println(allData.get(allData.size()-1));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }


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
        System.out.println("总获取数据：" + allData.size());
        if(allData.size() > 0) {
            System.out.println(allData.get(0));
            System.out.println(allData.get(allData.size()-1));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }


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
        System.out.println("总获取数据：" + allData.size());
        if(allData.size() > 0) {
            System.out.println(allData.get(0));
            System.out.println(allData.get(allData.size()-1));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }

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
        System.out.println("总获取数据：" + allData.size());
        if(allData.size() > 0) {
            System.out.println(allData.get(0));
            System.out.println(allData.get(allData.size()-1));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }

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
        System.out.println("总获取数据：" + allData.size());
        if(allData.size() > 0) {
            System.out.println(allData.get(0));
            System.out.println(allData.get(allData.size()-1));
        }

        // 获取结束时间
        long endTime = System.nanoTime();

        // 计算运行时间
        long duration = endTime - startTime;

        // 转换为秒并打印
        double durationInSeconds = duration / 1_000_000_000.0;
        System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

        return allData;
    }
}
