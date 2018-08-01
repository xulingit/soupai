package info.meihua.task.service.task;

import common.util.date.DateUtils;
import common.util.lang.GsonUtils;
import info.meihua.task.service.client.ServiceCampaignClient;
import info.meihua.task.service.client.ServiceContactClient;
import info.meihua.task.service.client.ServiceRecipientClient;
import info.meihua.task.service.entity.Campaign;
import info.meihua.task.service.entity.CampaignLog;
import info.meihua.task.service.entity.CampaignStep;
import info.meihua.task.service.entity.Replacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author sunwell
 */
@Component
@SuppressWarnings({"SpringJavaAutowiringInspection", "unchecked"})
public class CampaignTask {
    private Logger logger = LoggerFactory.getLogger(CampaignTask.class);

    private ServiceCampaignClient campaignClient;
    private ServiceContactClient contactClient;
    private ServiceRecipientClient recipientClient;

    @Autowired
    public CampaignTask(ServiceCampaignClient campaignClient, ServiceRecipientClient recipientClient, ServiceContactClient contactClient) {
        this.campaignClient = campaignClient;
        this.contactClient = contactClient;
        this.recipientClient = recipientClient;
    }

    @Scheduled(cron = "8 0/1 * * * *")
    public void corn() throws Exception {
        logger.error(DateUtils.formatDate(new Date(), DateUtils.DATETIME_FORMAT) + "：开始将符合活动的联系人添加到收件人池子");
        List<Long>    allid =    new ArrayList<Long>();
        List<CampaignLog>     modify  = new ArrayList<CampaignLog>();
        List<CampaignLog>     delete  = new ArrayList<CampaignLog>();
        List<CampaignLog>     close  = new ArrayList<CampaignLog>();
        Map<String,List<CampaignLog>>    map = new HashMap<String,List<CampaignLog>>();
        List<Campaign> campaigns = campaignClient.listAllCampaigns();

        for (Campaign campaign : campaigns) {
            allid.add(campaign.getId());
            int status = campaignClient.getTaskStatus(campaign.getId());
            if (status == 1) {
                continue;
            }
            //清空集合
            modify.clear();
            delete.clear();
            close.clear();
            map.clear();
            //如果活动删除了，不需要添加联系人到活动
            List<CampaignLog> campaignLog = campaignClient.getCampaignLog(campaign.getUser_id(), campaign.getId());
            if (campaignLog.size() > 0) {
                for (CampaignLog c : campaignLog) {
                    if (c.getType() == CampaignLog.LOG_TYPE_CAMPAIGN_RECIPIENTS_TERM_MODIFY) {//修改了活动联系人
                        modify.add(c);
                    }
                    if (c.getType() == CampaignLog.LOG_TYPE_CAMPAIGN_DELETE || c.getType() == CampaignLog.LOG_TYPE_CAMPAIGN_CLOSE) {//删除了活动或者關閉了活動
                        delete.add(c);
                    }

                }
            }
            if (delete.size() > 0 && null != delete) {//包含，删除了活动或者關閉了，停止下面的步骤
                for(CampaignLog c : delete){
                    c.setStatus(CampaignLog.LOG_STATUS_FINISHED);
                    c.setGmtModified(System.currentTimeMillis());
                    campaignClient.updateCampaign_Log(c);
                }
                continue;
            }

            Long conditionId = campaign.getTarget_condition();
            if (conditionId != null) {
                Long campaignId = campaign.getId();
                if (campaign.getType() == 0) {
                    // 2018/1/17 判断执行时间 单次活动有定时时间
                    if (campaign.getGmt_action() != null && campaign.getGmt_action() > System.currentTimeMillis()) {
                        continue;
                    }
                }

                if(null != modify && modify.size() > 0){
                    map.put("campaignLog",modify);
                }
                contactClient.putToRecipients(conditionId, campaign.getAccount_id(), campaign.getUser_id(), campaignId,map);
                 //更新修改受众条件日志
                if(null != modify && modify.size() > 0){
                    for(CampaignLog c : modify){
                        c.setStatus(CampaignLog.LOG_STATUS_FINISHED);
                        c.setGmtModified(System.currentTimeMillis());
                        campaignClient.updateCampaign_Log(c);
                    }
                }
            }
        }
         //查询 所有的日志 条件 已经删除 或者关闭 查询的活动列表是处理完以后的 结果
        List<CampaignLog> campaignList = campaignClient.listAllCampaignLog();
        for(CampaignLog c : campaignList){
            if(!allid.contains(c.getCampaignId())){//不包含 id
                //更新该条日志状态
                campaignClient.updateCampaign_Log(c);
            }
        }
        logger.error(DateUtils.formatDate(new Date(), DateUtils.DATETIME_FORMAT) + "：结束将符合活动的联系人添加到收件人池子");
    }


