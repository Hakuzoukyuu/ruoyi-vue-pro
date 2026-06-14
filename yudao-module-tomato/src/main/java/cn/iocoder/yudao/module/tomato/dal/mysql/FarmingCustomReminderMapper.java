package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingCustomReminderDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FarmingCustomReminderMapper extends BaseMapperX<FarmingCustomReminderDO> {

    default List<FarmingCustomReminderDO> selectListByGreenhouseIdAndDate(Long greenhouseId, LocalDate date) {
        return selectList(new LambdaQueryWrapperX<FarmingCustomReminderDO>()
                .eq(FarmingCustomReminderDO::getGreenhouseId, greenhouseId)
                .eq(FarmingCustomReminderDO::getReminderDate, date)
                .orderByAsc(FarmingCustomReminderDO::getIsCompleted));
    }

    default List<FarmingCustomReminderDO> selectListByGreenhouseId(Long greenhouseId) {
        return selectList(new LambdaQueryWrapperX<FarmingCustomReminderDO>()
                .eq(FarmingCustomReminderDO::getGreenhouseId, greenhouseId)
                .orderByDesc(FarmingCustomReminderDO::getReminderDate));
    }

    default List<FarmingCustomReminderDO> selectPendingByGreenhouseId(Long greenhouseId) {
        return selectList(new LambdaQueryWrapperX<FarmingCustomReminderDO>()
                .eq(FarmingCustomReminderDO::getGreenhouseId, greenhouseId)
                .eq(FarmingCustomReminderDO::getIsCompleted, 0)
                .orderByAsc(FarmingCustomReminderDO::getReminderDate));
    }
}
