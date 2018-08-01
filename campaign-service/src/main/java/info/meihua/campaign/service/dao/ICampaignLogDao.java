package info.meihua.campaign.service.dao;

import info.meihua.campaign.service.entity.CampaignLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sunwell
 */
public interface ICampaignLogDao {
    /**
     * 新增日志
     *
     * @param campaignLog
     * @throws Exception
     */
    void insert(CampaignLog campaignLog) throws Exception;

    /**
     * 编辑
     *
     * @param id
     * @param status
     * @param gmtModified
     * @throws Exception
     */
    void update(@Param("id") Long id, @Param("status") Integer status, @Param("gmtModified") Long gmtModified) throws Exception;

    /**
     * 获取日志
     *
     * @param campaignId
     * @return
     * @throws Exception
     */
    List<CampaignLog> listByCampaignId(@Param("campaignId") Long campaignId) throws Exception;

    List<CampaignLog> getCampaignLog(@Param("userId") Long userId, @Param("campaignId") Long campaignId)throws Exception;
    /**
     * 更新日志的状态
     *
     * @param campaignlog
     * @return status
     * @throws Exception
     */
    void updateCampaignLog(@Param("campaignlog") List<CampaignLog> campaignlog)throws Exception;

    /**
     * 批量插入日志
     *
     * @param campaignLog
     * @return
     * @throws Exception
     */
    void batchInsertionCampaignLog(@Param("c_list") List<CampaignLog> c_list);

    void updateStatus(CampaignLog campaignlog);

    List<CampaignLog> selectCampaignLog(@Param("scope") Integer scope, @Param("campaignId") Long campaignId, @Param("type") Integer type)throws Exception;

    List<CampaignLog> listAllCampaignLog();
}
