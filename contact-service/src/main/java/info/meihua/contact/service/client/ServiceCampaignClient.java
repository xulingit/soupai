package info.meihua.contact.service.client;

import info.meihua.contact.service.entity.CampaignLog;
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
     * 标记活动执行任务状态
     *
     * @param id
     * @param status
     */
    @GetMapping(value = {"/support/mark/{id}/{status}"})
    void markTaskStatus(@PathVariable(value = "id") Long id, @PathVariable(value = "status") Integer status);

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
     * 更改campaignlog的日志状态
     *
     *
     *
     * @param campaignloglist
     */
    @PostMapping(value = {"/support/mark/campaignlog"})
    void updateCampaignLog(@RequestParam Map<String,List<CampaignLog>> campaignlog);
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
     * 更改campaignlog单个的日志状态
     *
     *
     *
     * @param campaignloglist
     */
    @PostMapping(value = {"/support/mark/campaign/campaignlog"})
    void updateCampaign_Log(@RequestBody  CampaignLog campaignlog);

    @Component
    class ServiceCampaignClientFallback implements ServiceCampaignClient {
        private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCampaignClientFallback.class);

        @Override
        public void markTaskStatus(Long id, Integer status) {
            LOGGER.info("异常发生，标记活动异常");
        }

        @Override
        public void markCampaignTaskStatus(Long id, Integer status, String statusStr) {

        }

        public void updateCampaignLog(Map<String,List<CampaignLog>> campaignlog) {

        }

        public void insertCampaignLog(CampaignLog campaignLog) {

        }

        @Override
        public List<CampaignLog> selectCampaignLog(Integer scope, Long campaignId, Integer type) {
            return null;
        }

        @Override
        public void updateCampaign_Log(CampaignLog campaignlog) {

        }

        public CampaignLog getCampaignLog(Long userId, Long campaignId, Integer type) {
            return null;
        }
    }
}