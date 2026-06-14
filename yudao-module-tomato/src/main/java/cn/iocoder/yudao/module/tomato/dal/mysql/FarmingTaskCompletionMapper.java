package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingTaskCompletionDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FarmingTaskCompletionMapper extends BaseMapperX<FarmingTaskCompletionDO> {

    default List<FarmingTaskCompletionDO> selectListByGreenhouseIdAndDate(Long greenhouseId, LocalDate date) {
        return selectList(new LambdaQueryWrapperX<FarmingTaskCompletionDO>()
                .eq(FarmingTaskCompletionDO::getGreenhouseId, greenhouseId)
                .eq(FarmingTaskCompletionDO::getCompletionDate, date));
    }

    default FarmingTaskCompletionDO selectByGreenhouseAndTemplateAndDate(Long greenhouseId, Long templateId, LocalDate date) {
        return selectOne(new LambdaQueryWrapperX<FarmingTaskCompletionDO>()
                .eq(FarmingTaskCompletionDO::getGreenhouseId, greenhouseId)
                .eq(FarmingTaskCompletionDO::getTemplateId, templateId)
                .eq(FarmingTaskCompletionDO::getCompletionDate, date));
    }

    default List<FarmingTaskCompletionDO> selectListByGreenhouseIdsAndDateRange(List<Long> greenhouseIds,
                                                                                LocalDate startDate,
                                                                                LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<FarmingTaskCompletionDO>()
                .in(FarmingTaskCompletionDO::getGreenhouseId, greenhouseIds)
                .ge(FarmingTaskCompletionDO::getCompletionDate, startDate)
                .le(FarmingTaskCompletionDO::getCompletionDate, endDate));
    }
}
