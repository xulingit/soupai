package info.meihua.campaign.service.service.impl;

import info.meihua.campaign.service.dao.ICampaignDao;
import info.meihua.campaign.service.dao.ICampaignLogDao;
import info.meihua.campaign.service.entity.CampaignLog;
import info.meihua.campaign.service.service.ICampaignLogService;
import info.meihua.campaign.service.service.ICampaignStepContentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
@SuppressWarnings("SpringJavaAutowiringInspection")
public class CampaignLogServiceImpl implements ICampaignLogService {

    private final ICampaignLogDao campaignLogDao;
    @Autowired
    public CampaignLogServiceImpl(ICampaignLogDao campaignLogDao) {
        this.campaignLogDao = campaignLogDao;

    }
    public CampaignLog get(Long campaignId, Long userId, Integer type) {
        return null;
    }

    public List<CampaignLog> getCampaignLog(Long userId, Long campaignId) throws Exception {
        return campaignLogDao.getCampaignLog(userId,campaignId);
    }

    public void updateCampaignLog(List<CampaignLog> campaignlog) {
        try {
            if(null != campaignlog && campaignlog.size()>0){
                for(CampaignLog c : campaignlog){
                    c.setGmtModified(System.currentTimeMillis());
                }
            }
            campaignLogDao.updateCampaignLog(campaignlog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertCampaignLog(CampaignLog campaignLog)throws Exception {
        campaignLogDao.insert(campaignLog);
    }

    public void updateCampaign_log(CampaignLog campaignlog) throws Exception {
        if(null != campaignlog){
            campaignlog.setGmtModified(System.currentTimeMillis());
        }
        campaignLogDao.updateStatus(campaignlog);
    }

    @Override
    public List<CampaignLog> selectCampaignLog(Integer scope, Long campaignId, Integer type) {
        List<CampaignLog> campaignLog = null;
        try {
            campaignLog =  campaignLogDao.selectCampaignLog(scope,campaignId,type);
        } catch (Exception e) {
            return null;
        }
         return campaignLog;
    }

    @Override
    public List<CampaignLog> listAllCamapignLog() {
        List<CampaignLog> campaignLog = null;
        try {
            campaignLog = campaignLogDao.listAllCampaignLog();
        } catch (Exception e) {
            return null;
        }
        return campaignLog;
    }
}
