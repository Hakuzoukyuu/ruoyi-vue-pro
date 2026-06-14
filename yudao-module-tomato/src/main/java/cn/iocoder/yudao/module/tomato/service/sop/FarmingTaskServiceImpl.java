package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingStatisticsRespVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingTaskCreateReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingTaskRespVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.*;
import cn.iocoder.yudao.module.tomato.dal.mysql.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmingTaskServiceImpl implements FarmingTaskService {

    @Resource
    private FarmingTaskMapper farmingTaskMapper;

    @Resource
    private GreenhouseMapper greenhouseMapper;

    @Resource
    private FarmingTaskCompletionMapper taskCompletionMapper;

    @Resource
    private FarmingStatisticsDailyMapper statisticsDailyMapper;

    @Override
    @Transactional
    public void createFarmingTask(Long userId, FarmingTaskCreateReqVO reqVO) {
        FarmingTaskDO task = FarmingTaskDO.builder()
                .greenhouseId(reqVO.getGreenhouseId())
                .taskType(reqVO.getTaskType())
                .taskTime(reqVO.getTaskTime() != null ? reqVO.getTaskTime() : LocalDateTime.now())
                .dosage(reqVO.getDosage())
                .operatorId(userId)
                .remark(reqVO.getRemark())
                .createTime(LocalDateTime.now())
                .build();
        farmingTaskMapper.insert(task);

        // 更新日统计
        LocalDate date = task.getTaskTime().toLocalDate();
        updateDailyStatistics(reqVO.getGreenhouseId(), date, reqVO.getTaskType());
    }

    @Override
    public List<FarmingTaskRespVO> getFarmingTasks(Long userId, Long greenhouseId, String taskType,
                                                   LocalDateTime startTime, LocalDateTime endTime) {
        List<FarmingTaskDO> tasks;
        if (greenhouseId != null) {
            if (startTime != null && endTime != null) {
                tasks = farmingTaskMapper.selectListByGreenhouseIdAndTimeRange(greenhouseId, startTime, endTime);
            } else if (taskType != null) {
                tasks = farmingTaskMapper.selectListByGreenhouseIdAndType(greenhouseId, taskType);
            } else {
                tasks = farmingTaskMapper.selectListByGreenhouseId(greenhouseId);
            }
        } else {
            // 获取用户所有大棚的记录
            List<Long> greenhouseIds = getUserGreenhouseIds(userId);
            tasks = farmingTaskMapper.selectListByGreenhouseIds(greenhouseIds, startTime, endTime);
        }

        // 转换为 VO
        return tasks.stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    public FarmingStatisticsRespVO getStatistics(Long userId) {
        List<Long> greenhouseIds = getUserGreenhouseIds(userId);
        if (greenhouseIds.isEmpty()) {
            FarmingStatisticsRespVO empty = new FarmingStatisticsRespVO();
            empty.setTodayCount(0);
            empty.setYesterdayCount(0);
            empty.setWeekCount(0);
            empty.setWeekTrend(new ArrayList<>());
            return empty;
        }

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 获取本周统计数据
        List<FarmingTaskCompletionDO> weekCompletions = taskCompletionMapper
                .selectListByGreenhouseIdsAndDateRange(greenhouseIds, weekStart, today);

        // 今日完成数
        long todayCount = weekCompletions.stream()
                .filter(c -> c.getCompletionDate().equals(today))
                .count();

        // 昨日完成数
        long yesterdayCount = weekCompletions.stream()
                .filter(c -> c.getCompletionDate().equals(yesterday))
                .count();

        // 本周总数
        int weekCount = weekCompletions.size();

        // 本周每日趋势
        Map<LocalDate, Long> dailyMap = weekCompletions.stream()
                .collect(Collectors.groupingBy(FarmingTaskCompletionDO::getCompletionDate, Collectors.counting()));

        List<FarmingStatisticsRespVO.DailyTrend> weekTrend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (LocalDate date = weekStart; !date.isAfter(today); date = date.plusDays(1)) {
            FarmingStatisticsRespVO.DailyTrend trend = new FarmingStatisticsRespVO.DailyTrend();
            trend.setDate(date.format(formatter));
            trend.setCount(dailyMap.getOrDefault(date, 0L).intValue());
            weekTrend.add(trend);
        }

        FarmingStatisticsRespVO result = new FarmingStatisticsRespVO();
        result.setTodayCount((int) todayCount);
        result.setYesterdayCount((int) yesterdayCount);
        result.setWeekCount(weekCount);
        result.setWeekTrend(weekTrend);
        return result;
    }

    private List<Long> getUserGreenhouseIds(Long userId) {
        List<GreenhouseDO> greenhouses = greenhouseMapper.selectListByOwnerId(userId);
        if (greenhouses.isEmpty()) {
            greenhouses = greenhouseMapper.selectListByManagerId(userId);
        }
        if (greenhouses.isEmpty()) {
            greenhouses = greenhouseMapper.selectAll();
        }
        return greenhouses.stream().map(GreenhouseDO::getId).collect(Collectors.toList());
    }

    private FarmingTaskRespVO convertToRespVO(FarmingTaskDO task) {
        FarmingTaskRespVO vo = new FarmingTaskRespVO();
        vo.setId(task.getId());
        vo.setGreenhouseId(task.getGreenhouseId());
        vo.setTaskType(task.getTaskType());
        vo.setTaskTime(task.getTaskTime());
        vo.setDosage(task.getDosage());
        vo.setOperatorId(task.getOperatorId());
        vo.setRemark(task.getRemark());
        vo.setCreateTime(task.getCreateTime());

        // 获取大棚名称
        GreenhouseDO greenhouse = greenhouseMapper.selectById(task.getGreenhouseId());
        if (greenhouse != null) {
            vo.setGreenhouseName(greenhouse.getName());
        }
        return vo;
    }

    private void updateDailyStatistics(Long greenhouseId, LocalDate date, String taskType) {
        FarmingStatisticsDailyDO stat = statisticsDailyMapper.selectByGreenhouseAndDateAndType(
                greenhouseId, date, taskType);
        if (stat != null) {
            stat.setCompletionCount(stat.getCompletionCount() + 1);
            stat.setUpdateTime(LocalDateTime.now());
            statisticsDailyMapper.updateById(stat);
        } else {
            FarmingStatisticsDailyDO newStat = FarmingStatisticsDailyDO.builder()
                    .greenhouseId(greenhouseId)
                    .statDate(date)
                    .taskType(taskType)
                    .completionCount(1)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            statisticsDailyMapper.insert(newStat);
        }
    }
}
