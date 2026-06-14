package cn.iocoder.yudao.module.ai.controller.admin.tomato.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 支持的病害类型响应 VO")
public class AiTomatoSupportedDiseaseRespVO {

    @Schema(description = "病害名称", example = "晚疫病")
    private String name;

    @Schema(description = "病害编码", example = "Late_blight")
    private String code;

    @Schema(description = "病害描述", example = "由疫霉菌引起的病害")
    private String description;

}
