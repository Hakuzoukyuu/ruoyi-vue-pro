package cn.iocoder.yudao.module.tomato.controller.admin.sop;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingStatisticsRespVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingTaskCreateReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingTaskRespVO;
import cn.iocoder.yudao.module.tomato.service.sop.FarmingTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/tomato/farming-task")
@Tag(name = "农事记录管理")
public class FarmingTaskController {

    @Resource
    private FarmingTaskService farmingTaskService;

    @PostMapping("/create")
    @Operation(summary = "手动添加农事记录")
    public CommonResult<Boolean> createFarmingTask(@RequestBody FarmingTaskCreateReqVO reqVO) {
        farmingTaskService.createFarmingTask(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "查询农事记录")
    public CommonResult<List<FarmingTaskRespVO>> getFarmingTasks(
            @Parameter(description = "大棚ID") @RequestParam(required = false) Long greenhouseId,
            @Parameter(description = "任务类型") @RequestParam(required = false) String taskType,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<FarmingTaskRespVO> list = farmingTaskService.getFarmingTasks(
                getLoginUserId(), greenhouseId, taskType, startTime, endTime);
        return success(list);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取农事统计看板")
    public CommonResult<FarmingStatisticsRespVO> getStatistics() {
        FarmingStatisticsRespVO statistics = farmingTaskService.getStatistics(getLoginUserId());
        return success(statistics);
    }

    private Long getLoginUserId() {
        return WebFrameworkUtils.getLoginUserId();
    }
}
