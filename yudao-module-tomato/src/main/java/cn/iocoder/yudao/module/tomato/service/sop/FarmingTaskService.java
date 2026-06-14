package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingStatisticsRespVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingTaskCreateReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingTaskRespVO;

import java.time.LocalDateTime;
import java.util.List;

public interface FarmingTaskService {

    /**
     * 手动添加农事记录
     */
    void createFarmingTask(Long userId, FarmingTaskCreateReqVO reqVO);

    /**
     * 查询农事记录（按大棚+时间范围+类型筛选）
     */
    List<FarmingTaskRespVO> getFarmingTasks(Long userId, Long greenhouseId, String taskType,
                                            LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取农事统计看板数据
     */
    FarmingStatisticsRespVO getStatistics(Long userId);
}
