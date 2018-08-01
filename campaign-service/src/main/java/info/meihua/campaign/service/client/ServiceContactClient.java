package info.meihua.campaign.service.client;


import info.meihua.campaign.service.entity.CampaignLog;
import info.meihua.campaign.service.entity.extend.contact.ContactCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author sunwell
 */
@FeignClient(name = "contact-service", fallback = ServiceContactClient.ServiceContactClientFallback.class)
public interface ServiceContactClient {

    @PostMapping(value = {"/support/select/conditionId"})
    ContactCondition selectConditionById(@RequestParam(value = "conditionId") Long conditionId);

    @PostMapping(value = {"/support/clone/conditionId"})
    Long cloneConditionById(@RequestParam(value="conditionId") Long conditionId);


    /**
     * 新增筛选条件
     *
     * @param accountId
     * @param userId
     * @param name
     * @param conditionStr
     * @return
     */
    @PostMapping(value = {"/support/condition"})
    Long addCondition(@RequestParam(value = "accountId") Long accountId,
                      @RequestParam(value = "userId") Long userId,
                      @RequestParam(value = "name", required = false) String name,
                      @RequestParam(value = "conditionStr") String conditionStr);

    /**
     * 编辑筛选条件
     *
     * @param accountId
     * @param id
     * @param conditionStr
     */
    @PostMapping(value = {"/support/condition/{id}"})
    void updateCondition(@RequestParam(value = "accountId") Long accountId,
                         @PathVariable(value = "id") Long id,
                         @RequestParam(value = "conditionStr") String conditionStr);

    /**
     * 添加联系人至收件人系统
     *
     * @param conditionId
     * @param accountId
     * @param userId
     * @param campaignId
     */
    @GetMapping(value = "/support/recipient/put")
    void putToRecipients(@RequestParam(value = "conditionId") Long conditionId,
                         @RequestParam(value = "accountId") Long accountId,
                         @RequestParam(value = "userId") Long userId,
                         @RequestParam(value = "campaignId") Long campaignId,@RequestParam Map<String,List<CampaignLog>> campaignLog);

    @Component
    class ServiceContactClientFallback implements ServiceContactClient {
        private static final Logger log = LoggerFactory.getLogger(ServiceContactClientFallback.class);

        @Override
        public ContactCondition selectConditionById(Long conditionId) {
            log.error("异常发生，selectConditionById fallback方法");
            return null;
        }

        @Override
        public Long cloneConditionById(Long conditionId) {
            log.error("异常发生，cloneConditionById fallback方法");
            return null;
        }

        @Override
        public Long addCondition(Long accountId, Long userId, String name, String conditionStr) {
            log.error("异常发生，addCondition fallback方法");
            return null;
        }

        @Override
        public void updateCondition(Long accountId, Long id, String conditionStr) {
            log.error("异常发生，进入 updateCondition fallback方法");
        }

        @Override
        public void putToRecipients(Long conditionId, Long accountId, Long userId, Long campaignId, Map<String,List<CampaignLog>> campaignLog) {
            log.error("异常发生，进入putToRecipients方法");

        }
    }
}
