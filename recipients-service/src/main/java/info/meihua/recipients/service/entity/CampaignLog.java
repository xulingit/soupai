package info.meihua.recipients.service.entity;

import java.io.Serializable;

/**
 * content  replacement detail
 *
 * @author sunwell.gu
 */
public class CampaignLog implements Serializable {

    private static final long serialVersionUID = 1364619984594706668L;

    public static final int LOG_SCOPE_USER = 1;
    public static final int LOG_SCOPE_SYSTEM = 2;

    public static final int LOG_STATUS_DOING = 1;
    public static final int LOG_STATUS_FINISHED = 2;

    /**
     * content 新增了活动步骤  replacement  step_id#step_order  detail  null
     */
    public static final int LOG_TYPE_STEP_ADD = 16;
    /**
     * content 删除了活动步骤  replacement  step_id#step_order  detail  null  影响：停止该活动该步骤筛选、开启的活动中止该活动该步骤下所有邮件发送，邮件部分反馈此操作已完成
     */
    public static final int LOG_TYPE_STEP_DEL = 18;
    /**
     * content 修改了活动步骤的等待时间，由XXX改为了XXX  replacement  step_id#step_order  detail  null  影响：停止该活动该步骤筛选
     */
    public static final int LOG_TYPE_STEP_TIME_MODIFY = 172;

    /**
     * content 修改了活动步骤的触达条件，由XXX改为了XXX  replacement  【step_id、step_order、step_terms】 detail  null  影响：停止该活动该步骤筛选
     */
    public static final int LOG_TYPE_STEP_CONDITION_MODIFY=175 ;


    /**
     * content 添加了活动步骤的邮件  replacement  step_id#step_order#task_id  detail  null
     */
    public static final int LOG_TYPE_STEP_MAIL_ADD = 171;
    /**
     * content 修改了活动步骤的邮件  replacement  step_id#step_order#task_id  detail  null   影响：开启中的活动的该邮件立即中止发送，邮件部分反馈此操作已完成
     */
    public static final int LOG_TYPE_STEP_MAIL_MODIFY = 173;
    /**
     * content 删除了活动步骤的邮件  replacement  step_id#step_order#task_id  detail  null   影响：开启中的活动的该邮件立即中止发送，邮件部分反馈此操作已完成
     */
    public static final int LOG_TYPE_STEP_MAIL_REMOVE = 174;

    /**
     * content 添加了受众筛选条件  replacement  condition_id   detail  条件详情
     */
    public static final int LOG_TYPE_CAMPAIGN_RECIPIENTS_TERM_ADD = 14;
    /**
     * content 修改了受众筛选条件  replacement  condition_id   detail  条件详情  影响：停止活动筛选受众、之前的受众如何处理（todo）、开启的活动中止发送
     */
    public static final int LOG_TYPE_CAMPAIGN_RECIPIENTS_TERM_MODIFY = 141;
    /**
     * content 移除了收件人  replacement  收件人id   detail  影响：停止活动发送，todo 后续如何处理？
     */
    public static final int LOG_TYPE_CAMPAIGN_RECIPIENTS_REMOVE = 142;

    /**
     * content 开启了活动（，活动执行时间为：XXX）  replacement  null  detail  条件详情
     */
    public static final int LOG_TYPE_CAMPAIGN_OPEN = 12;
    /**
     * content 关闭了活动  replacement  null   detail  条件详情
     */
    public static final int LOG_TYPE_CAMPAIGN_CLOSE = 13;
    /**
     * content 删除了活动  replacement  null   detail  条件详情
     */
    public static final int LOG_TYPE_CAMPAIGN_DELETE= 1312;
    /**
     * content 修改了活动名称，由""改为了""  replacement  null  detail  null
     */
    public static final int LOG_TYPE_CAMPAIGN_MODIFY_NAME = 11;
    /**
     * content 修改了活动XXX（时区、时间间隔、时间段、执行时间），由""改为了""  replacement  null  detail  null  影响：开启的活动中止发送
     */
    public static final int LOG_TYPE_CAMPAIGN_MODIFY = 111;

    /**
     * content 收件人符合活动条件，添加到活动  replacement  null   detail 收件人list
     */
    public static final int LOG_TYPE_CAMPAIGN_RECIPIENTS_ADD = 21;
    /**
     *  #RECEIVER#符合触达规则，进入待发送列表，即将发送
     */
    public static final int LOG_TYPE_CAMPAIGN_RECIPIENTS_TASK_ADD = 123;
    /**
     * content 收件人触发了活动步骤条件  replacement  step_id#step_order#task_id   detail 收件人list
     */
    public static final int LOG_TYPE_CAMPAIGN_RECIPIENTS_ADD_TASK = 22;

    /**
     * content 活动进入就绪状态  replacement  null   detail null
     */
    public static final int LOG_TYPE_CAMPAIGN_READY = 23;
    /**
     * content 活动开始发送邮件  replacement  step_id#step_order#task_id   detail 收件人list
     */
    public static final int LOG_TYPE_CAMPAIGN_SENDING = 24;
    /**
     * content 活动正在运行中   replacement  null   detail null
     */
    public static final int LOG_TYPE_CAMPAIGN_RUNNING = 25;
    /**
     * content 活动出现异常，异常原因：   replacement  null   detail null
     */
    public static final int LOG_TYPE_CAMPAIGN_EXCEPTION = 26;
    /**
     * content 活动执行完毕   replacement  null   detail null
     */
    public static final int LOG_TYPE_CAMPAIGN_FINISHED = 27;
    /**
     * content 活动被关闭了  replacement  null   detail null
     */
    public static final int LOG_TYPE_CAMPAIGN_CLOSED = 28;
    /**
     * content 正在计算符合营销条件的收件人  replacement  null   detail null
     */
    public static final int LOG_TYPE_CAMPAIGN_AGAIN= 82;

    /**
     * content 系统在活动第#STEP_ORDER#步自动给#RECEIVER#发送了一封主题为"TASK_SUBJECT"的邮件
     */
    public static final int LOG_TYPE_CAMPAIGN_STEP_ORDER= 820;
    //日志id
    private Long id;
    //产生日志的类型
    private Integer type;
    //区分是系统操作,还是用户操作
    private Integer scope;
    //是否有影响
    private Integer status;
    //产生日志的类型对应的内容
    private String content;
    //修改了哪一步
    private String replacement;
    //更详细的说明
    private String detail;
    //所属的ip
    private String ip;
    //修改的用户id
    private Long userId;
    //修改的那条活动的id
    private Long campaignId;
    //修改人名称
    private String operator;
    //创建时间
    private Long gmtCreate;
    //修改时间
    private Long gmtModified;



    public CampaignLog(Long id, Integer type, Integer scope, Integer status, String content, String replacement, String detail, String ip, Long userId, Long campaignId, String operator, Long gmtCreate, Long gmtModified) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.replacement = replacement;
        this.detail = detail;
        this.ip = ip;
        this.userId = userId;
        this.campaignId = campaignId;
        this.operator = operator;
        this.gmtCreate = gmtCreate;
        this.scope = scope;
        this.status = status;
        this.gmtModified = gmtModified;
    }


    public CampaignLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }
}
