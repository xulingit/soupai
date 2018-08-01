package info.meihua.contact.service.client;

import info.meihua.contact.service.entity.extend.Recipients.RecipientsCampaign;
import info.meihua.contact.service.entity.extend.Recipients.RecipientsContactTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunwell 必须是RestController
 */
@FeignClient(name = "recipients-service", fallback = ServiceRecipientsClient.ServiceRecipientsClientFallback.class)
public interface ServiceRecipientsClient {

    /**
     * 获取活动联系人ids
     *
     * @param campaignId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/support/{campaignId}/contacts")
    List<Long> getCampaignContactIds(@PathVariable(value = "campaignId") Long campaignId);

    /**
     * 添加联系人到活动
     *
     * @param accountId
     * @param campaignId
     * @param recipients
     * @return
     */
    @PostMapping(value = "/support/{campaignId}/put")
    void putContactsToCampaign(@RequestParam("accountId") Long accountId,
                               @PathVariable("campaignId") Long campaignId,
                               @RequestParam("recipients") String recipients);

    /**
     * 联系人活动
     *
     * @param contactId
     * @return
     */
    @GetMapping(value = "/support/{contactId}/campaigns")
    List<RecipientsCampaign> listCampaigns(@PathVariable(value = "contactId") Long contactId);

    /**
     * 联系人task
     *
     * @param contactId
     * @return
     */
    @GetMapping(value = "/support/{contactId}/tasks")
    List<RecipientsContactTask> listTasks(@PathVariable(value = "contactId") Long contactId);

    /**
     * 联系人标记
     *
     * @param contactId
     * @return
     */
    @GetMapping(value = "/support/{contactId}/marks")
    Map<String, Boolean> detailMarks(@PathVariable("contactId") Long contactId);

    @Component
    class ServiceRecipientsClientFallback implements ServiceRecipientsClient {
        private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRecipientsClientFallback.class);

        @Override
        public List<Long> getCampaignContactIds(Long campaignId) {
            LOGGER.info("异常发生，获取活动联系人ids异常");
            return new ArrayList<>();
        }

        @Override
        public void putContactsToCampaign(Long accountId, Long campaignId, String recipients) {
            LOGGER.info("异常发生，添加联系人至活动异常");
        }

        @Override
        public List<RecipientsCampaign> listCampaigns(Long contactId) {
            LOGGER.info("异常发生，获取联系人活动异常");
            return new ArrayList<>();
        }

        @Override
        public List<RecipientsContactTask> listTasks(Long contactId) {
            LOGGER.info("异常发生，获取联系人邮件异常");
            return new ArrayList<>();
        }

        @Override
        public Map<String, Boolean> detailMarks(Long contactId) {
            LOGGER.info("异常发生，detailMarks异常");
            Map<String, Boolean> res = new HashMap<>();
            res.put("Touches", false);
            res.put("Opened", false);
            res.put("Clicked", false);
            res.put("Replied", false);
            res.put("Unsubscribe", false);
            res.put("HardBullet", false);
            res.put("SoftBullet", false);
            res.put("Completed", false);
            return res;
        }
    }
}