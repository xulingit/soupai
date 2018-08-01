package info.meihua.recipients.service.service;

import java.util.List;
import java.util.Map;


/**
 * @author sunwell
 */
public interface IRecipientsTaskService {

    /**
     * 发送任务数据回传
     *  @param campaignId
     * @param taskId
     * @param rIds
     * @param status
     * @param subject
     * @param summary
     * @param content
     * @param email
     * @param untouchedReason
     * @param untouchedReasonDes
     * @param supplierType
     */
    void passBack(Long campaignId, Long taskId, List<Long> rIds, Integer status,
                  String subject, String summary, String content,
                  String email, Integer untouchedReason, String untouchedReasonDes, Integer supplierType);

    /**
     * 联系人标记状态
     *
     * @param contactId
     * @return
     * @throws Exception
     */
    Map<String, Boolean> contactMarks(Long contactId) throws Exception;

    /**
     * 收件人标记状态
     *
     * @param recipientsId
     * @return
     * @throws Exception
     */
    Map<String, Boolean> recipientsMarks(Long recipientsId) throws Exception;


    List<Map<String, Object>> getUnSentEmailRecipients();

    void setUnSentEmailRecipientsReadyToSent(List<Long> taskIds);
}
