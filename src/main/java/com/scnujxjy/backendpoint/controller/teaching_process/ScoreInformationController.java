package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.service.teaching_process.ScoreInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * <p>
 * 成绩信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-09-10
 */
@RestController
@RequestMapping("/score-information")
public class ScoreInformationController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private ScoreInformationService scoreInformationService;


//    @GetMapping("/getGradeInfo")
//    public SaResult getGradeInfo() {
//        String loginId = (String) StpUtil.getLoginId();
//        // 参数校验
//        if (Objects.isNull(loginId) || loginId.trim().length() == 0) {
//            throw dataMissError();
//        }
//        // 查询数据
//        List<ScoreInformationPO> scoreInformationPOS = scoreInformationService.getBaseMapper().getGradeInfo(loginId);
//        if (Objects.isNull(scoreInformationPOS)) {
//            throw dataNotFoundError();
//        }
//        return SaResult.data(scoreInformationPOS);
//    }

    /**
     * 学生获取自己的成绩信息
     * @return
     */
    @GetMapping("/getGradeInfo")
    public SaResult getGradeInfo() {
        String loginId = (String) StpUtil.getLoginId();
        // 参数校验
        if (Objects.isNull(loginId) || loginId.trim().length() == 0) {
            throw dataMissError();
        }

        // 尝试从 Redis 中获取数据
        List<ScoreInformationPO> scoreInformationPOS = (List<ScoreInformationPO>) redisTemplate.opsForValue().get("score:" + loginId);

        // 如果 Redis 中没有数据
        if (Objects.isNull(scoreInformationPOS)) {
            // 从数据库中查询数据
            scoreInformationPOS = scoreInformationService.getBaseMapper().getGradeInfo(loginId);
            if (Objects.isNull(scoreInformationPOS)) {
                throw dataNotFoundError();
            }
            // 将查询结果存入 Redis，设置过期时间为1小时（可以根据实际需求调整）
            redisTemplate.opsForValue().set("score:" + loginId, scoreInformationPOS, 100, TimeUnit.HOURS);
        }

        return SaResult.data(scoreInformationPOS);
    }

}

