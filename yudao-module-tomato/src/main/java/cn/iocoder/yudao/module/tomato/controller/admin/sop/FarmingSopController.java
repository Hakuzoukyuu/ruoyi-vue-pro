package cn.iocoder.yudao.module.tomato.controller.admin.sop;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.SopCompleteTaskReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.SopTodoItemRespVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingSopTemplateDO;
import cn.iocoder.yudao.module.tomato.service.sop.FarmingSopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/tomato/sop")
@Tag(name = "农事SOP管理")
public class FarmingSopController {

    @Resource
    private FarmingSopService farmingSopService;

    @GetMapping("/todo-list")
    @Operation(summary = "获取大棚待办提醒列表")
    public CommonResult<List<SopTodoItemRespVO>> getTodoList(
            @Parameter(description = "大棚ID") @RequestParam Long greenhouseId) {
        List<SopTodoItemRespVO> list = farmingSopService.getTodoList(greenhouseId);
        return success(list);
    }

    @PostMapping("/complete")
    @Operation(summary = "完成待办任务")
    public CommonResult<Boolean> completeTask(@RequestBody SopCompleteTaskReqVO reqVO) {
        farmingSopService.completeTask(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/templates")
    @Operation(summary = "获取所有SOP模板")
    public CommonResult<List<FarmingSopTemplateDO>> getAllTemplates() {
        return success(farmingSopService.getAllTemplates());
    }

    @GetMapping("/templates/stage")
    @Operation(summary = "获取指定生长期的SOP模板")
    public CommonResult<List<FarmingSopTemplateDO>> getTemplatesByStage(
            @Parameter(description = "生长期 1-6") @RequestParam Integer stage) {
        return success(farmingSopService.getTemplatesByStage(stage));
    }

    private Long getLoginUserId() {
        return WebFrameworkUtils.getLoginUserId();
    }
}
