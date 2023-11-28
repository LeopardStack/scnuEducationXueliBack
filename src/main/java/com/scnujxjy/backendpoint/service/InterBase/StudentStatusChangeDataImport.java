package com.scnujxjy.backendpoint.service.InterBase;

import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@Slf4j
public class StudentStatusChangeDataImport {


    private MinioService minioService;

    public ExecutorService executorService;

    public BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    public CountDownLatch latch;
}
