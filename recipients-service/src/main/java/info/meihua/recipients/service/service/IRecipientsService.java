package info.meihua.recipients.service.service;


import info.meihua.recipients.service.entity.*;
import info.meihua.recipients.service.entity.extend.campaign.RecipientsCampaign;

import java.util.List;
import java.util.Map;

/**
 * @author sunwell
 */
public interface IRecipientsService {
    /**
     * 新增到收件人池
     *
     * @param accountId
     * @param campaignId
     * @param recipientsContacts
     * @throws Exception
     */
    void insert(Long accountId, Long campaignId, List<RecipientsContact> recipientsContacts) throws Exception;

    /**
     * 新增到收件人池
     *
     * @param accountId
     * @param campaignId
     * @param recipientIds
     * @throws Exception
     */
    void putToCampaign(Long accountId, Long campaignId, List<Long> recipientIds) throws Exception;

    /**
     * 移除收件人
     *
     * @param campaignId
     * @param recipientIds
     */
    void removeFromCampaign(Long campaignId, List<Long> recipientIds);

    /**
     * 添加到黑名单
     *
     * @param accountId
     * @param userId
     * @param recipientIds
     */
    void putToBlacklist(Long accountId, Long userId, List<Long> recipientIds);


    /**
     * 获取活动收件人列表
     *
     * @param campaignId
     * @param status     2 打开、3 点击、 4 退订、 6未触达, 8 回复、 7 弹回
     * @param step
     * @param page
     * @param limit
     * @param accountId
     * @return
     * @throws Exception
     */
    Map<String, Object> listRecipientsByCampaignId(Long campaignId, Integer status, Integer step, Integer page, Integer limit, Long accountId) throws Exception;


    /**
     * 获取收件人详情
     *
     * @param recipientId
     * @return
     */
    Recipients get(Long recipientId);


    /**
     * 获取已加入活动的联系人id
     *
     * @param campaignId
     * @return
     * @throws Exception
     */
    List<Long> listContactIdsByCampaignId(Long campaignId) throws Exception;

    /**
     * 获取联系人所有活动
     *
     * @param contactId
     * @return
     */
    List<RecipientsCampaign> listCampaignsByContactId(Long contactId);

    /**
     * 获取联系人所有任务
     *
     * @param contactId
     * @return
     */
    List<RecipientsContactTask> listTasksByContactId(Long contactId);

    /**
     * 获取task对应未发送收件人
     *
     * @param campaignId
     * @param taskId
     * @return
     * @throws Exception
     */
    List<Recipients> listRecipientsByCampaignIdTaskId(Long campaignId, Long taskId) throws Exception;

    /**
     * 给收件人加锁
     *
     * @param campaignId
     * @param taskId
     * @param recipientIds
     * @throws Exception
     */
    void updateLockedStatus(Long campaignId, Long taskId, List<Long> recipientIds) throws Exception;

    /**
     * 获取task对应未发送收件人
     *
     * @param campaignId
     * @param taskId
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    Map<String, Object> listPagerRecipientsByCampaignIdTaskId(Long campaignId, Long taskId, Integer page, Integer limit) throws Exception;

    /**
     * 获取收件人列表
     *
     * @param campaignId
     * @param taskId
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    List<Recipients> listPagerRecipientsByCampaignIdTaskId1(Long campaignId, Long taskId, Integer page, Integer limit) throws Exception;

    /**
     * 获取收件人总数
     *
     * @param campaignId
     * @param taskId
     * @return
     * @throws Exception
     */
    Integer countPagerRecipientsByCampaignIdTaskId1(Long campaignId, Long taskId) throws Exception;

    /**
     * 收件人步骤服务
     * {"delaytime":"1:5:29","type":"1","task_id":"1,2","click_url":"1#2,1#2,1#3,1#4"}
     * （时分秒）\(0:不做任何条件判断,1:未打开上一封邮件,2:未点击上一封连接,3:点击了上一封连接)  未回复\（上一封邮件任务ID）\点击taskid#链接ID
     *
     * @param campaignId
     * @param order
     * @param tasks
     * @param gmtAction
     * @param type
     * @param latestTasks
     * @param clickUrl
     * @throws Exception
     */
    void doCampaignStepTask(Long campaignId, Integer order, List<String> tasks, Long gmtAction, Integer type, List<String> latestTasks, List<Long> clickUrl,Map<String,List<CampaignLog>> map) throws Exception;


    /**
     * 获取活动概览
     *
     * @param campaignId
     * @param accountId
     * @return
     * @throws Exception
     */
    Map<String, Object> getCampaignOverview(Long campaignId, Long accountId) throws Exception;

    Map<String, Object> getCampaignOverviewStatics(Long campaignId, Long accountId, Long gmtStart, Long gmtEnd) throws Exception;

    /**
     * 活动概览数据，每步task数据，活动时区
     *
     * @param campaignId
     * @param gmtStart
     * @param gmtEnd
     * @param accountId
     * @return
     * @throws Exception
     */
    Map<String, Object> statistics(Long campaignId, Long gmtStart, Long gmtEnd, Long accountId) throws Exception;

    /**
     * 图表数据
     *
     * @param campaignId
     * @param gmtStart
     * @param gmtEnd
     * @param accountId
     * @return
     * @throws Exception
     */
    List<RecipientStatisticsChart> chart(Long campaignId, Long gmtStart, Long gmtEnd, Long accountId) throws Exception;

    /**
     * 操作数据回传 todo 自动响应处理逻辑
     *
     * @param campaignId
     * @param replyCode
     * @param responseType
     * @param email
     * @param taskId
     * @param recipientId
     * @param urlId
     * @param taskLinkId
     * @param replyClassification
     * @param content
     * @param replySubject
     * @param read
     */
    void operationPassBack(Long campaignId, String responseType, String email, Long taskId, Long recipientId, String url, Long taskLinkId, Integer replyClassification, String replyCode, String content,
                           String replySubject, Integer read);


}
