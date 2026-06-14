package cn.iocoder.yudao.module.tomato.controller.admin.diagnosis;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo.AiDiagnosisDetectReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo.AiDiagnosisRespVO;
import cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo.AiSupportedDiseaseRespVO;
import cn.iocoder.yudao.module.tomato.service.AiDiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/tomato/diagnosis")
@Tag(name = "番茄病害诊断管理")
public class AiDiagnosisController {

    @Resource
    private AiDiagnosisService diagnosisService;

    @PostMapping("/detect")
    @Operation(summary = "检测病害")
    public CommonResult<AiDiagnosisRespVO> detectDisease(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "greenhouseId", required = false) Long greenhouseId,
            @RequestParam(value = "temperature", required = false) Double temperature,
            @RequestParam(value = "humidity", required = false) Double humidity) {
        AiDiagnosisDetectReqVO reqVO = new AiDiagnosisDetectReqVO();
        reqVO.setGreenhouseId(greenhouseId);
        reqVO.setTemperature(temperature);
        reqVO.setHumidity(humidity);
        AiDiagnosisRespVO result = diagnosisService.detectDisease(getLoginUserId(), file, reqVO);
        return success(result);
    }

    @GetMapping("/history")
    @Operation(summary = "获取诊断历史")
    public CommonResult<List<AiDiagnosisRespVO>> getDiagnosisHistory() {
        List<AiDiagnosisRespVO> list = diagnosisService.getDiagnosisHistory(getLoginUserId());
        return success(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取诊断记录详情")
    public CommonResult<AiDiagnosisRespVO> getDiagnosisRecord(
            @Parameter(description = "诊断记录ID") @PathVariable Long id) {
        AiDiagnosisRespVO result = diagnosisService.getDiagnosisRecord(getLoginUserId(), id);
        return success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除诊断记录")
    public CommonResult<Boolean> deleteDiagnosisRecord(
            @Parameter(description = "诊断记录ID") @PathVariable Long id) {
        diagnosisService.deleteDiagnosisRecord(getLoginUserId(), id);
        return success(true);
    }

    @PutMapping("/{id}/helpful")
    @Operation(summary = "评价诊断结果")
    public CommonResult<Boolean> updateHelpful(
            @Parameter(description = "诊断记录ID") @PathVariable Long id,
            @Parameter(description = "是否有帮助: 1有用 0无用") @RequestParam Integer isHelpful) {
        diagnosisService.updateHelpful(getLoginUserId(), id, isHelpful);
        return success(true);
    }

    @GetMapping("/greenhouse/{greenhouseId}")
    @Operation(summary = "获取大棚诊断记录")
    public CommonResult<List<AiDiagnosisRespVO>> getDiagnosisByGreenhouse(
            @Parameter(description = "大棚ID") @PathVariable Long greenhouseId) {
        List<AiDiagnosisRespVO> list = diagnosisService.getDiagnosisByGreenhouse(greenhouseId);
        return success(list);
    }

    @GetMapping("/supported-diseases")
    @Operation(summary = "获取支持识别的病害类型")
    public CommonResult<List<AiSupportedDiseaseRespVO>> getSupportedDiseases() {
        List<AiSupportedDiseaseRespVO> diseases = Arrays.asList(
            createDisease("healthy", "健康", "植株生长正常"),
            createDisease("Target_Spot", "靶斑病", "由真菌引起的叶片病害"),
            createDisease("YellowLeaf_Curl_Virus", "黄化曲叶病毒", "由病毒引起的病害"),
            createDisease("Bacterial_spot", "细菌性斑点病", "由细菌引起的病害"),
            createDisease("Early_blight", "早疫病", "由链格孢菌引起"),
            createDisease("Late_blight", "晚疫病", "由疫霉菌引起"),
            createDisease("Leaf_Mold", "叶霉病", "由黄枝孢菌引起"),
            createDisease("Septoria_leaf_spot", "针壳孢叶斑病", "由真菌引起"),
            createDisease("Spider_mites", "蜘蛛螨", "螨虫类害虫"),
            createDisease("灰霉病", "灰霉病", "由灰葡萄孢菌引起"),
            createDisease("白粉病", "白粉病", "由白粉菌引起"),
            createDisease("病毒病", "病毒病", "由多种病毒引起"),
            createDisease("蚜虫", "蚜虫", "吸食性害虫"),
            createDisease("白粉虱", "白粉虱", "吸食性害虫"),
            createDisease("斑潜蝇", "斑潜蝇", "潜叶性害虫")
        );
        return success(diseases);
    }

    @GetMapping("/health-check")
    @Operation(summary = "健康检查")
    public CommonResult<String> healthCheck() {
        return success("OK");
    }

    private AiSupportedDiseaseRespVO createDisease(String code, String name, String description) {
        AiSupportedDiseaseRespVO vo = new AiSupportedDiseaseRespVO();
        vo.setCode(code);
        vo.setName(name);
        vo.setDescription(description);
        return vo;
    }

    private Long getLoginUserId() {
        return WebFrameworkUtils.getLoginUserId();
    }

}
