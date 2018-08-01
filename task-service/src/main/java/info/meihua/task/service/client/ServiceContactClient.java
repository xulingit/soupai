package info.meihua.task.service.client;

import info.meihua.task.service.entity.CampaignLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author sunwell 必须是RestController
 */
@FeignClient(name = "contact-service", fallback = ServiceContactClient.ServiceContactClientFallback.class)
public interface ServiceContactClient {

    /**
     * 活动定时任务
     */
    @GetMapping(value = {"/support/code-add/task"})
    void codeAddTask();

    /**
     * 同步金数据
     */
    @GetMapping(value = {"/jsj"})
    void syncData();

    /**
     * 添加联系人至收件人系统
     *  @param conditionId
     * @param accountId
     * @param userId
     * @param campaignId
     * @param campaignlog
     */
    @GetMapping(value = "/support/recipient/put")
    void putToRecipients(@RequestParam(value = "conditionId") Long conditionId,
                         @RequestParam(value = "accountId") Long accountId,
                         @RequestParam(value = "userId") Long userId,
                         @RequestParam(value = "campaignId") Long campaignId,@RequestParam Map<String,List<CampaignLog>> campaignLog);

    @Component
    class ServiceContactClientFallback implements ServiceContactClient {
        private static final Logger logger = LoggerFactory.getLogger(ServiceContactClientFallback.class);

        @Override
        public void codeAddTask() {
            logger.error("异常发生，脚本同步服务");
        }

        @Override
        public void syncData() {
            logger.error("异常发生，金数据同步服务");

        }

        @Override
        public void putToRecipients(Long conditionId, Long accountId, Long userId, Long campaignId, Map<String,List<CampaignLog>> campaignLog) {
            logger.error("异常发生，客户添加到收件人池子异常");

        }
    }
}