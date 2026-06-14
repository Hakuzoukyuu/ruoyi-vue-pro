package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.SopCompleteTaskReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.SopTodoItemRespVO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmingSopServiceImpl implements FarmingSopService {

    @Resource
    private FarmingSopTemplateMapper sopTemplateMapper;

    @Resource
    private FarmingTaskCompletionMapper taskCompletionMapper;

    @Resource
    private FarmingTaskMapper farmingTaskMapper;

    @Resource
    private GreenhouseMapper greenhouseMapper;

    @Resource
    private GreenhouseStageLogMapper stageLogMapper;

    @Resource
    private FarmingStatisticsDailyMapper statisticsDailyMapper;

    @Override
    public List<SopTodoItemRespVO> getTodoList(Long greenhouseId) {
        // 1. 获取大棚当前生长期信息
        GreenhouseDO greenhouse = greenhouseMapper.selectById(greenhouseId);
        if (greenhouse == null) {
            return new ArrayList<>();
        }
        Integer currentStage = greenhouse.getCurrentStage();

        // 2. 获取当前生长期进入天数
        int stageDays = calculateStageDays(greenhouseId);

        // 3. 获取该生长期的所有SOP模板
        List<FarmingSopTemplateDO> templates = sopTemplateMapper.selectListByStage(currentStage);

        // 4. 获取今日已完成的任务
        LocalDate today = LocalDate.now();
        List<FarmingTaskCompletionDO> completions = taskCompletionMapper.selectListByGreenhouseIdAndDate(greenhouseId, today);
        Set<Long> completedTemplateIds = completions.stream()
                .map(FarmingTaskCompletionDO::getTemplateId)
                .collect(Collectors.toSet());

        // 5. 构建待办列表
        List<SopTodoItemRespVO> todoList = new ArrayList<>();
        for (FarmingSopTemplateDO template : templates) {
            SopTodoItemRespVO item = new SopTodoItemRespVO();
            item.setTemplateId(template.getId());
            item.setStage(template.getStage());
            item.setTaskType(template.getTaskType());
            item.setTaskName(template.getTaskName());
            item.setSuggestContent(template.getSuggestContent());
            item.setSuggestDosage(template.getSuggestDosage());
            item.setPriority(template.getPriority());

            // 计算建议日期
            LocalDate stageStartDate = getStageStartDate(greenhouseId);
            Integer startDays = template.getSuggestDaysStart() != null ? template.getSuggestDaysStart() : 1;
            Integer endDays = template.getSuggestDaysEnd() != null ? template.getSuggestDaysEnd() : 999;
            LocalDate suggestStart = stageStartDate.plusDays(startDays - 1);
            LocalDate suggestEnd = stageStartDate.plusDays(endDays - 1);
            item.setSuggestStartDate(suggestStart);
            item.setSuggestEndDate(suggestEnd);

            // 判断状态
            if (completedTemplateIds.contains(template.getId())) {
                item.setStatus("completed");
                item.setOverdueDays(0);
            } else if (today.isAfter(suggestEnd)) {
                item.setStatus("overdue");
                item.setOverdueDays((int) ChronoUnit.DAYS.between(suggestEnd, today));
            } else {
                item.setStatus("pending");
                item.setOverdueDays(0);
            }

            todoList.add(item);
        }

        // 6. 排序：逾期优先 → 高优先级 → 待完成 → 已完成
        todoList.sort(Comparator
                .comparing((SopTodoItemRespVO item) -> getStatusOrder(item.getStatus()))
                .thenComparing(SopTodoItemRespVO::getPriority)
                .thenComparing(SopTodoItemRespVO::getOverdueDays, Comparator.reverseOrder()));

        return todoList;
    }

    @Override
    @Transactional
    public void completeTask(Long userId, SopCompleteTaskReqVO reqVO) {
        Long greenhouseId = reqVO.getGreenhouseId();
        Long templateId = reqVO.getTemplateId();
        LocalDate today = LocalDate.now();

        // 1. 检查是否已完成（去重）
        FarmingTaskCompletionDO existing = taskCompletionMapper.selectByGreenhouseAndTemplateAndDate(
                greenhouseId, templateId, today);
        if (existing != null) {
            return; // 已完成，不重复记录
        }

        // 2. 获取模板信息
        FarmingSopTemplateDO template = sopTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException("SOP模板不存在");
        }

        // 3. 创建农事记录
        FarmingTaskDO farmingTask = FarmingTaskDO.builder()
                .greenhouseId(greenhouseId)
                .taskType(template.getTaskType())
                .taskTime(LocalDateTime.now())
                .dosage(reqVO.getDosage())
                .operatorId(userId)
                .remark(reqVO.getRemark())
                .createTime(LocalDateTime.now())
                .build();
        farmingTaskMapper.insert(farmingTask);

        // 4. 创建完成记录
        FarmingTaskCompletionDO completion = FarmingTaskCompletionDO.builder()
                .greenhouseId(greenhouseId)
                .templateId(templateId)
                .taskType(template.getTaskType())
                .completionDate(today)
                .farmingTaskId(farmingTask.getId())
                .createTime(LocalDateTime.now())
                .build();
        taskCompletionMapper.insert(completion);

        // 5. 更新统计表
        updateDailyStatistics(greenhouseId, today, template.getTaskType());
    }

    @Override
    public List<FarmingSopTemplateDO> getAllTemplates() {
        return sopTemplateMapper.selectAll();
    }

    @Override
    public List<FarmingSopTemplateDO> getTemplatesByStage(Integer stage) {
        return sopTemplateMapper.selectListByStage(stage);
    }

    /**
     * 计算大棚当前生长期已进入天数
     */
    private int calculateStageDays(Long greenhouseId) {
        GreenhouseStageLogDO stageLog = stageLogMapper.selectCurrentByGreenhouseId(greenhouseId);
        if (stageLog != null && stageLog.getStartDate() != null) {
            return (int) ChronoUnit.DAYS.between(stageLog.getStartDate(), LocalDate.now()) + 1;
        }
        return 1;
    }

    /**
     * 获取当前生长期开始日期
     */
    private LocalDate getStageStartDate(Long greenhouseId) {
        GreenhouseStageLogDO stageLog = stageLogMapper.selectCurrentByGreenhouseId(greenhouseId);
        if (stageLog != null && stageLog.getStartDate() != null) {
            return stageLog.getStartDate();
        }
        return LocalDate.now();
    }

    /**
     * 更新日统计
     */
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

    private int getStatusOrder(String status) {
        return switch (status) {
            case "overdue" -> 0;
            case "pending" -> 1;
            case "completed" -> 2;
            default -> 3;
        };
    }
}