    @Scheduled(cron = "10 0/1 * * * *")
    public void corn1() throws Exception {
        // TODO: 2018/1/9 多个邮箱的发送处理逻辑
        logger.error(DateUtils.formatDate(new Date(), DateUtils.DATETIME_FORMAT) + "：开始触发符合活动步骤条件的收件人");
        Set<Long> types =   new HashSet<Long>();
        List<Long>    allid =    new ArrayList<Long>();
        List<CampaignLog>     modify  = new ArrayList<CampaignLog>();
        List<CampaignLog>     delete  = new ArrayList<CampaignLog>();
        List<CampaignLog>     close  = new ArrayList<CampaignLog>();
        List<CampaignLog>     deleteCamp  = new ArrayList<CampaignLog>();
        Set<Long> modifyId  =   new HashSet<Long>();
        Map<String,List<CampaignLog>>   map =   new HashMap<String,List<CampaignLog>>();
        List<Campaign> campaigns = campaignClient.listAllCampaigns();
        for (Campaign campaign : campaigns) {
            allid.add(campaign.getId());
            if (campaign.getType() != null && campaign.getType() == 0) {
                // 2018/1/17 判断执行时间 单次活动有定时时间
                if (campaign.getGmt_action() != null && campaign.getGmt_action() > System.currentTimeMillis()) {
                    continue;
                }
            }
                 //清空集合
                 modify.clear();
                 delete.clear();
                 types.clear();
                 close.clear();
                 deleteCamp.clear();
                 modifyId.clear();
                 map.clear();
                 List<CampaignLog> campaignlog = campaignClient.getCampaignLog(campaign.getUser_id(),campaign.getId());
                 if(campaignlog.size() > 0){
                     for (CampaignLog c : campaignlog) {
                         Replacement replacement = (Replacement) GsonUtils.json2object(c.getReplacement(),Replacement.class);
                         Long stepid = 0L;
                         if( null != replacement.getStepId() && c.getType() == CampaignLog.LOG_TYPE_STEP_DEL){//步骤id
                             types.add(replacement.getStepId());//删除步骤的id
                         }
                         if((c.getType() == CampaignLog.LOG_TYPE_STEP_TIME_MODIFY  || c.getType() == CampaignLog.LOG_TYPE_STEP_CONDITION_MODIFY)  && null != replacement.getStepId()){
                             modifyId.add(replacement.getStepId());
                         }
                         if (c.getType() == CampaignLog.LOG_TYPE_STEP_TIME_MODIFY || c.getType() == CampaignLog.LOG_TYPE_STEP_CONDITION_MODIFY) {//修改了活动步骤条件
                             modify.add(c);
                         }
                         if (c.getType() == CampaignLog.LOG_TYPE_STEP_DEL) {//删除活动步骤
                             delete.add(c);
                         }
                         if(c.getType() == CampaignLog.LOG_TYPE_CAMPAIGN_DELETE || c.getType() == CampaignLog.LOG_TYPE_CAMPAIGN_CLOSE){//删除活动关闭了活动
                             deleteCamp.add(c);
                         }

                     }
                 }
                 if(deleteCamp.size() > 0 && null != deleteCamp){
                     for(CampaignLog c : deleteCamp){
                         c.setStatus(CampaignLog.LOG_STATUS_FINISHED);
                         c.setGmtModified(System.currentTimeMillis());
                         campaignClient.updateCampaign_Log(c);
                     }
                     continue;
                 }

                 List<CampaignStep> campaignSteps = campaignClient.listAllCampaignSteps(campaign.getId());
                 for (CampaignStep campaignStep : campaignSteps) {
                     map.clear();
                     //匹配步骤id是否  删除了该活动步骤 更新日志状态
                        if(types.size() > 0 && types.contains(campaignStep.getId())){
                            if (null != types && types.size() > 0 && null != delete && delete.size() > 0) {
                                for (CampaignLog c : delete) {
                                    Replacement replacement = (Replacement) GsonUtils.json2object(c.getReplacement(),Replacement.class);
                                    //遍历日志集合并更新
                                    if (replacement.getStepId().longValue() == campaignStep.getId().longValue()) {
                                        c.setStatus(CampaignLog.LOG_STATUS_FINISHED);
                                        c.setGmtModified(System.currentTimeMillis());
                                        campaignClient.updateCampaign_Log(c);
                                    }
                                }
                            }
                           continue;
                        }
                     if (modify.size() > 0 && null != modify && modifyId.size() > 0 && modifyId.contains(campaignStep.getId())) {//修改了步骤的等待时间或者步骤条件，重新获取条件和时间
                         //重新獲取步驟條件
                         campaignStep = campaignClient.getCampaignStepAgain(campaignStep.getId());
                         //更新campaignLog 等待时间或者步骤条件的日志
                         for (CampaignLog c : modify) {
                             Replacement replacement = (Replacement) GsonUtils.json2object(c.getReplacement(),Replacement.class);
                             if ( null != replacement.getStepId() && (c.getType() == CampaignLog.LOG_TYPE_STEP_TIME_MODIFY || c.getType() == CampaignLog.LOG_TYPE_STEP_CONDITION_MODIFY)) {
                                 if (replacement.getStepId().longValue() == campaignStep.getId().longValue()) {
                                     c.setStatus(CampaignLog.LOG_STATUS_FINISHED);
                                     c.setGmtModified(System.currentTimeMillis());
                                     campaignClient.updateCampaign_Log(c);
                                 }
                             }
                         }
                         map.put("modify",modify);
                     }
                     String stepCondition = campaignStep.getTarget_condition();
                     Integer order = campaignStep.getStep_order();
                     Long campaignId = campaign.getId();
                     List<String> taskIds = campaignClient.getCampaignStepsTasksByStepId(campaignStep.getId());
                     recipientClient.doCampaignStepTask(campaignId, order, stepCondition, taskIds,map);

                 }

                 if (campaign.getType() == null || campaign.getType() == 0) {
                     campaignClient.close(campaign.getId());
                 }
               //查询的结果是删除完以后的结果，完成需要更新的日志记录，-删除步骤
                 if (null != types && types.size() > 0 && null != delete && delete.size() > 0) {
                         for (CampaignLog c : delete) {
                             //遍历日志集合并更新
                             c.setStatus(CampaignLog.LOG_STATUS_FINISHED);
                             c.setGmtModified(System.currentTimeMillis());
                             campaignClient.updateCampaign_Log(c);
                         }
                 }
        }
        //查询 所有的日志 条件 已经删除 或者关闭 查询的活动列表是处理完以后的 结果
        List<CampaignLog> campaignList = campaignClient.listAllCampaignLog();
        for (CampaignLog c : campaignList) {
            if (!allid.contains(c.getCampaignId())) {
                c.setStatus(CampaignLog.LOG_STATUS_FINISHED);
                c.setGmtModified(System.currentTimeMillis());
                campaignClient.updateCampaign_Log(c);
            }
        }
        logger.error(DateUtils.formatDate(new Date(), DateUtils.DATETIME_FORMAT) + "：结束触发符合活动步骤条件的收件人");
    }

}
