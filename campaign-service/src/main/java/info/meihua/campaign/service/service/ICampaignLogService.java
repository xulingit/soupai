package info.meihua.campaign.service.service;

import info.meihua.campaign.service.entity.CampaignLog;

import java.util.List;

/**
 * @author lin xu
 */
public interface ICampaignLogService {

    /**
     * 获取受众用户的信息
     *
     * @param userId
     * @param campaignid
     * @param type
     * @return
     */
    CampaignLog get(Long campaignId, Long userId, Integer type);


    List<CampaignLog> getCampaignLog(Long userId, Long campaignId) throws Exception;

    void updateCampaignLog(List<CampaignLog> campaignlog) throws Exception;

    void insertCampaignLog(CampaignLog campaignLog) throws Exception;

    void updateCampaign_log(CampaignLog campaignlog) throws Exception;

    List<CampaignLog> selectCampaignLog(Integer scope, Long campaignId, Integer type);

    List<CampaignLog> listAllCamapignLog();
}