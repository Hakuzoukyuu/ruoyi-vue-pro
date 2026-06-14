package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.GreenhouseRespVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.GreenhouseStageChangeReqVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.GreenhouseStageLogDO;

import java.util.List;

public interface GreenhouseService {

    /**
     * 获取用户关联的大棚列表
     */
    List<GreenhouseRespVO> getGreenhouseList(Long userId);

    /**
     * 获取大棚详情（含生长期天数和农事建议）
     */
    GreenhouseRespVO getGreenhouseDetail(Long greenhouseId);

    /**
     * 切换大棚生长期
     */
    void changeStage(Long userId, GreenhouseStageChangeReqVO reqVO);

    /**
     * 获取大棚生长期变更历史
     */
    List<GreenhouseStageLogDO> getStageHistory(Long greenhouseId);
}
