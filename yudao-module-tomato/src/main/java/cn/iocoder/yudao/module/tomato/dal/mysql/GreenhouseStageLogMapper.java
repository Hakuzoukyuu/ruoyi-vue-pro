package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.GreenhouseStageLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GreenhouseStageLogMapper extends BaseMapperX<GreenhouseStageLogDO> {

    default List<GreenhouseStageLogDO> selectListByGreenhouseId(Long greenhouseId) {
        return selectList(new LambdaQueryWrapperX<GreenhouseStageLogDO>()
                .eq(GreenhouseStageLogDO::getGreenhouseId, greenhouseId)
                .orderByDesc(GreenhouseStageLogDO::getStartDate));
    }

    default GreenhouseStageLogDO selectCurrentByGreenhouseId(Long greenhouseId) {
        return selectOne(new LambdaQueryWrapperX<GreenhouseStageLogDO>()
                .eq(GreenhouseStageLogDO::getGreenhouseId, greenhouseId)
                .isNull(GreenhouseStageLogDO::getEndDate));
    }
}
