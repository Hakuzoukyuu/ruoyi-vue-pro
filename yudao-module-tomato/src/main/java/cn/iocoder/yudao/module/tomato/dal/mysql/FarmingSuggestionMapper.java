package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingSuggestionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FarmingSuggestionMapper extends BaseMapperX<FarmingSuggestionDO> {

    default FarmingSuggestionDO selectByStage(Integer stage) {
        return selectOne(FarmingSuggestionDO::getStage, stage);
    }
}
