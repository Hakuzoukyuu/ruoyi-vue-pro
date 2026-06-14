package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingSopTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FarmingSopTemplateMapper extends BaseMapperX<FarmingSopTemplateDO> {

    default List<FarmingSopTemplateDO> selectListByStage(Integer stage) {
        return selectList(new LambdaQueryWrapperX<FarmingSopTemplateDO>()
                .eq(FarmingSopTemplateDO::getStage, stage)
                .eq(FarmingSopTemplateDO::getIsEnabled, 1)
                .orderByAsc(FarmingSopTemplateDO::getSortOrder));
    }

    default List<FarmingSopTemplateDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<FarmingSopTemplateDO>()
                .orderByAsc(FarmingSopTemplateDO::getStage)
                .orderByAsc(FarmingSopTemplateDO::getSortOrder));
    }

    default List<FarmingSopTemplateDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<FarmingSopTemplateDO>()
                .eq(FarmingSopTemplateDO::getIsEnabled, 1)
                .orderByAsc(FarmingSopTemplateDO::getStage)
                .orderByAsc(FarmingSopTemplateDO::getSortOrder));
    }
}
