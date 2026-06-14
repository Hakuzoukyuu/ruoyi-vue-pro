package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FarmingTaskMapper extends BaseMapperX<FarmingTaskDO> {

    default List<FarmingTaskDO> selectListByGreenhouseId(Long greenhouseId) {
        return selectList(new LambdaQueryWrapperX<FarmingTaskDO>()
                .eq(FarmingTaskDO::getGreenhouseId, greenhouseId)
                .orderByDesc(FarmingTaskDO::getTaskTime));
    }

    default List<FarmingTaskDO> selectListByGreenhouseIdAndType(Long greenhouseId, String taskType) {
        return selectList(new LambdaQueryWrapperX<FarmingTaskDO>()
                .eq(FarmingTaskDO::getGreenhouseId, greenhouseId)
                .eqIfPresent(FarmingTaskDO::getTaskType, taskType)
                .orderByDesc(FarmingTaskDO::getTaskTime));
    }

    default List<FarmingTaskDO> selectListByGreenhouseIdAndTimeRange(Long greenhouseId,
                                                                     LocalDateTime startTime,
                                                                     LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<FarmingTaskDO>()
                .eq(FarmingTaskDO::getGreenhouseId, greenhouseId)
                .ge(FarmingTaskDO::getTaskTime, startTime)
                .le(FarmingTaskDO::getTaskTime, endTime)
                .orderByDesc(FarmingTaskDO::getTaskTime));
    }

    default List<FarmingTaskDO> selectListByGreenhouseIds(List<Long> greenhouseIds,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<FarmingTaskDO>()
                .in(FarmingTaskDO::getGreenhouseId, greenhouseIds)
                .geIfPresent(FarmingTaskDO::getTaskTime, startTime)
                .leIfPresent(FarmingTaskDO::getTaskTime, endTime)
                .orderByDesc(FarmingTaskDO::getTaskTime));
    }
}
