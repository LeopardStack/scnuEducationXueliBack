package com.scnujxjy.backendpoint.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

public class SCNUXLJYDatabase {
    private static final Logger logger = LoggerFactory.getLogger(SCNUXLJYDatabase.class);
    private static boolean print_log_ident_1 = true;
    final String JDBC_DRIVER = "interbase.interclient.Driver"; // 请替换为你下载的 JDBC 驱动的类名
    final String DB_URL = "jdbc:interbase://10.248.5.64:3050/d:\\JJDATA\\JJDATA.IB"; // 请替换为你的数据库 URL
    final String DB_URL1 = "jdbc:interbase://10.248.5.64:3050/d:\\JJDATA\\JJDATA.IB?charSet=GBK";
    // 请替换为你的数据库 URL

    // 数据库用户名和密码
    final String USER = "SYSDBA";
    final String PASS = "sysadmin";
    Connection conn = null;
    Statement stmt = null;
    int error_count = 1;

    public void init(){
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();
        } catch(SQLException se) {
            // 处理 JDBC 错误
            System.out.println("SQL 错误");
            error_count += 1;
            if(error_count < 3){
                init();
            }
            se.printStackTrace();
        } catch(Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        }

    }
    public SCNUXLJYDatabase(){
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开连接
            // System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查询
            // System.out.println("Creating statement...");
            stmt = conn.createStatement();
        }
        catch(SQLException se) {
            // 处理 JDBC 错误
            System.out.println("SQL 错误");
            error_count += 1;
            if(error_count < 3){
                init();
            }
            se.printStackTrace();
        } catch(Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(String sql){
        try {
            ResultSet rs = stmt.executeQuery(sql);
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (SQLException e) {
            logger.error(e.toString());
            return null;
        }
    }

    public ArrayList<HashMap<String, String>> getData(String tableView){
        String sql;
        ResultSet rs = null;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        sql = "SELECT * FROM " + tableView;  // 请替换为你要查询的表名
        try {
            // 获取开始时间
            long startTime = System.nanoTime();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取结束时间
            long endTime = System.nanoTime();

            // 计算运行时间
            long duration = endTime - startTime;

            // 转换为秒并打印
            double durationInSeconds = duration / 1_000_000_000.0;
            System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

            // 获取列数
            int numberOfColumns = rsmd.getColumnCount();
            System.out.println("拿到数据了 ");

            while (rs.next()) {
                HashMap<String, String> hashMap = new HashMap<>();
                // 遍历每一列
                for (int i = 1; i <= numberOfColumns; i++) {
                    byte[] columnValue = rs.getBytes(i);
                    String name = rsmd.getColumnName(i);
                    if (columnValue == null) {
                        //System.out.println("Column " + name + " value: " + " NULL");
                        hashMap.put(name, "NULL");
                    } else if (name.contains("PIC")) {
                        Blob blob = rs.getBlob(i);
                        byte[] picBytes = blob.getBytes(1, (int) blob.length());
                        ByteArrayInputStream bais = new ByteArrayInputStream(picBytes);
                        BufferedImage img = null;
                        try {
                            img = ImageIO.read(bais);
                            hashMap.put(name, ".jpg");
                            try {
                                File outputfile = new File("saved.jpg");
                                ImageIO.write(img, "jpg", outputfile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // System.out.println("Column " + name + " value: " + new String(picBytes));

                    } else {
                        hashMap.put(name, new String(columnValue, "UTF-8"));
                        //System.out.println("Column " + name + " value: " + new String(columnValue, "GB2312"));
                    }
                }
                ret.add(hashMap);
                System.out.println(hashMap);
            }
        } catch (SQLException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    public ArrayList<HashMap<String, String>> getDataPages(String tableView, int start, int offset){
        String sql;
        ResultSet rs = null;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        sql = "SELECT * FROM " + tableView + " ROWS " + start + " TO " + offset;  // 请替换为你要查询的表名
        try {
            // 获取开始时间
            long startTime = System.nanoTime();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取结束时间
            long endTime = System.nanoTime();

            // 计算运行时间
            long duration = endTime - startTime;

            // 转换为秒并打印
            double durationInSeconds = duration / 1_000_000_000.0;
            System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

            // 获取列数
            int numberOfColumns = rsmd.getColumnCount();
            System.out.println("拿到数据了 ");

            while (rs.next()) {
                HashMap<String, String> hashMap = new HashMap<>();
                // 遍历每一列
                for (int i = 1; i <= numberOfColumns; i++) {
                    byte[] columnValue = rs.getBytes(i);
                    String name = rsmd.getColumnName(i);
                    if (columnValue == null) {
                        //System.out.println("Column " + name + " value: " + " NULL");
                        hashMap.put(name, "NULL");
                    } else if (name.contains("PIC")) {
                        Blob blob = rs.getBlob(i);
                        byte[] picBytes = blob.getBytes(1, (int) blob.length());
                        ByteArrayInputStream bais = new ByteArrayInputStream(picBytes);
                        BufferedImage img = null;
                        try {
                            img = ImageIO.read(bais);
                            hashMap.put(name, ".jpg");
                            try {
                                File outputfile = new File("saved.jpg");
                                ImageIO.write(img, "jpg", outputfile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // System.out.println("Column " + name + " value: " + new String(picBytes));

                    } else {
                        hashMap.put(name, new String(columnValue, "GB2312"));
                        //System.out.println("Column " + name + " value: " + new String(columnValue, "GB2312"));
                    }
                }
                ret.add(hashMap);
                System.out.println(hashMap);
            }
        } catch (SQLException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    public ArrayList<HashMap<String, String>> getDataDIY2(String sql, String picKey){
        ResultSet rs = null;
        long startTime1 = 0;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        try {
            // 获取开始时间
            long startTime = System.nanoTime();

            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取结束时间
            long endTime = System.nanoTime();

            // 计算运行时间
            long duration = endTime - startTime;

            // 转换为秒并打印
            double durationInSeconds = duration / 1_000_000_000.0;
            // System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

            // 获取列数
            int numberOfColumns = rsmd.getColumnCount();
            // System.out.println("拿到数据了 ");

            // 获取开始时间
            startTime1 = System.nanoTime();
            while (rs.next()) {
                HashMap<String, String> hashMap = new HashMap<>();
                // 记录一下考生号
                String ksh = "";
                BufferedImage img = null;
                // 遍历每一列
                for (int i = 1; i <= numberOfColumns; i++) {
                    // System.out.println(hashMap);
                    String name = rsmd.getColumnName(i);
                    byte[] columnValue = null;
                    if(name.equals("JINE")){
                        // 学费
                        hashMap.put(name, String.valueOf(rs.getDouble(i)));
                        continue;
                    }
                    else if(name.equals("PXZF")){
                        // 分数
                        hashMap.put(name, String.valueOf(rs.getInt(i)));
                        continue;
                    }
                    else{
                        columnValue = rs.getBytes(i);
                    }

                    if (columnValue == null) {
                        //System.out.println("Column " + name + " value: " + " NULL");
                        hashMap.put(name, "NULL");
                    } else if (name.contains("PIC")) {
                        Blob blob = rs.getBlob(i);
                        InputStream is = blob.getBinaryStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int len;
                        while (true) {
                            try {
                                if ((len = is.read(buffer)) == -1) break;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            os.write(buffer, 0, len);
                        }
                        byte[] bytes = os.toByteArray();

                        InputStream in = new ByteArrayInputStream(bytes);
                        try {
                            img = ImageIO.read(in);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // System.out.println("Column " + name + " value: " + new String(picBytes));

                    } else {
                        hashMap.put(name, new String(columnValue, "GB2312"));
                        //System.out.println("Column " + name + " value: " + new String(columnValue, "GB2312"));
                    }
                }
                if(hashMap.get("BH").equals("澳门")){
                    continue;
                }
                else{
                    // 存储一下照片
                    ksh = hashMap.get(picKey);
                    // System.out.print("考生号 " + ksh + "  ");
                    if(ksh != null){
                        if (img != null) {
                            File outputfile = new File("./pictures/" + ksh + ".jpg");
                            try {
                                ImageIO.write(img, "jpg", outputfile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // System.out.println(" 照片为 " + ksh + ".jpg");
                        }
                    }
                    ret.add(hashMap);
                }
                // System.out.println(hashMap);
            }
        } catch (SQLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 获取结束时间
        long endTime1 = System.nanoTime();
        // 计算运行时间
        long duration1 = endTime1 - startTime1;

        // 转换为秒并打印
        double durationInSeconds1 = duration1 / 1_000_000_000.0;
        // System.out.println("数据处理时间：" + durationInSeconds1 + " 秒");
        return ret;
    }

    public ArrayList<HashMap<String, String>> getDataDIY(String sql){
        ResultSet rs = null;
        long startTime1 = 0;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        try {
            // 获取开始时间
            long startTime = System.nanoTime();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
//            System.out.println(rs.next());
            // 获取结束时间
            long endTime = System.nanoTime();

            // 计算运行时间
            long duration = endTime - startTime;

            // 转换为秒并打印
            double durationInSeconds = duration / 1_000_000_000.0;
            // System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

            // 获取列数
            int numberOfColumns = rsmd.getColumnCount();
            // System.out.println("拿到数据了 ");

            // 获取开始时间
            startTime1 = System.nanoTime();
            while (rs.next()) {
                HashMap<String, String> hashMap = new HashMap<>();
                // 记录一下考生号
                String ksh = "";
                BufferedImage imgBY = null;
                BufferedImage imgRX = null;
                // 遍历每一列
                for (int i = 1; i <= numberOfColumns; i++) {
                    // System.out.println(hashMap);
                    String name = rsmd.getColumnName(i);
                    byte[] columnValue = null;
                    if(name.equals("JINE")){
                        // 学费
                        hashMap.put(name, String.valueOf(rs.getDouble(i)));
                        continue;
                    }
                    else if(name.equals("PXZF")){
                        // 分数
                        hashMap.put(name, String.valueOf(rs.getInt(i)));
                        continue;
                    }
                    else if(name.equals("CHANGENUM")){
                        // 分数
                        hashMap.put(name, String.valueOf(rs.getInt(i)));
                        continue;
                    }else if(name.equals("UPDATETIME")){
                        // 分数
                        hashMap.put(name, String.valueOf(rs.getDate(i)));
                        continue;
                    }
                    else{
                        try {
                            columnValue = rs.getBytes(i);
                        }catch (Exception e){
                            logger.error(e.toString() + '\n' + name);
                        }
                    }

                    if (columnValue == null) {
                        //System.out.println("Column " + name + " value: " + " NULL");
                        hashMap.put(name, "NULL");
                    } else if (name.trim().equals("PIC") || name.trim().equals("RXPIC")) {
                        Blob blob = rs.getBlob(i);
                        InputStream is = blob.getBinaryStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int len;
                        while (true) {
                            try {
                                if ((len = is.read(buffer)) == -1) {
                                    break;
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            os.write(buffer, 0, len);
                        }
                        byte[] bytes = os.toByteArray();

                        InputStream in = new ByteArrayInputStream(bytes);
                        try {
                            if(name.trim().equals("PIC")) {
                                imgBY = ImageIO.read(in);
                            }else{
                                imgRX = ImageIO.read(in);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // System.out.println("Column " + name + " value: " + new String(picBytes));

                    } else {
//                        String tmp = new String(columnValue, "GB2312");
                        String tmp = new String(columnValue, "GBK");
                        tmp = tmp.trim();
//                        if(name.equals("KSH") && tmp.matches("\\d*")){
//
//                        }
//                        else if (name.equals("KSH")){
//                            tmp = new String(columnValue, "GBK");
//                        }
                        hashMap.put(name, tmp);
                        //System.out.println("Column " + name + " value: " + new String(columnValue, "GB2312"));
                    }
                }
                // 存储一下照片
                ksh = hashMap.get("KSH");
                // System.out.print("考生号 " + ksh + "  ");
                if(ksh != null && hashMap.containsKey("NJ")){
                    String nj = hashMap.get("NJ");
                    if (imgRX != null && nj != null && nj.length() > 0) {
                        String directoryPath = "./xuelistudentpictures" + "/" + nj + "/" + "import";
                        File directory = new File(directoryPath);
                        if (!directory.exists()){
                            // 如果目录不存在则创建
                            boolean result = directory.mkdirs();
                            if(result) {
                                System.out.println("Directory was created successfully");
                            } else {
                                System.out.println("Directory creation failed");
                            }
                        }

                        File outputFile = new File(directoryPath + "/" + ksh + ".jpg");

                        try {
                            ImageIO.write(imgRX, "jpg", outputFile);
                            hashMap.put("RXPIC", directoryPath + "/" + ksh + ".jpg");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // System.out.println(" 照片为 " + ksh + ".jpg");
                    }
                    if(imgBY != null && nj != null && nj.length() > 0){
                        String directoryPath = "./xuelistudentpictures" + "/" + nj + "/" + "export";
                        File directory = new File(directoryPath);
                        if (!directory.exists()){
                            // 如果目录不存在则创建
                            boolean result = directory.mkdirs();
                            if(result) {
                                System.out.println("Directory was created successfully");
                            } else {
                                System.out.println("Directory creation failed");
                            }
                        }

                        File outputFile = new File(directoryPath + "/" + ksh + ".jpg");

                        try {
                            ImageIO.write(imgBY, "jpg", outputFile);
                            hashMap.put("BYPIC", directoryPath + "/" + ksh + ".jpg");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                ret.add(hashMap);
                // System.out.println(hashMap);
            }
        } catch (SQLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 获取结束时间
        long endTime1 = System.nanoTime();
        // 计算运行时间
        long duration1 = endTime1 - startTime1;

        // 转换为秒并打印
        double durationInSeconds1 = duration1 / 1_000_000_000.0;
        // System.out.println("数据处理时间：" + durationInSeconds1 + " 秒");
        return ret;
    }

    public ArrayList<HashMap<String, String>> getDegreeData(String sql) {
        ResultSet rs = null;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        try {
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            // 获取列数
            int numberOfColumns = rsmd.getColumnCount();
            while (rs.next()) {
                HashMap<String, String> hashMap = new HashMap<>();
                BufferedImage imgXW = null;
                for (int i = 1; i <= numberOfColumns; i++) {
                    // System.out.println(hashMap);
                    String name = rsmd.getColumnName(i);
                    if(name.equals("PICTURE")){
                        Blob blob = rs.getBlob(i);
                        if(blob == null){
                            continue;
                        }
                        InputStream is = blob.getBinaryStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int len;
                        while (true) {
                            try {
                                if ((len = is.read(buffer)) == -1) {
                                    break;
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            os.write(buffer, 0, len);
                        }
                        byte[] bytes = os.toByteArray();

                        InputStream in = new ByteArrayInputStream(bytes);
                        try {
                            imgXW = ImageIO.read(in);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        byte[] columnValue = null;
                        try {
                            columnValue = rs.getBytes(i);
                            if(columnValue == null){
                                hashMap.put(name, null);
                                continue;
                            }
                            String tmp = new String(columnValue, "GBK");
                            tmp = tmp.trim();
                            hashMap.put(name, tmp);
                        }catch (Exception e){
                            logger.error(e.toString() + '\n' + name);
                        }
                    }
                }

                String nj = hashMap.get("FZRQ");
                String sfzh = hashMap.get("SFZH");

                if(imgXW != null && nj != null && sfzh != null){
                    String directoryPath = "./xueweipictures" + "/" + nj + "/" + hashMap.get("PYXS");
                    File directory = new File(directoryPath);
                    if (!directory.exists()){
                        // 如果目录不存在则创建
                        boolean result = directory.mkdirs();
                        if(result) {
                            System.out.println("Directory was created successfully");
                        } else {
                            System.out.println("Directory creation failed");
                        }
                    }
                    if (sfzh.matches(".*[<>:\"/\\\\|?*].*")) {
                        logger.error("Invalid sfzh: " + sfzh);
                        sfzh = sfzh.replaceAll("[<>:\"/\\\\|?*]", "_");
                    }

                    File outputFile = new File(directoryPath + "/" + sfzh + ".jpg");

                    try {
                        ImageIO.write(imgXW, "jpg", outputFile);
                        hashMap.put("XWPIC", directoryPath + "/" + sfzh + ".jpg");
                    } catch (Exception e) {
                        logger.error("照片写入失败 " + e.toString());
                    }
                }
                else{
                    logger.error("信息缺失 " + hashMap);
                }
//                logger.info("获取数据 " + hashMap);
                ret.add(hashMap);
            }
        }catch (Exception e){
            logger.error(e.toString());
        }
        return ret;
    }

    public ArrayList<HashMap<String, String>> getDataDIYMultiThread(int start, int pageSize, int threadNums, int nj, String picKey) {
        ExecutorService executor = Executors.newFixedThreadPool(threadNums); // 创建一个包含10个线程的线程池

        List<Future<ArrayList<HashMap<String, String>>>> futures = new ArrayList<>();

        /**
         * 从 STUDENTDATA 数据库中选择 ZKZH 22 开头的所有学生，并进行分页
         * SELECT *
         * FROM STUDENTDATA
         * WHERE ZKZH LIKE '22%'
         * ORDER BY ZKZH ROWS 0 TO 100;
         */

        int count1 = 0;
        for (int i = 0; i < threadNums; i++) {
            int finalCount = count1;
            Callable<ArrayList<HashMap<String, String>>> task = () -> {
                int start1 = start + finalCount * pageSize;
                int end1 = start + (finalCount + 1) * pageSize-1;
                String sql1 = "SELECT * " +
                        "FROM STUDENT_VIEW_WITHPIC " +
                        "WHERE BH != '澳门' AND NJ='2023' ROWS " + (start1) + " TO " + (end1);
                System.out.println("SQL is " + sql1);

                SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
                try {
                    ArrayList<HashMap<String, String>> student_view_withpic = scnuxljyDatabase.
                            getDataDIY2(sql1, picKey);
                    return student_view_withpic;
                } finally {
                    scnuxljyDatabase.close();
                }
            };
            count1 += 1;
            futures.add(executor.submit(task)); // 提交任务并添加返回的Future到列表中
        }

        executor.shutdown(); // 关闭线程池

        // 获取并处理每个任务的结果
        ArrayList<HashMap<String, String>> all = new ArrayList<>();
        for (Future<ArrayList<HashMap<String, String>>> future : futures) {
            try {
                ArrayList<HashMap<String, String>> student_view_withpic = future.get(); // 获取任务的结果
                System.out.println("人数 ：" + student_view_withpic.size());
                all.addAll(student_view_withpic);
            } catch (InterruptedException | ExecutionException e) {
                // 处理可能的异常
                e.printStackTrace();
            }
        }
        return all;
    }


    public ArrayList<HashMap<String, String>> getDataDIYExclude(String sql, String picKey, String picName,
                                                                String excludeName, String excludeValue, String child_file){
        ResultSet rs = null;
        long startTime1 = 0;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        try {
            // 获取开始时间
            long startTime = System.nanoTime();

            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取结束时间
            long endTime = System.nanoTime();

            // 计算运行时间
            long duration = endTime - startTime;

            // 转换为秒并打印
            double durationInSeconds = duration / 1_000_000_000.0;
            // System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

            // 获取列数
            int numberOfColumns = rsmd.getColumnCount();
            // System.out.println("拿到数据了 ");

            // 获取开始时间
            startTime1 = System.nanoTime();
            while (rs.next()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                // 记录一下考生号
                // 遍历每一列
                for (int i = 1; i <= numberOfColumns; i++) {
                    // System.out.println(hashMap);
                    String name = rsmd.getColumnName(i);
                    byte[] columnValue = null;
                    if(name.equals("JINE")){
                        // 学费
                        hashMap.put(name, String.valueOf(rs.getDouble(i)));
                        continue;
                    }
                    else if(name.equals("PXZF")){
                        // 分数
                        hashMap.put(name, String.valueOf(rs.getInt(i)));
                        continue;
                    }
                    else{
                        columnValue = rs.getBytes(i);
                    }

                    if (columnValue == null) {
                        //System.out.println("Column " + name + " value: " + " NULL");
                        hashMap.put(name, "NULL");
                    } else if (name.contains("PIC")) {
                        Blob blob = rs.getBlob(i);
                        InputStream is = blob.getBinaryStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int len;
                        while (true) {
                            try {
                                if ((len = is.read(buffer)) == -1) {
                                    break;
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            os.write(buffer, 0, len);
                        }
                        byte[] bytes = os.toByteArray();

                        InputStream in = new ByteArrayInputStream(bytes);
                        try {
                            BufferedImage img = ImageIO.read(in);
                            hashMap.put(name, img);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // System.out.println("Column " + name + " value: " + new String(picBytes));

                    } else {
                        hashMap.put(name, new String(columnValue, "GB2312"));
                        //System.out.println("Column " + name + " value: " + new String(columnValue, "GB2312"));
                    }
                }


                if(hashMap.get(excludeName).equals(excludeValue)){
                    continue;
                }else{
                    Object o = hashMap.get(picKey);
                    // System.out.print("考生号 " + ksh + "  ");
                    if(picKey.equals("NULL") && print_log_ident_1){
                        logger.warn("不需要打印照片");
                        print_log_ident_1 = false;
                    }
                    else if(o != null){
                        try {
                            BufferedImage bufferedImage = (BufferedImage) o;
                            if (bufferedImage != null) {

                                // 确保父目录存在
                                String parentDirPath = "./pictures" + "/" + child_file;
                                File parentDir = new File(parentDirPath);
                                if (!parentDir.exists()) {
                                    parentDir.mkdirs();
                                }


                                File outputfile = new File(parentDirPath + hashMap.get(picName) + ".jpg");
                                try {
                                    ImageIO.write(bufferedImage, "jpg", outputfile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                // System.out.println(" 照片为 " + ksh + ".jpg");
                            }
                        }catch (Exception e){
                            logger.error(e.toString());
                        }
                    }
                    // 将字典中的字符串值全部保存下来
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    for(String k: hashMap.keySet()){
                        if(hashMap.get(k) instanceof String){
                            hashMap1.put(k, (String)hashMap.get(k));
                        }
                    }
                    ret.add(hashMap1);
                }

            }
        } catch (SQLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 获取结束时间
        long endTime1 = System.nanoTime();
        // 计算运行时间
        long duration1 = endTime1 - startTime1;

        // 转换为秒并打印
        double durationInSeconds1 = duration1 / 1_000_000_000.0;
        // System.out.println("数据处理时间：" + durationInSeconds1 + " 秒");
        return ret;
    }


    public ArrayList<HashMap<String, String>> getDataDIYExclude2(String sql, String picKey, String picName,
                                                                 String excludeName, HashSet<String> excludeValue, String child_file){
        ResultSet rs = null;
        long startTime1 = 0;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        try {
            // 获取开始时间
            long startTime = System.nanoTime();

            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取结束时间
            long endTime = System.nanoTime();

            // 计算运行时间
            long duration = endTime - startTime;

            // 转换为秒并打印
            double durationInSeconds = duration / 1_000_000_000.0;
            // System.out.println("SQL运行时间：" + durationInSeconds + " 秒");

            // 获取列数
            int numberOfColumns = rsmd.getColumnCount();
            // System.out.println("拿到数据了 ");

            // 获取开始时间
            startTime1 = System.nanoTime();
            while (rs.next()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                // 记录一下考生号
                // 遍历每一列
                for (int i = 1; i <= numberOfColumns; i++) {
                    // System.out.println(hashMap);
                    String name = rsmd.getColumnName(i);
                    byte[] columnValue = null;
                    if(name.equals("JINE")){
                        // 学费
                        hashMap.put(name, String.valueOf(rs.getDouble(i)));
                        continue;
                    }
                    else if(name.equals("PXZF")){
                        // 分数
                        hashMap.put(name, String.valueOf(rs.getInt(i)));
                        continue;
                    }
                    else{
                        columnValue = rs.getBytes(i);
                    }

                    if (columnValue == null) {
                        //System.out.println("Column " + name + " value: " + " NULL");
                        hashMap.put(name, "NULL");
                    } else if (name.contains("PIC")) {
                        Blob blob = rs.getBlob(i);
                        InputStream is = blob.getBinaryStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int len;
                        while (true) {
                            try {
                                if ((len = is.read(buffer)) == -1) {
                                    break;
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            os.write(buffer, 0, len);
                        }
                        byte[] bytes = os.toByteArray();

                        InputStream in = new ByteArrayInputStream(bytes);
                        try {
                            BufferedImage img = ImageIO.read(in);
                            hashMap.put(name, img);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // System.out.println("Column " + name + " value: " + new String(picBytes));

                    } else {
                        hashMap.put(name, new String(columnValue, "GB2312"));
                        //System.out.println("Column " + name + " value: " + new String(columnValue, "GB2312"));
                    }
                }

                if(excludeValue.contains(hashMap.get(excludeName))){
                    Object o = hashMap.get(picKey);
                    // System.out.print("考生号 " + ksh + "  ");
                    if(picKey.equals("NULL") && print_log_ident_1){
                        logger.warn("不需要打印照片");
                        print_log_ident_1 = false;
                    }
                    else if(o != null){
                        try {
                            BufferedImage bufferedImage = (BufferedImage) o;
                            if (bufferedImage != null) {

                                // 确保父目录存在
                                String parentDirPath = "./pictures" + "/" + child_file;
                                File parentDir = new File(parentDirPath);
                                if (!parentDir.exists()) {
                                    parentDir.mkdirs();
                                }


                                File outputfile = new File(parentDirPath + hashMap.get(picName) + ".jpg");
                                try {
                                    ImageIO.write(bufferedImage, "jpg", outputfile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                // System.out.println(" 照片为 " + ksh + ".jpg");
                            }
                        }catch (Exception e){
                            logger.error(e.toString());
                        }
                    }
                    // 将字典中的字符串值全部保存下来
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    for(String k: hashMap.keySet()){
                        if(hashMap.get(k) instanceof String){
                            hashMap1.put(k, (String)hashMap.get(k));
                        }
                    }
                    ret.add(hashMap1);
                }


            }
        } catch (SQLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 获取结束时间
        long endTime1 = System.nanoTime();
        // 计算运行时间
        long duration1 = endTime1 - startTime1;

        // 转换为秒并打印
        double durationInSeconds1 = duration1 / 1_000_000_000.0;
        // System.out.println("数据处理时间：" + durationInSeconds1 + " 秒");
        return ret;
    }

}
