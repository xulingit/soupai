package info.meihua.task.service.client;

import info.meihua.task.service.entity.Campaign;
import info.meihua.task.service.entity.CampaignLog;
import info.meihua.task.service.entity.CampaignStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author sunwell 必须是RestController
 */
@FeignClient(name = "campaign-service", fallback = ServiceCampaignClient.ServiceCampaignClientFallback.class)
public interface ServiceCampaignClient {

    /**
     * 获取所有活动
     *
     * @return
     */
    @GetMapping(value = "/support/listAll")
    List<Campaign> listAllCampaigns();

    /**
     * 后去所有步骤
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/support/{id}/listAllCampaignSteps")
    List<CampaignStep> listAllCampaignSteps(@PathVariable(value = "id") Long id);

    /**
     *
     *获取步骤所有task
     * @param stepId
     * @return
     */
    @GetMapping(value = "/support/{stepId}/tasks")
    List<String> getCampaignStepsTasksByStepId(@PathVariable("stepId") Long stepId);

    /**
     * 获取状态
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/support/campaign/{id}/task-status")
    int getTaskStatus(@PathVariable(value = "id") Long id);

    /**
     * 关闭活动
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/support/campaign/{id}/close")
    void close(@PathVariable(value = "id") Long id);


    /**
     * 标记活动状态
     *
     * @param id
     * @param status
     */
    @GetMapping(value = {"/support/mark/{id}/{status}"})
    void markTaskStatus(@PathVariable(value = "id") Long id, @PathVariable(value = "status") Integer status);

    /**
     * 获取campaignlog
     *
     * @param userId
     * @param CampaignId
     * @param type
     */
    @PostMapping(value = {"/support/campaign/{userId}/{campaignId}"})
    List<CampaignLog> getCampaignLog(@PathVariable(value = "userId") Long userId, @PathVariable(value = "campaignId") Long campaignId);
    /**
     * 获取活动步骤根据步骤id
     *
     * @param id
     * @param
     * @param
     */
    @PostMapping(value = {"/support/campaignStep/{id}"})
    CampaignStep getCampaignStepAgain(@PathVariable(value = "id") Long id);

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
     * 更改campaignlog的日志状态
     *
     *
     *
     * @param campaignloglist
     */
    @PostMapping(value = {"/support/mark/campaignlog"})
    void updateCampaignLog(@RequestParam Map<String,List<CampaignLog>> campaignlog);
    /**
     * 更改campaignlog单个的日志状态
     *
     *
     *
     * @param campaignloglist
     */
    @PostMapping(value = {"/support/mark/campaign/campaignlog"})
    void updateCampaign_Log(@RequestBody  CampaignLog campaignlog);

    @PostMapping(value = {"/support/mark/campaign/listAllcampaignlog"})
    List<CampaignLog> listAllCampaignLog();

    @Component
    class ServiceCampaignClientFallback implements ServiceCampaignClient {
        private static final Logger logger = LoggerFactory.getLogger(ServiceCampaignClientFallback.class);

        @Override
        public void markTaskStatus(Long id, Integer status) {
            logger.error("异常发生，标记活动任务状态异常");
        }
        @Override
        public List<CampaignLog> getCampaignLog(Long userId, Long campaignId) {
            return null;
        }
        @Override
        public  CampaignStep getCampaignStepAgain(Long id){
            return null;
        }

        public void insertCampaignLog(CampaignLog campaignLog) {

        }

        public void updateCampaignLog(Map<String,List<CampaignLog>> campaignlog) {

        }

        public void updateCampaign_Log(CampaignLog campaignlog) {

        }

        @Override
        public List<CampaignLog> listAllCampaignLog() {
            return null;
        }

        @Override
        public List<Campaign> listAllCampaigns() {
            logger.error("异常发生，获取活动列表异常");
            return null;
        }

        @Override
        public List<CampaignStep> listAllCampaignSteps(Long id) {
            logger.error("异常发生，获取活动步骤列表异常");
            return null;
        }

        @Override
        public List<String> getCampaignStepsTasksByStepId(Long stepId) {
            logger.error("异常发生，获取活动步骤邮件列表异常");
            return null;
        }

        @Override
        public int getTaskStatus(Long id) {
            logger.error("异常发生，获取活动任务状态异常");
            return 2;
        }

        @Override
        public void close(Long id) {
            logger.error("异常发生，关闭活动异常");
        }
    }
}