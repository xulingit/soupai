package info.meihua.campaign.service.controller;

import common.entity.domain.account.CountStatistics;
import common.util.date.DateUtils;
import info.meihua.campaign.service.entity.Campaign;
import info.meihua.campaign.service.entity.CampaignLog;
import info.meihua.campaign.service.entity.CampaignStep;
import info.meihua.campaign.service.entity.CampaignStepContents;
import info.meihua.campaign.service.service.ICampaignLogService;
import info.meihua.campaign.service.service.ICampaignService;
import info.meihua.campaign.service.service.ICampaignStepContentsService;
import info.meihua.campaign.service.service.ICampaignStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author sunwell
 */
@RequestMapping({"/support"})
@SuppressWarnings({"unchecked", "serial"})
@CrossOrigin
@RestController
public class SupportController {
    private ICampaignService campaignService;
    private ICampaignStepService campaignStepService;
    private ICampaignStepContentsService campaignStepContentsService;
    private ICampaignLogService campaignLogService;

    @Autowired
    public SupportController(ICampaignService campaignService, ICampaignStepContentsService campaignStepContentsService, ICampaignStepService campaignStepService, ICampaignLogService campaignLogService) {
        this.campaignService = campaignService;
        this.campaignStepService = campaignStepService;
        this.campaignStepContentsService = campaignStepContentsService;
        this.campaignLogService = campaignLogService;
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public List<Campaign> listAllCampaigns() {
        return campaignService.listAll();
    }

    @RequestMapping(value = {"/{id}/listAllCampaignSteps"}, method = {RequestMethod.GET})
    public List<CampaignStep> getCampaignStepsByCampaignId(@PathVariable(value = "id") Long id) {
        return campaignStepService.list(id);
    }


    @RequestMapping(value = "/listAllStatics", method = RequestMethod.GET)
    public List<Campaign> listAllStatics(@RequestParam(value = "accountId") Long accountId, @RequestParam(value = "gmtStart") Long gmtStart, @RequestParam(value = "gmtEnd") Long gmtEnd) {
        return campaignService.listAllStatics(accountId, gmtStart, gmtEnd);
    }

    @RequestMapping(value = {"/listForCrm"}, method = RequestMethod.GET)
    public Map<String, Object> listForCrm(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                          @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        return campaignService.listForCrm(page, limit);
    }

    @RequestMapping(value = {"/today"}, method = RequestMethod.GET)
    public Integer countTodayCampaign() {
        return campaignService.countTodayCampaign(DateUtils.initDateByDay(), DateUtils.addDay(DateUtils.initDateByDay(), 1));
    }

    @RequestMapping(value = "/campaign-chart", method = RequestMethod.GET)
    public List<CountStatistics> getCampaignStatistics(@RequestParam(value = "gmtStart", required = false) Long gmtStart,
                                                       @RequestParam(value = "gmtEnd", required = false) Long gmtEnd) {

        if (gmtStart == null) {
            gmtStart = DateUtils.addDay(DateUtils.initDateByDay(), -7).getTime();
        }
        if (gmtEnd == null) {
            gmtEnd = DateUtils.addDay(DateUtils.initDateByDay(), 1).getTime();
        }

        return campaignService.getCampaignStatistics(new Date(gmtStart), new Date(gmtEnd));
    }

    @RequestMapping(value = {"/{stepId}/tasks"}, method = {RequestMethod.GET})
    public List<String> getCampaignStepsTasksByStepId(@PathVariable("stepId") Long stepId) {
        List<Long> refIds = campaignStepContentsService.listRids(stepId);
        List<String> mailIds = new ArrayList<>();
        for (Long refId : refIds) {
            mailIds.add(refId.toString());
        }
        return mailIds;
    }

    @RequestMapping(value = {"/{id}/{order}/tasks"}, method = {RequestMethod.GET})
    public List<String> getLatestTasks(@PathVariable(value = "id") Long id, @PathVariable(value = "order") Integer order) {
        List<String> mailIds = new ArrayList<>();
        List<CampaignStep> campaignSteps = campaignStepService.list(id);
        for (CampaignStep campaignStep : campaignSteps) {
            if (campaignStep.getStep_order().equals(order)) {
                List<Long> refIds = campaignStepContentsService.listRids(campaignStep.getId());
                for (Long refId : refIds) {
                    mailIds.add(refId.toString());
                }
            }
        }
        return mailIds;
    }


    @RequestMapping(value = "/campaign/{id}/close", method = RequestMethod.GET)
    public void close(@PathVariable(value = "id") Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        campaignService.batchUpdateStatus(Campaign.STATUS_STOP, ids);
    }

    @RequestMapping(value = "/campaign/{id}/task-status", method = RequestMethod.GET)
    public int getTaskStatus(@PathVariable(value = "id") Long id) {
        return campaignService.getTaskStatus(id);
    }

    @RequestMapping(value = {"/mark/{id}/{status}"}, method = {RequestMethod.GET})
    public void markTaskStatus(@PathVariable(value = "id") Long id, @PathVariable(value = "status") Integer status) {
        campaignService.mark(id, status);
    }

    @RequestMapping(value = {"/updateStepCondition"}, method = {RequestMethod.GET})
    public void updateStepCondition(@RequestParam(value = "id") Long id,
                                    @RequestParam(value = "stepId") Long stepId,
                                    @RequestParam(value = "userId") Long userId,
                                    @RequestParam(value = "accountId") Long accountId,
                                    @RequestParam(value = "condition") String condition) {
        campaignStepService.modify(stepId, id, condition, userId, accountId);
    }

    @RequestMapping(value = {"/mark/{id}/{status}"}, method = {RequestMethod.POST})
    public void markCampaignTaskStatus(@PathVariable(value = "id") Long id, @PathVariable(value = "status") Integer status, @RequestParam(value = "statusStr") String statusStr) {
        campaignService.markCampaignTaskStatus(id, status, statusStr);
    }


    @RequestMapping(value = "/{campaignId}", method = {RequestMethod.GET})
    public Campaign getCampaignById(@PathVariable("campaignId") Long campaignId) {
        return campaignService.get(campaignId);
    }

    @RequestMapping(value = "/{campaignId}/step-num", method = {RequestMethod.GET})
    public int countCampaignStepsById(@PathVariable("campaignId") Long campaignId) {
        return campaignService.countCampaignStepsById(campaignId);
    }


    @RequestMapping(value = {"/{id}/steps"}, method = {RequestMethod.GET})
    public List<CampaignStep> listSteps(@PathVariable(value = "id") Long id, @RequestParam(value = "accountId") Long accountId) {
        return campaignService.listSteps(id, accountId);
    }

    /**
     * 活动当前邮件的下一步骤
     *
     * @param campaignId
     * @param mailId
     * @return
     */
    @RequestMapping(value = {"/next-step/{campaignId}/{mailId}"}, method = {RequestMethod.GET})
    public CampaignStep getNextStep(@PathVariable(value = "campaignId") Long campaignId,
                                    @PathVariable(value = "mailId") Long mailId) {
        try {
            CampaignStepContents stepContent = campaignStepContentsService.getStepByMailId(mailId);
            if (stepContent == null) {
                return null;
            }
            CampaignStep campaignStep = campaignStepService.getNextCampaignStep(campaignId, stepContent.getStep_id());
            return campaignStep;
        } catch (Exception e) {
            return null;
        }
    }


    @RequestMapping(value = "/campaigns/{accountId}", method = RequestMethod.GET)
    public List<Campaign> listCampaignsByAccount(@PathVariable(value = "accountId") Long accountId) {
        return campaignService.listAll(accountId);
    }
    /**
     * 获取campaignlog
     *
     * @param campaignId
     * @param mailId
     * @return
     */
    @RequestMapping(value = {"/campaign/{userId}/{campaignId}"})
   public List<CampaignLog> getCampaignLog(@PathVariable(value = "userId") Long userId,@PathVariable(value = "campaignId") Long campaignId) throws Exception {
        List<CampaignLog> campaignLogList = null;
        try {
            campaignLogList = campaignLogService.getCampaignLog(userId,campaignId);
        } catch (Exception e) {
          return null;
        }
        return campaignLogList;
   }
    /**
     * 更新日志状态
     *
     * @param campaignId
     * @param mailId
     * @return
     */
    @PostMapping(value = {"/mark/campaignlog"})
    public  void updateCampaignLog(@RequestParam Map<String,List<CampaignLog>> campaignlog) throws Exception {
        if(null != campaignlog.get("campaignLog")){
           List<CampaignLog> campaignLoglist =  campaignlog.get("campaignLog");
           campaignLogService.updateCampaignLog(campaignLoglist);
        }

    }
    /**
     * 插入日志
     *
     * @param campaignId
     * @param mailId
     * @return
     */
    @PostMapping(value = {"/campaign/campaignlog"})
    public  void insertCampaignLog(@RequestBody CampaignLog campaignLog) throws Exception {
        campaignLogService.insertCampaignLog(campaignLog);
    }
    /**
     * 获取活动步骤
     *
     * @param campaignStep
     * @param
     * @return
     */
    @PostMapping(value = {"/campaignStep/{id}"})
    public  CampaignStep getCampaignStepAgain(@PathVariable(value = "id") Long id){
        return campaignStepService.getCampaignStepById(id);
    }


    @PostMapping(value = {"/mark/campaign/campaignlog"})
   public  void updateCampaign_Log(@RequestBody  CampaignLog campaignlog){
        try {
          campaignLogService.updateCampaign_log(campaignlog);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 查询活动日志
     *
     *
     *
     * @param campaignLog
     */
    @PostMapping(value = {"/{scope}/{campaignId}/{type}"})
   public  List<CampaignLog> selectCampaignLog(@PathVariable(value = "scope") Integer scope, @PathVariable(value = "campaignId")Long campaignId, @PathVariable(value = "type") Integer type){
        List<CampaignLog> campaignlog =null;
        try {
            campaignlog =  campaignLogService.selectCampaignLog(scope,campaignId,type);
        } catch (Exception e) {
           return null;
        }
        return campaignlog;
    }

    @PostMapping(value = {"/mark/campaign/listAllcampaignlog"})
    public List<CampaignLog> listAllCampaignLog(){
        List<CampaignLog> campaignList = null;
        try {
            campaignList = campaignLogService.listAllCamapignLog();
        } catch (Exception e) {
           return null;
        }
        return campaignList;
    }

    /**
     * 活动当前邮件的下一步骤
     *
     * @param campaignId
     * @param mailId
     * @return
     */
    @RequestMapping(value = {"/current-step/{campaignId}/{mailId}"}, method = {RequestMethod.POST})
    public CampaignStep getCurrentStep(@PathVariable(value = "campaignId") Long campaignId,
                                    @PathVariable(value = "mailId") Long mailId) {
        try {
            CampaignStepContents stepContent = campaignStepContentsService.getStepByMailId(mailId);
            if (stepContent == null) {
                return null;
            }
            CampaignStep campaignStep =  campaignStepService.getCampaignStepById(stepContent.getStep_id());
            return campaignStep;
        } catch (Exception e) {
            return null;
        }
    }
}
