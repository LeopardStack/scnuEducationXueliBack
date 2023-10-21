package com.scnujxjy.backendpoint.mapperTest;

import com.scnujxjy.backendpoint.dao.entity.basic.RolePermissionPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PermissionMapper;
import com.scnujxjy.backendpoint.service.basic.RolePermissionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class PermissionTest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Pair{
        private long userId;
        private long permissionId;
    }

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RolePermissionService rolePermissionService;

    public List<Pair> readFromFile(String classPath) {
        List<Pair> pairs = new ArrayList<>();
        try {
            // 通过ClassLoader读取resources下的文件
            ClassPathResource resource = new ClassPathResource(classPath);
            InputStream inputStream = resource.getInputStream();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        long x = Long.parseLong(parts[0].trim());
                        long y = Long.parseLong(parts[1].trim());
                        pairs.add(new Pair(x, y));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pairs;
    }

    @Test
    public void test1(){
        String filePath = "/data/二级学院管理员权限.txt";
        List<Pair> pairs = readFromFile(filePath);
        List<RolePermissionPO> rolePermissionPOList = new ArrayList<>();
        for(Pair pair: pairs){
            RolePermissionPO rolePermissionPO = new RolePermissionPO(pair.getUserId(), pair.getPermissionId());
            rolePermissionPOList.add(rolePermissionPO);
        }


        log.info("\n"+pairs);
        rolePermissionService.saveBatch(rolePermissionPOList);
    }
}
