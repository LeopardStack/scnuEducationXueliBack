//package transactionTest;
//
//import com.scnujxjy.backendpoint.BackEndpointApplication;
//import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
//import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//
//import static com.scnujxjy.backendpoint.config.AsyncConfig.getIOTaskExecutor;
//
//@SpringBootTest(classes = BackEndpointApplication.class)
//@Slf4j
//public class Test1 {
//
//    @Resource
//    private PlatformMessageService platformMessageService;
//    @Transactional
//    public void testInsert() {
//        try {
//            PlatformMessagePO platformMessagePO = new PlatformMessagePO()
//                    .setUserId("1")
//                    .setMessageType("测试消息");
//            baseMapper.insert(platformMessagePO);
//
//            // 模拟异常以触发事务回滚
//            if (true) {
//                throw new RuntimeException("模拟异常，测试事务回滚");
//            }
//        } catch (Exception e) {
//            log.error("插入平台消息时发生异常，事务回滚。", e);
//            throw e;
//        }
//    }
//    @Resource
//    @Lazy
//    private Test1 test1;
//
//    @Test
//    public  void test1(){
//        System.out.println("fasdfsdafasf ");
//    }
//
//    @Test
//    public void testMainThread() {
//        try {
//            platformMessageService.testInsert();
//        } catch (Exception e) {
//            log.error("主线程测试事务回滚成功。", e);
//        }
//    }
//
//
//}
