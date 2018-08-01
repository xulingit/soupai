package info.meihua.contact.service.controller;

import com.google.gson.reflect.TypeToken;
import common.entity.domain.contact.ContactStatics;
import common.util.lang.GsonUtils;
import info.meihua.contact.service.client.ServiceWebHookClient;
import info.meihua.contact.service.entity.*;
import info.meihua.contact.service.service.*;
import info.meihua.contact.service.util.LogicUtils;
import info.meihua.contact.service.util.analysis.AnalysisUtils;
import info.meihua.contact.service.util.snowflake.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author sunwell
 */
@RequestMapping({"/support"})
@SuppressWarnings({"SpringJavaAutowiringInspection", "unchecked"})
@CrossOrigin
@RestController
public class SupportController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IContactService contactService;
    private final IGroupService groupService;
    private final IPropertySettingService settingService;
    private final ICodeSettingService codeSettingService;
    private final IContactConditionService conditionService;
    private final IContactLogService logService;
    private final ServiceWebHookClient webHookClient;


    @Autowired
    public SupportController(IContactService contactService, IContactLogService logService, IGroupService groupService, IPropertySettingService settingService, IContactConditionService conditionService, ICodeSettingService codeSettingService, ServiceWebHookClient webHookClient ) {
        this.contactService = contactService;
        this.webHookClient = webHookClient;
        this.groupService = groupService;
        this.settingService = settingService;
        this.conditionService = conditionService;
        this.codeSettingService = codeSettingService;
        this.logService = logService;

    }
    //根据conditionId查询condition
    @RequestMapping(value = "/select/conditionId", method = RequestMethod.GET)
    public ContactCondition selectConditionById(@RequestParam(value = "conditionId") Long conditionId){
        return conditionService.selectConditionById(conditionId);
    }

    //根据conditionId克隆受众条件
    @RequestMapping(value = "/clone/conditionId", method = RequestMethod.POST)
    public Long cloneConditionById(@RequestParam(value="conditionId") Long conditionId){
        ContactCondition contactCondition=conditionService.selectConditionById(conditionId);
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        contactCondition.setId(idWorker.nextId());
        try {
            conditionService.add(contactCondition,contactCondition.getUserId(),contactCondition.getAccountId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactCondition.getId();
    }
    @RequestMapping(value = "/code-add/task", method = RequestMethod.GET)
    public void codeAddTask() {
        // 2018/1/16 默认覆盖，没有就新增 字段关系表存在主键冲突，观察调整
        try {
            logger.error(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + "开始执行脚本录入联系人同步服务");
            List<String> keys = codeSettingService.listKeys();
            for (String key : keys) {
                while (AnalysisUtils.getContactsNode(key, "1000").size() > 0) {
                    List<String> sids = new ArrayList<>();
                    List<CodeSettingDataObj> codeSettingDataObjs = AnalysisUtils.getContactsNode(key, "1000");
                    Map<String, CodeSettingDataObj> uniqueCodeSettingDataObjs = new HashMap<>();
                    Map<String, CodeSettingDataObj> repeatCodeSettingDataObjs = new HashMap<>();

                    for (CodeSettingDataObj codeSettingDataObj : codeSettingDataObjs) {
                        if (codeSettingDataObj.getContact_info() == null) {
                            List<String> idsTemp = new ArrayList<>();
                            idsTemp.add(codeSettingDataObj.get_id());
                            AnalysisUtils.editContactNode(idsTemp);
                            continue;
                        }
                        Map<String, Object> contact = GsonUtils.json2map(codeSettingDataObj.getContact_info());
                        String email = contact.get("email").toString();
                        if (uniqueCodeSettingDataObjs.containsKey(email)) {
                            repeatCodeSettingDataObjs.put(email, codeSettingDataObj);
                        } else {
                            uniqueCodeSettingDataObjs.put(email, codeSettingDataObj);
                        }
                    }

                    for (Map.Entry<String, CodeSettingDataObj> item : uniqueCodeSettingDataObjs.entrySet()) {
                        doJsItem(key, item.getValue(), sids, true);
                    }

                    for (Map.Entry<String, CodeSettingDataObj> item : repeatCodeSettingDataObjs.entrySet()) {
                        doJsItem(key, item.getValue(), sids, false);
                    }
                    AnalysisUtils.editContactNode(sids);
                }
            }
            logger.error(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + "结束执行脚本录入联系人同步服务");
        } catch (Exception e) {
            logger.error(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + "执行同步服务异常");
        }
    }

    private void doJsItem(String key, CodeSettingDataObj codeSettingDataObj, List<String> sids, boolean flag) {
        try {
            if (codeSettingDataObj.getContact_info() == null) {
                List<String> idsTemp = new ArrayList<>();
                idsTemp.add(codeSettingDataObj.get_id());
                AnalysisUtils.editContactNode(idsTemp);
            } else {
                String cc = codeSettingDataObj.getContact_info().replace("gmt_registration", "gmtRegistration");
                codeSettingDataObj.setContact_info(cc);
            }
            List<String> groups = (List<String>) GsonUtils.json2list(codeSettingDataObj.getGroups(),
                    new TypeToken<List<String>>() {
                    }.getType());
            int opType = Contact.CONTACT_OP_TYPE_MERGE;
            Long accountId = Long.valueOf(key.split("-")[2]);

            Map<String, Object> contact = GsonUtils.json2map(codeSettingDataObj.getContact_info());

            BigDecimal bd1 = new BigDecimal(codeSettingDataObj.getCreatetime());
            contact.put("gmtLog", bd1.toPlainString());

            logger.error(GsonUtils.object2json(codeSettingDataObj));
            logger.error(GsonUtils.object2json(contact));
            logger.error(codeSettingDataObj.getContact_info());

            List<Contact> list;
            List<Contact> repeatList;
            if (flag) {
                Map<String, Object> resolveData = contactService.resolveDataEx(AnalysisUtils.trans(contact), accountId);
                list = (List<Contact>) resolveData.get("uniqueData");
                repeatList = (List<Contact>) resolveData.get("repeatData");
            } else {
                list = new ArrayList<>();
                repeatList = AnalysisUtils.trans(contact);
            }

            LogicUtils.doEmailPhone(list);
            LogicUtils.doEmailPhone(repeatList);


            Map<String, Long> groupMap = new HashMap<>();
            String source = "";
            for (String groupName : groups) {
                Long groupId = 0L;
                source += groupName + ";";
                if (groupName != null && !"".equals(groupName)) {
                    //标记开始导入
                    groupId = groupService.addPlus(groupName, Group.GROUP_SCOPE_CONTACT, 0L, accountId);
                    groupService.updateTaskCountBegin(groupId);
                    groupMap.put(groupName, groupId);
                }
            }
            settingService.doDefaultSetting(0L, accountId, PropertySetting.PROPERTY_SETTING_SCOPE_CONTACT);

            Map<String, Object> res = contactService.addEx(list, repeatList, opType, "", 0L, accountId, "网站接入", new ArrayList<>(), null, null, null);
//                            Map<String, Object> res = contactService.addEx(list, repeatList, opType, "", 0L, accountId, "脚本导入" + "[" + source + "]", new ArrayList<>());

            List<Long> ids = (List<Long>) res.get("ids");

            for (String groupName : groups) {
                if (groupName != null && !"".equals(groupName)) {
                    groupService.putToGroups(ids, groupMap.get(groupName));
//                                    if (opType == 3) {
//                                        try {
//                                            Webhook webhook = (Webhook) webHookClient.getExtra(accountId, ApiAuthUtil.genAuthCode(accountId.toString()), groupMap.get(groupName)).getData();
//                                            if (webhook != null) {
//                                                contactService.doHook(ids, groupName, groupMap.get(groupName));
//                                            }
//                                        } catch (Exception ignored) {
//
//                                        }
//                                    }
                }
            }
            sids.add(codeSettingDataObj.get_id());
        } catch (Exception e) {
            sids.add(codeSettingDataObj.get_id());
        }
    }

    @RequestMapping(value = {"/condition"}, method = RequestMethod.POST)
    public Long addCondition(@RequestParam(value = "accountId") Long accountId,
                             @RequestParam(value = "userId") Long userId,
                             @RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "conditionStr") String conditionStr) {
        try {
            ContactCondition condition = new ContactCondition();
            if (name == null) {
                name = "";
            }
            condition.setName(name);
            condition.setQueryCondition(conditionStr);

            return conditionService.addEx(condition, userId, accountId);

        } catch (Exception e) {
            return 0L;
        }
    }

    @RequestMapping(value = {"/condition/{id}"}, method = RequestMethod.POST)
    public void updateCondition(@RequestParam(value = "accountId") Long accountId,
                                @PathVariable(value = "id") Long id,
                                @RequestParam(value = "conditionStr") String conditionStr) {
        try {
            ContactCondition condition = new ContactCondition();
            condition.setQueryCondition(conditionStr);
            condition.setId(id);
            conditionService.update(condition, accountId);
        } catch (Exception ignored) {
        }
    }


    @RequestMapping(value = "/recipient/put", method = RequestMethod.GET)
    public void putToRecipients(@RequestParam(value = "conditionId") Long conditionId,
                                @RequestParam(value = "accountId") Long accountId,
                                @RequestParam(value = "userId") Long userId,
                                @RequestParam(value = "campaignId") Long campaignId, @RequestParam Map<String,List<CampaignLog>> campaignLog) {
        try {
            ContactCondition contactCondition = conditionService.get(conditionId, accountId);
            if (contactCondition != null) {
                String conditionStr = contactCondition.getQueryCondition();
                Map<String, Object> settingMap = settingService.doDefaultSetting(userId, accountId, PropertySetting.PROPERTY_SETTING_SCOPE_CONTACT);
                List<Fields> fields = (List<Fields>) settingMap.get("setting");
                List<Map<Integer, Map<String, List<List<QueryItem>>>>> conditions;
                if (conditionStr != null && !Objects.equals(conditionStr, "")) {
                    List<String> conditionsParam = (List<String>) GsonUtils.json2list(conditionStr, new TypeToken<List<String>>() {
                    }.getType());
                    List<String> realConditionStr = new ArrayList<>();
                    for (String conPara : conditionsParam) {
                        realConditionStr.addAll(groupService.calculateConditions(conPara, accountId));
                    }
                    conditions = LogicUtils.getConditions(realConditionStr);
                    //获取日志状态是否修改受众人群
                   if(campaignLog.size() == 5){//重新获取受众筛选条件
                         //重新获取条件
                   setConditiones(conditionId,accountId,userId,campaignId);
                   }else{
                       contactService.doPutToRecipientsSync(conditions, fields, accountId, campaignId);
                   }

                }
            }
        } catch (Exception ignored) {
        }
    }

    private void setConditiones(Long conditionId, Long accountId, Long userId, Long campaignId) {

        try {
            ContactCondition contactCondition = conditionService.get(conditionId, accountId);
            if (contactCondition != null) {
                String conditionStr = contactCondition.getQueryCondition();
                Map<String, Object> settingMap = settingService.doDefaultSetting(userId, accountId, PropertySetting.PROPERTY_SETTING_SCOPE_CONTACT);
                List<Fields> fields = (List<Fields>) settingMap.get("setting");
                List<Map<Integer, Map<String, List<List<QueryItem>>>>> conditions;
                if (conditionStr != null && !Objects.equals(conditionStr, "")) {
                    List<String> conditionsParam = (List<String>) GsonUtils.json2list(conditionStr, new TypeToken<List<String>>() {
                    }.getType());
                    List<String> realConditionStr = new ArrayList<String>();
                    for (String conPara : conditionsParam) {
                        realConditionStr.addAll(groupService.calculateConditions(conPara, accountId));
                    }
                    conditions = LogicUtils.getConditions(realConditionStr);
                    contactService.doPutToRecipientsSync(conditions, fields, accountId, campaignId);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/contact/statics", method = RequestMethod.GET)
    public ContactStatics getContactStatics(@RequestParam(value = "accountId") Long accountId, @RequestParam(value = "gmtStart") Long gmtStart, @RequestParam(value = "gmtEnd") Long gmtEnd) {
        try {
            return contactService.getContactStatics(accountId, gmtStart, gmtEnd);
        } catch (Exception ignored) {
            return null;
        }
    }


    @RequestMapping(value = "/contact/use-quantity", method = RequestMethod.GET)
    public String getContactQuantity(@RequestParam(value = "accountIds") List<Long> accountIds) {
        try {
            return contactService.getContactQuantity(accountIds);
        } catch (Exception ignored) {
            return null;
        }
    }


    /**
     * public static final int LOG_TYPE_CONTACT_CAMPAIGN_ADD = 21;
     * public static final int LOG_TYPE_CONTACT_CAMPAIGN_SEND_EMAIL = 22;
     * <p>
     * public static final int LOG_TYPE_CONTACT_CAMPAIGN_OPEN_EMAIL = 31;
     * public static final int LOG_TYPE_CONTACT_CAMPAIGN_CLICK_EMAIL = 32;
     * public static final int LOG_TYPE_CONTACT_CAMPAIGN_REPLY_EMAIL = 33;
     * <p>
     * public static final int LOG_SCOPE_OPERATE = 3;
     * <p>
     * 将#NAME#添加到营销活动#CAMPAIGN#中    112999#活动名称#联系人姓名
     * <p>
     * public static final int LOG_SCOPE_SYSTEM_AUTO = 2;
     * <p>
     * 营销活动#CAMPAIGN#自动发送了一封邮件#TASK#给#NAME#      112999#活动名称#21312#邮件名称#联系人姓名   detail 邮件内容
     * <p>
     * public static final int LOG_SCOPE_CONTACT = 1;
     * <p>
     * #NAME#打开了邮件#TASK#，在营销活动为#CAMPAIGN#的#STEP#   联系人姓名#123123#邮件名称#123123#活动名称#123123#步骤
     * #NAME#点击了邮件#TASK#的链接#LINK#，在营销活动为#CAMPAIGN#的#STEP#   联系人姓名#123123#邮件名称#123123#活动名称#123123#步骤#link#链接名称
     * #NAME#回复了邮件#TASK#，在营销活动为#CAMPAIGN#的#STEP#   联系人姓名#123123#邮件名称#123123#活动名称#123123#步骤
     */
    @RequestMapping(value = {"/log/{contactId}"}, method = RequestMethod.POST)
    public void log(@RequestParam(value = "type") Integer type,
                    @RequestParam(value = "scope") Integer scope,
                    @RequestParam(value = "content") String content,
                    @RequestParam(value = "replacement") String replacement,
                    @RequestParam(value = "operator") String operator,
                    @RequestParam(value = "ip") String ip,
                    @RequestParam(value = "detail") String detail,
                    @RequestParam(value = "userId") Long userId,
                    @PathVariable(value = "contactId") Long contactId) throws Exception {
        logService.add(new ContactLog(type, scope, content, replacement, detail, ip, userId, contactId, operator, System.currentTimeMillis()));
    }


    @RequestMapping(value = "/query-terms", method = RequestMethod.GET)
    public Map<String, Object> queryTerms(@RequestParam(value = "accountId") Long accountId) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = contactService.queryTerms(accountId, 2);
        } catch (Exception e) {
        }
        return result;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Map<String, Object> search(@RequestParam(value = "accountId") Long accountId,
                                      @RequestParam(value = "userId") Long userId,
                                      @RequestParam(value = "page") Integer page,
                                      @RequestParam(value = "limit") Integer limit,
                                      @RequestParam(value = "conditionsStr", required = false) String conditionsStr,
                                      @RequestParam(value = "keywords", required = false) String keywords
    ) {
        Map<String, Object> result = new HashMap<>();

        try {

            settingService.doDefaultSetting(userId, accountId, PropertySetting.PROPERTY_SETTING_SCOPE_CONTACT);

            List<Map<Integer, Map<String, List<List<QueryItem>>>>> conditions = null;

            if (conditionsStr != null && !Objects.equals(conditionsStr, "")) {
                List<String> conditionsParam = (List<String>) GsonUtils.json2list(conditionsStr, new TypeToken<List<String>>() {
                }.getType());

                List<String> realConditionStr = new ArrayList<>();

                for (String conPara : conditionsParam) {
                    realConditionStr.addAll(groupService.calculateConditions(conPara, accountId));
                }

                conditions = LogicUtils.getConditions(realConditionStr);
            }

            result = contactService.listByCondition(page, limit, keywords, conditions, userId, accountId);

        } catch (Exception e) {
        }
        return result;
    }


    @RequestMapping(value = "/{campaignId}/put", method = RequestMethod.GET)
    public void putToCampaign(@RequestParam(value = "contactIds") List<Long> contactIds,
                              @RequestParam(value = "userId") Long userId,
                              @RequestParam(value = "accountId") Long accountId,
                              @PathVariable(value = "campaignId") Long campaignId) {
        try {
            Map<String, Object> settingMap = settingService.doDefaultSetting(userId, accountId, PropertySetting.PROPERTY_SETTING_SCOPE_CONTACT);
            List<Fields> fields = (List<Fields>) settingMap.get("setting");

            contactService.doPutToCampaignSync(contactIds, fields, accountId, campaignId);
        } catch (Exception ignored) {
        }
    }

    @RequestMapping(value = "/{groupId}/put", method = RequestMethod.POST)
    public void putToGroup(@RequestParam(value = "contactIds") List<Long> contactIds,
                           @PathVariable(value = "groupId") Long groupId) {
        try {
            groupService.putToGroups(contactIds, groupId);
        } catch (Exception ignored) {
        }
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public Map<String, Object> listGroups(@RequestParam(value = "accountId") Long accountId) {
        try {
            return groupService.listAll(null, Group.GROUP_TYPE_NORMAL, Group.GROUP_SCOPE_CONTACT, accountId);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 新增联系人
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void add(@RequestParam(value = "accountId") Long accountId,
                    @RequestParam(value = "contactStr", required = false) String contactStr,//手动新增数据
                    @RequestParam(value = "handle") Integer handle,//1 覆盖  2  合并  3 新增
                    @RequestParam(value = "groupName", required = false) String groupName
    ) {//数组
        Map<String, Object> contact = GsonUtils.json2map(contactStr.replace("gmt_registration", "gmtRegistration"));

        contact.put("gmtLog", System.currentTimeMillis());

        try {

            List<Contact> list;
            List<Contact> repeatList;
            if (handle != 3) {
                Map<String, Object> resolveData = contactService.resolveDataEx(AnalysisUtils.trans(contact), accountId);
                list = (List<Contact>) resolveData.get("uniqueData");
                repeatList = (List<Contact>) resolveData.get("repeatData");
            } else {
                list = AnalysisUtils.trans(contact);
                repeatList = new ArrayList<>();
            }

            LogicUtils.doEmailPhone(list);
            LogicUtils.doEmailPhone(repeatList);

            Map<String, Long> groupMap = new HashMap<>();
            String source = "";
            Long groupId;
            if (groupName != null && !"".equals(groupName)) {
                //标记开始导入
                groupId = groupService.addPlus(groupName, Group.GROUP_SCOPE_CONTACT, 0L, accountId);
                groupService.updateTaskCountBegin(groupId);
                groupMap.put(groupName, groupId);
            }
            settingService.doDefaultSetting(0L, accountId, PropertySetting.PROPERTY_SETTING_SCOPE_CONTACT);

            Map<String, Object> res = contactService.addEx(list, repeatList, handle, "", 0L, accountId, "开放平台接入", new ArrayList<>(), null, null, null);

            List<Long> ids = (List<Long>) res.get("ids");
            if (groupName != null && !"".equals(groupName)) {
                groupService.putToGroups(ids, groupMap.get(groupName));
//                                    if (opType == 3) {
//                                        try {
//                                            Webhook webhook = (Webhook) webHookClient.getExtra(accountId, ApiAuthUtil.genAuthCode(accountId.toString()), groupMap.get(groupName)).getData();
//                                            if (webhook != null) {
//                                                contactService.doHook(ids, groupName, groupMap.get(groupName));
//                                            }
//                                        } catch (Exception ignored) {
//
//                                        }
//                                    }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 新增联系人
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public void modify(@RequestParam(value = "accountId") Long accountId,
                       @RequestParam(value = "userId") Long userId,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "ip") String ip,
                       @RequestParam(value = "contactStr") String contactStr
    ) {
        try {
            contactService.updateSingleField(GsonUtils.json2map(contactStr), ip, userId, username, accountId);
        } catch (Exception e) {

        }
    }


}
