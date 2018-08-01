package info.meihua.task.service.client;

import info.meihua.task.service.entity.CampaignLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


/**
 * @author sunwell 必须是RestController
 */
@FeignClient(name = "recipients-service", fallback = ServiceRecipientClient.ServiceRecipientClientFallback.class)
public interface ServiceRecipientClient {

    /**
     * 标记活动状态
     *
     * @param campaignId
     * @param order
     * @param condition
     * @param tasks
     */
    @PostMapping(value = {"/support/mark/{campaignId}/{order}"})
    void doCampaignStepTask(@PathVariable(value = "campaignId") Long campaignId, @PathVariable(value = "order") Integer order, @RequestParam(value = "condition", required = false) String condition, @RequestParam(value = "tasks") List<String> tasks, @RequestParam Map<String,List<CampaignLog>> map);


    @Component
    class ServiceRecipientClientFallback implements ServiceRecipientClient {
        private static final Logger logger = LoggerFactory.getLogger(ServiceRecipientClientFallback.class);

        @Override
        public void doCampaignStepTask(Long campaignId, Integer order, String condition, List<String> tasks,Map<String,List<CampaignLog>> map) {
            logger.error("执行活动步骤任务异常");
        }
    }
}