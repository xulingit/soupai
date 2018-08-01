package info.meihua.task.service.entity;

import java.util.ArrayList;
import java.util.List;

public class Replacement {

    private String userEmil;

    private Long stepId;

    private Integer stepOrder;

    private String stepTerms;

    private String stepOldTerms;

    private String taskSubject;

    private Long conditionId;

    private String conditionTerms;

    private String conditionOldTerms;

    private List<Long> taskId;

    private List<Long> recipientIds;

    private List<String> recipientEmils;

    public Replacement(){
        taskId=new ArrayList<Long>();
        recipientIds=new ArrayList<Long>();
        recipientEmils=new ArrayList<String>();
    }

    public String getUserEmil() {
        return userEmil;
    }

    public void setUserEmil(String userEmil) {
        this.userEmil = userEmil;
    }

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStepTerms() {
        return stepTerms;
    }

    public void setStepTerms(String stepTerms) {
        this.stepTerms = stepTerms;
    }

    public String getStepOldTerms() {
        return stepOldTerms;
    }

    public void setStepOldTerms(String stepOldTerms) {
        this.stepOldTerms = stepOldTerms;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public String getTaskSubject() {
        return taskSubject;
    }

    public void setTaskSubject(String taskSubject) {
        this.taskSubject = taskSubject;
    }

    public String getConditionTerms() {
        return conditionTerms;
    }

    public void setConditionTerms(String conditionTerms) {
        this.conditionTerms = conditionTerms;
    }

    public String getConditionOldTerms() {
        return conditionOldTerms;
    }

    public void setConditionOldTerms(String conditionOldTerms) {
        this.conditionOldTerms = conditionOldTerms;
    }

    public List<Long> getTaskId() {
        return taskId;
    }

    public void setTaskId(List<Long> taskId) {
        this.taskId = taskId;
    }

    public List<Long> getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(List<Long> recipientIds) {
        this.recipientIds = recipientIds;
    }

    public List<String> getRecipientEmils() {
        return recipientEmils;
    }

    public void setRecipientEmils(List<String> recipientEmils) {
        this.recipientEmils = recipientEmils;
    }
}
