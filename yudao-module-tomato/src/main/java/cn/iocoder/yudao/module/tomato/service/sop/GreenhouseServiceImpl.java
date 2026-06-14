package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.GreenhouseRespVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.GreenhouseStageChangeReqVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.*;
import cn.iocoder.yudao.module.tomato.dal.mysql.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GreenhouseServiceImpl implements GreenhouseService {

    private static final Map<Integer, String> STAGE_NAMES = new HashMap<>();
    static {
        STAGE_NAMES.put(1, "苗期");
        STAGE_NAMES.put(2, "定植缓苗期");
        STAGE_NAMES.put(3, "开花期");
        STAGE_NAMES.put(4, "坐果期");
        STAGE_NAMES.put(5, "膨果期");
        STAGE_NAMES.put(6, "转色采收期");
    }

    @Resource
    private GreenhouseMapper greenhouseMapper;

    @Resource
    private GreenhouseStageLogMapper stageLogMapper;

    @Resource
    private FarmingSuggestionMapper suggestionMapper;

    @Override
    public List<GreenhouseRespVO> getGreenhouseList(Long userId) {
        // 先按owner查找，再按manager查找
        List<GreenhouseDO> list = greenhouseMapper.selectListByOwnerId(userId);
        if (list.isEmpty()) {
            list = greenhouseMapper.selectListByManagerId(userId);
        }
        if (list.isEmpty()) {
            // 管理员看所有
            list = greenhouseMapper.selectAll();
        }

        List<GreenhouseRespVO> result = new ArrayList<>();
        for (GreenhouseDO gh : list) {
            result.add(convertToRespVO(gh));
        }
        return result;
    }

    @Override
    public GreenhouseRespVO getGreenhouseDetail(Long greenhouseId) {
        GreenhouseDO greenhouse = greenhouseMapper.selectById(greenhouseId);
        if (greenhouse == null) {
            return null;
        }
        return convertToRespVO(greenhouse);
    }

    @Override
    @Transactional
    public void changeStage(Long userId, GreenhouseStageChangeReqVO reqVO) {
        Long greenhouseId = reqVO.getGreenhouseId();
        Integer targetStage = reqVO.getTargetStage();

        GreenhouseDO greenhouse = greenhouseMapper.selectById(greenhouseId);
        if (greenhouse == null) {
            throw new RuntimeException("大棚不存在");
        }

        LocalDate today = LocalDate.now();

        // 1. 结束当前生长期记录
        GreenhouseStageLogDO currentLog = stageLogMapper.selectCurrentByGreenhouseId(greenhouseId);
        if (currentLog != null) {
            currentLog.setEndDate(today);
            stageLogMapper.updateById(currentLog);
        }

        // 2. 创建新的生长期记录
        GreenhouseStageLogDO newLog = GreenhouseStageLogDO.builder()
                .greenhouseId(greenhouseId)
                .stage(targetStage)
                .startDate(today)
                .operatorId(userId)
                .createTime(LocalDateTime.now())
                .build();
        stageLogMapper.insert(newLog);

        // 3. 更新大棚当前生长期
        greenhouse.setCurrentStage(targetStage);
        greenhouse.setUpdateTime(LocalDateTime.now());
        greenhouseMapper.updateById(greenhouse);

        log.info("大棚 {} 生长期切换: {} -> {}, 操作人: {}", greenhouseId,
                STAGE_NAMES.get(greenhouse.getCurrentStage()), STAGE_NAMES.get(targetStage), userId);
    }

    @Override
    public List<GreenhouseStageLogDO> getStageHistory(Long greenhouseId) {
        return stageLogMapper.selectListByGreenhouseId(greenhouseId);
    }

    private GreenhouseRespVO convertToRespVO(GreenhouseDO greenhouse) {
        GreenhouseRespVO vo = new GreenhouseRespVO();
        vo.setId(greenhouse.getId());
        vo.setCode(greenhouse.getCode());
        vo.setName(greenhouse.getName());
        vo.setArea(greenhouse.getArea());
        vo.setVariety(greenhouse.getVariety());
        vo.setPlantingDate(greenhouse.getPlantingDate());
        vo.setCurrentStage(greenhouse.getCurrentStage());
        vo.setCurrentStageName(STAGE_NAMES.getOrDefault(greenhouse.getCurrentStage(), "未知"));
        vo.setTotalYield(greenhouse.getTotalYield());
        vo.setOwnerId(greenhouse.getOwnerId());
        vo.setManagerId(greenhouse.getManagerId());
        vo.setNotes(greenhouse.getNotes());

        // 计算生长期天数
        GreenhouseStageLogDO stageLog = stageLogMapper.selectCurrentByGreenhouseId(greenhouse.getId());
        if (stageLog != null && stageLog.getStartDate() != null) {
            vo.setStageDays((int) ChronoUnit.DAYS.between(stageLog.getStartDate(), LocalDate.now()) + 1);
        } else {
            vo.setStageDays(1);
        }

        // 获取静态农事建议
        FarmingSuggestionDO suggestion = suggestionMapper.selectByStage(greenhouse.getCurrentStage());
        if (suggestion != null) {
            vo.setSuggestion(suggestion.getSuggestion());
        }

        return vo;
    }
}
