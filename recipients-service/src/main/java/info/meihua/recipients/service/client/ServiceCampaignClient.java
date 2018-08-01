package info.meihua.recipients.service.client;

import info.meihua.recipients.service.entity.CampaignLog;
import info.meihua.recipients.service.entity.extend.campaign.Campaign;
import info.meihua.recipients.service.entity.extend.campaign.CampaignStep;
import info.meihua.recipients.service.entity.extend.campaign.CampaignStepContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sunwell
 */
@FeignClient(name = "campaign-service", fallback = ServiceCampaignClient.ServiceCampaignClientFallback.class)
public interface ServiceCampaignClient {
    /**
     * 获取活动详情
     *
     * @param campaignId
     * @return
     */
    @GetMapping(value = "/support/{campaignId}")
    Campaign getCampaignById(@PathVariable("campaignId") Long campaignId);

    /**
     * 获取活动详情
     *
     * @param campaignId
     * @return
     */
    @GetMapping(value = "/support/{campaignId}/step-num")
    int countCampaignStepsById(@PathVariable("campaignId") Long campaignId);

    /**
     * 获取所有步骤
     *
     * @param id
     * @param accountId
     * @return
     */
    @GetMapping(value = "/support/{id}/steps")
    List<CampaignStep> listSteps(@PathVariable(value = "id") Long id, @RequestParam(value = "accountId") Long accountId);

    /**
     * 获取步骤taskId
     *
     * @param id
     * @param order
     * @return
     */
    @GetMapping(value = "/support/{id}/{order}/tasks")
    List<String> getLatestTasks(@PathVariable(value = "id") Long id, @PathVariable(value = "order") Integer order);

    /**
     * 标记活动执行任务状态
     *
     * @param id
     * @param status
     * @param statusStr
     */
    @PostMapping(value = {"/support/mark/{id}/{status}"})
    void markCampaignTaskStatus(@PathVariable(value = "id") Long id, @PathVariable(value = "status") Integer status, @RequestParam(value = "statusStr") String statusStr);

    /**
     * 关闭活动
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/support/campaign/{id}/close")
    void close(@PathVariable(value = "id") Long id);

    /**
     * 插入一条有影响的日志
     *
     *
     *
     * @param campaignLog
     */
    @PostMapping(value = {"/support/campaign/campaignlog"})
    void insertCampaignLog(@RequestBody CampaignLog campaignLog);
    /**
     * 查询活动日志
     *
     *
     *
     * @param campaignLog
     */
    @PostMapping(value = {"/support/{scope}/{campaignId}/{type}"})
    List<CampaignLog> selectCampaignLog(@PathVariable(value = "scope") Integer scope, @PathVariable(value = "campaignId")Long campaignId, @PathVariable(value = "type") Integer type);
    /**
     * 获取活动步骤
     *
     * @param campaignStep
     * @param
     * @return
     */
    @PostMapping(value = {"/support/campaignStep/{id}"})
     CampaignStep getCampaignStepAgain(@PathVariable(value = "id") Long id);

    /**
     * 活动当前邮件的步骤
     *
     * @param campaignId
     * @param mailId
     * @return
     */
    @RequestMapping(value = {"/support/current-step/{campaignId}/{mailId}"}, method = {RequestMethod.POST})
     CampaignStep getCurrentStep(@PathVariable(value = "campaignId") Long campaignId,
                                       @PathVariable(value = "mailId") Long mailId);

    @Component
    class ServiceCampaignClientFallback implements ServiceCampaignClient {
        private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCampaignClientFallback.class);

        @Override
        public Campaign getCampaignById(Long campaignId) {
            LOGGER.error("获取活动数据异常");
            return null;
        }

        @Override
        public int countCampaignStepsById(Long campaignId) {
            LOGGER.error("获取活动步骤数据异常");
            return 0;
        }

        @Override
        public List<CampaignStep> listSteps(Long id, Long accountId) {
            LOGGER.error("获取活动步骤详情数据异常");
            return null;
        }

        @Override
        public List<String> getLatestTasks(Long id, Integer order) {
            return new ArrayList<>();
        }

        @Override
        public void markCampaignTaskStatus(Long id, Integer status, String statusStr) {

        }

        @Override
        public void close(Long id) {

        }

        public void insertCampaignLog(CampaignLog campaignLog) {

        }

        @Override
        public List<CampaignLog> selectCampaignLog(Integer scope, Long campaignId, Integer type) {
            return null;
        }

        @Override
        public CampaignStep getCampaignStepAgain(Long id) {
            return null;
        }

        @Override
        public CampaignStep getCurrentStep(Long campaignId, Long mailId) {
            return null;
        }


    }
}
