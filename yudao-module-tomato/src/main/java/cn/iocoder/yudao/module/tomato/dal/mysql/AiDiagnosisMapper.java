package cn.iocoder.yudao.module.tomato.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tomato.dal.dataobject.AiDiagnosisDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiDiagnosisMapper extends BaseMapperX<AiDiagnosisDO> {

    default List<AiDiagnosisDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiDiagnosisDO>()
                .eq(AiDiagnosisDO::getUserId, userId)
                .orderByDesc(AiDiagnosisDO::getCreatedAt));
    }

    default List<AiDiagnosisDO> selectListByGreenhouseId(Long greenhouseId) {
        return selectList(new LambdaQueryWrapperX<AiDiagnosisDO>()
                .eq(AiDiagnosisDO::getGreenhouseId, greenhouseId)
                .orderByDesc(AiDiagnosisDO::getCreatedAt));
    }

}
