package info.meihua.recipients.service.controller;

import com.google.gson.reflect.TypeToken;
import common.util.date.DateUtils;
import common.util.lang.GsonUtils;
import common.util.pay.httpClient.HttpRequest;
import info.meihua.recipients.service.entity.*;
import info.meihua.recipients.service.entity.extend.campaign.RecipientsCampaign;
import info.meihua.recipients.service.entity.extend.campaign.RecipientsQuery;
import info.meihua.recipients.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author sunwell
 */
@RequestMapping({"/support"})
@CrossOrigin
@RestController
@SuppressWarnings({"unchecked", "serial"})
public class SupportController {
    private final IRecipientsTaskService recipientsTaskService;
    private final IRecipientsService recipientsService;
    private final IRecipientsTaskLinkService recipientsTaskLinkService;


    @Autowired
    public SupportController(IRecipientsTaskService recipientsTaskService, IRecipientsTaskLinkService recipientsTaskLinkService, IRecipientsService recipientsService) {
        this.recipientsTaskService = recipientsTaskService;
        this.recipientsService = recipientsService;
        this.recipientsTaskLinkService = recipientsTaskLinkService;
    }


    /**********CAMPAIGN USE  ***/


    @RequestMapping(value = {"/{campaignId}/overview"}, method = {RequestMethod.GET})
    public Map<String, Object> getCampaignOverview(@PathVariable("campaignId") Long campaignId, @RequestParam("accountId") Long accountId) {

        try {
            return recipientsService.getCampaignOverview(campaignId, accountId);
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = {"/{campaignId}/overview/statics"}, method = {RequestMethod.GET})
    public Map<String, Object> getCampaignOverviewStatics(@PathVariable("campaignId") Long campaignId, @RequestParam("accountId") Long accountId, @RequestParam(value = "gmtStart") Long gmtStart, @RequestParam(value = "gmtEnd") Long gmtEnd) {
        try {
            return recipientsService.getCampaignOverviewStatics(campaignId, accountId, gmtStart, gmtEnd);
        } catch (Exception e) {
            return null;
        }
    }

    /****CONTACT USE***/

    @RequestMapping(value = "/{campaignId}/contacts", method = {RequestMethod.GET})
    public List<Long> getCampaignContactIds(@PathVariable(value = "campaignId") Long campaignId) {
        try {
            return recipientsService.listContactIdsByCampaignId(campaignId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @RequestMapping(value = "/{campaignId}/put", method = {RequestMethod.POST})
    public void putContactsToCampaign(@RequestParam("accountId") Long accountId,
                                      @PathVariable("campaignId") Long campaignId,
                                      @RequestParam("recipients") String recipients) {
        List<RecipientsContact> recipientsContacts = (List<RecipientsContact>) GsonUtils.json2list(recipients,
                new TypeToken<List<RecipientsContact>>() {
                }.getType());
        try {
            recipientsService.insert(accountId, campaignId, recipientsContacts);
        } catch (Exception ignored) {
        }
    }

    @RequestMapping(value = "/{contactId}/campaigns", method = {RequestMethod.GET})
    public List<RecipientsCampaign> listCampaigns(@PathVariable(value = "contactId") Long contactId) {
        try {
            return recipientsService.listCampaignsByContactId(contactId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @RequestMapping(value = "/{contactId}/tasks", method = {RequestMethod.GET})
    public List<RecipientsContactTask> listTasks(@PathVariable(value = "contactId") Long contactId) {
        try {
            return recipientsService.listTasksByContactId(contactId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @RequestMapping(value = "/{contactId}/marks", method = {RequestMethod.GET})
    public Map<String, Boolean> detailMarks(@PathVariable("contactId") Long contactId) {
        try {
            return recipientsTaskService.contactMarks(contactId);
        } catch (Exception e) {
            return null;
        }
    }


    /**********TASK USE ***/
    @RequestMapping(value = "/mark/{campaignId}/{order}", method = {RequestMethod.POST})
    public void doCampaignStepTask(@PathVariable(value = "campaignId") Long campaignId,
                                   @PathVariable(value = "order") Integer order,
                                   @RequestParam(value = "condition", required = false) String condition,
                                   @RequestParam(value = "tasks") List<String> tasks,@RequestParam Map<String,List<CampaignLog>> map) throws Exception {


        Long gmtAction = System.currentTimeMillis();

        Integer type = 0;
        List<String> latestTasks = new ArrayList<>();
        List<Long> clickUrl = new ArrayList<>();

//        if (order > 1) {
        try {
            RecipientsQuery recipientsQuery = (RecipientsQuery) GsonUtils.json2object(condition, RecipientsQuery.class);


            String[] delay = recipientsQuery.getDelaytime().split(":");

            Date actionDate = new Date();

            actionDate = DateUtils.addDay(actionDate, -Integer.valueOf(delay[0]));
            actionDate = DateUtils.addHour(actionDate, -Integer.valueOf(delay[1]));
            actionDate = DateUtils.addMinute(actionDate, -Integer.valueOf(delay[2]));
            gmtAction = actionDate.getTime();


            type = Integer.valueOf(recipientsQuery.getType());

            if (order > 1) {
                String[] lIds = recipientsQuery.getTask_id().split(",");
                for (String item : lIds) {
                    if (!Objects.equals(item, "")) {
                        latestTasks.add(item);
                    }
                }
            }

            if (type > 1) {
                if (!Objects.equals(recipientsQuery.getClick_url(), "")) {
                    String[] clicks = recipientsQuery.getClick_url().split(",");
                    for (String s : clicks) {
                        if (!Objects.equals(s, "")) {
                            clickUrl.add(Long.valueOf(s.split("#")[1]));
                        }
                    }
                }
            }
        } catch (Exception e) {
        }


//        }


        recipientsService.doCampaignStepTask(campaignId, order, tasks, gmtAction, type, latestTasks, clickUrl,map);
    }


    /**********EDM USE ***/

    @RequestMapping(value = "/{taskId}/{recipientId}/{taskLinkId}/{status}", method = {RequestMethod.POST})
    public Boolean addRecipientShortLink(@PathVariable(value = "taskId") Long taskId,
                                         @PathVariable(value = "recipientId") Long recipientId,
                                         @PathVariable(value = "taskLinkId") Long taskLinkId,
                                         @RequestParam(value = "shortLink") String shortLink,
                                         @PathVariable(value = "status") Integer status,
                                         @RequestParam(value = "campaignId") Long campaignId) {
        recipientsTaskLinkService.insert(taskId, recipientId, taskLinkId, shortLink, status, campaignId);
        return true;
    }

    @RequestMapping(value = "/{campaignId}/{taskId}", method = {RequestMethod.GET})
    public List<Recipients> listRecipients(@PathVariable(value = "campaignId") Long campaignId,
                                           @PathVariable(value = "taskId") Long taskId) {
        try {
            return recipientsService.listRecipientsByCampaignIdTaskId(campaignId, taskId);
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "/{campaignId}/{taskId}/lockStatus", method = {RequestMethod.POST})
    public void lockStatus(@PathVariable(value = "campaignId") Long campaignId,
                           @PathVariable(value = "taskId") Long taskId,
                           @RequestParam(value = "rIds") List<Long> rIds) {
        try {
            recipientsService.updateLockedStatus(campaignId, taskId, rIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/{campaignId}/{taskId}/list", method = {RequestMethod.GET})
    public List<Recipients> listPagerRecipients(@PathVariable(value = "campaignId") Long campaignId,
                                                @PathVariable(value = "taskId") Long taskId,
                                                @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                @RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit) {
        try {
            return recipientsService.listPagerRecipientsByCampaignIdTaskId1(campaignId, taskId, page, limit);
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "/{campaignId}/{taskId}/count", method = {RequestMethod.GET})
    public Integer countPagerRecipients(@PathVariable(value = "campaignId") Long campaignId,
                                        @PathVariable(value = "taskId") Long taskId) {
        try {
            return recipientsService.countPagerRecipientsByCampaignIdTaskId1(campaignId, taskId);
        } catch (Exception e) {
            return null;
        }
    }


    @RequestMapping(value = "/recipient/{recipientId}", method = {RequestMethod.GET})
    public Recipients getRecipient(@PathVariable("recipientId") Long recipientId) {
        return recipientsService.get(recipientId);
    }


    /**
     * 回传数据处理
     */
    @RequestMapping(value = "/{campaignId}/{taskId}/{status}/pass-back", method = {RequestMethod.POST})
    public void passBack(@PathVariable(value = "campaignId") Long campaignId,
                         @PathVariable(value = "taskId") Long taskId,
                         @RequestParam(value = "rIds", required = false) List<Long> rIds,//真实已发送收件人id
                         @PathVariable(value = "status") Integer status,
                         @RequestParam(value = "subject", required = false) String subject,
                         @RequestParam(value = "summary", required = false) String summary,
                         @RequestParam(value = "content", required = false) String content,
                         @RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "untouchedReason", required = false) Integer untouchedReason,
                         @RequestParam(value = "untouchedReasonDes", required = false) String untouchedReasonDes,
                         @RequestParam(value = "supplierType", required = false) Integer supplierType) {
        recipientsTaskService.passBack(campaignId, taskId, rIds, status, subject, summary, content, email, untouchedReason, untouchedReasonDes, supplierType);
    }

    @RequestMapping(value = {"/{campaignId}/operationPassBack"}, method = {RequestMethod.POST})
    public void operationPassBack(@PathVariable(value = "campaignId") Long campaignId,
                                  @RequestParam(value = "responseType") String responseType,
                                  @RequestParam(value = "email") String email,
                                  @RequestParam(value = "taskId", required = false) Long taskId,
                                  @RequestParam(value = "recipientId", required = false) Long recipientId,
                                  @RequestParam(value = "url", required = false) String url,
                                  @RequestParam(value = "taskLinkId", required = false) Long taskLinkId,
                                  @RequestParam(value = "replyClassification", required = false) Integer replyClassification,
                                  @RequestParam(value = "replyCode", required = false) String replyCode,
                                  @RequestParam(value = "message", required = false) String message,
                                  @RequestParam(value = "subject", required = false) String subject,
                                  @RequestParam(value = "read", required = false, defaultValue = "0") Integer read) {
        try {
            recipientsService.operationPassBack(campaignId, responseType, email, taskId, recipientId, url, taskLinkId, replyClassification, replyCode, message, subject, read);
        } catch (Exception ignored) {
        }
    }

    /**
     * 被限制收件人重新发送
     *
     * @return
     */
    @RequestMapping(value = "/recipients/reissue", method = {RequestMethod.GET})
    public List<Long> reissueRecipients() {
        List<Long> taskIds = new ArrayList<>();
        try {
            List<Map<String, Object>> unSentEmailTaskIds = recipientsTaskService.getUnSentEmailRecipients();
            if (unSentEmailTaskIds == null || unSentEmailTaskIds.size() <= 0) {
                return null;
            }
            for (Map<String, Object> map : unSentEmailTaskIds) {
                Long taskId = (Long) map.get("task_id");
                taskIds.add(taskId);
            }
            if (taskIds.size() > 0) {
                recipientsTaskService.setUnSentEmailRecipientsReadyToSent(taskIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskIds;
    }

    /**
     * 获取活动点击数量
     *
     * @param campaignId
     * @return
     */
    @RequestMapping(value = "/clickCount/{campaignId}", method = {RequestMethod.GET})
    public Integer getCampaignClickCount(@PathVariable("campaignId") Long campaignId) {
        return recipientsTaskLinkService.getCampaignClickCount(campaignId);
    }
}
