package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingStatisticsDailyDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FarmingStatisticsDailyMapper extends BaseMapperX<FarmingStatisticsDailyDO> {

    default List<FarmingStatisticsDailyDO> selectListByGreenhouseIdsAndDateRange(List<Long> greenhouseIds,
                                                                                 LocalDate startDate,
                                                                                 LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<FarmingStatisticsDailyDO>()
                .in(FarmingStatisticsDailyDO::getGreenhouseId, greenhouseIds)
                .ge(FarmingStatisticsDailyDO::getStatDate, startDate)
                .le(FarmingStatisticsDailyDO::getStatDate, endDate)
                .orderByAsc(FarmingStatisticsDailyDO::getStatDate));
    }

    default FarmingStatisticsDailyDO selectByGreenhouseAndDateAndType(Long greenhouseId, LocalDate date, String taskType) {
        return selectOne(new LambdaQueryWrapperX<FarmingStatisticsDailyDO>()
                .eq(FarmingStatisticsDailyDO::getGreenhouseId, greenhouseId)
                .eq(FarmingStatisticsDailyDO::getStatDate, date)
                .eq(FarmingStatisticsDailyDO::getTaskType, taskType));
    }
}
