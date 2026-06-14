package cn.iocoder.yudao.module.tomato.controller.admin.sop;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingCustomReminderCreateReqVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingCustomReminderDO;
import cn.iocoder.yudao.module.tomato.service.sop.FarmingCustomReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/tomato/custom-reminder")
@Tag(name = "用户自定义提醒")
public class FarmingCustomReminderController {

    @Resource
    private FarmingCustomReminderService farmingCustomReminderService;

    @PostMapping("/create")
    @Operation(summary = "创建自定义提醒")
    public CommonResult<Boolean> createReminder(@RequestBody FarmingCustomReminderCreateReqVO reqVO) {
        farmingCustomReminderService.createReminder(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/complete")
    @Operation(summary = "标记提醒为已完成")
    public CommonResult<Boolean> completeReminder(
            @Parameter(description = "提醒ID") @RequestParam Long id) {
        farmingCustomReminderService.completeReminder(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取大棚所有自定义提醒")
    public CommonResult<List<FarmingCustomReminderDO>> getReminderList(
            @Parameter(description = "大棚ID") @RequestParam Long greenhouseId) {
        return success(farmingCustomReminderService.getRemindersByGreenhouse(greenhouseId));
    }

    @GetMapping("/pending")
    @Operation(summary = "获取大棚未完成的自定义提醒")
    public CommonResult<List<FarmingCustomReminderDO>> getPendingReminders(
            @Parameter(description = "大棚ID") @RequestParam Long greenhouseId) {
        return success(farmingCustomReminderService.getPendingReminders(greenhouseId));
    }

    private Long getLoginUserId() {
        return WebFrameworkUtils.getLoginUserId();
    }
}
