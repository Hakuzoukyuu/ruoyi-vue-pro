package cn.iocoder.yudao.module.tomato.controller.admin.sop;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.GreenhouseRespVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.GreenhouseStageChangeReqVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.GreenhouseStageLogDO;
import cn.iocoder.yudao.module.tomato.service.sop.GreenhouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/tomato/greenhouse")
@Tag(name = "大棚管理")
public class GreenhouseController {

    @Resource
    private GreenhouseService greenhouseService;

    @GetMapping("/list")
    @Operation(summary = "获取大棚列表")
    public CommonResult<List<GreenhouseRespVO>> getGreenhouseList() {
        List<GreenhouseRespVO> list = greenhouseService.getGreenhouseList(getLoginUserId());
        return success(list);
    }

    @GetMapping("/detail")
    @Operation(summary = "获取大棚详情")
    public CommonResult<GreenhouseRespVO> getGreenhouseDetail(
            @Parameter(description = "大棚ID") @RequestParam Long greenhouseId) {
        GreenhouseRespVO detail = greenhouseService.getGreenhouseDetail(greenhouseId);
        return success(detail);
    }

    @PostMapping("/change-stage")
    @Operation(summary = "切换大棚生长期")
    public CommonResult<Boolean> changeStage(@RequestBody GreenhouseStageChangeReqVO reqVO) {
        greenhouseService.changeStage(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/stage-history")
    @Operation(summary = "获取生长期变更历史")
    public CommonResult<List<GreenhouseStageLogDO>> getStageHistory(
            @Parameter(description = "大棚ID") @RequestParam Long greenhouseId) {
        List<GreenhouseStageLogDO> list = greenhouseService.getStageHistory(greenhouseId);
        return success(list);
    }

    private Long getLoginUserId() {
        return WebFrameworkUtils.getLoginUserId();
    }
}
