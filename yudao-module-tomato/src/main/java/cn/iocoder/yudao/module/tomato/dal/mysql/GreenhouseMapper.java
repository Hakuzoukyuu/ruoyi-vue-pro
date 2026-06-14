package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.GreenhouseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GreenhouseMapper extends BaseMapperX<GreenhouseDO> {

    default GreenhouseDO selectByCode(String code) {
        return selectOne(GreenhouseDO::getCode, code);
    }

    default List<GreenhouseDO> selectListByOwnerId(Long ownerId) {
        return selectList(new LambdaQueryWrapperX<GreenhouseDO>()
                .eq(GreenhouseDO::getOwnerId, ownerId)
                .orderByAsc(GreenhouseDO::getCode));
    }

    default List<GreenhouseDO> selectListByManagerId(Long managerId) {
        return selectList(new LambdaQueryWrapperX<GreenhouseDO>()
                .eq(GreenhouseDO::getManagerId, managerId)
                .orderByAsc(GreenhouseDO::getCode));
    }

    default List<GreenhouseDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<GreenhouseDO>()
                .orderByAsc(GreenhouseDO::getCode));
    }
}
