package cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo;

import lombok.Data;

@Data
public class AiDiagnosisDetectReqVO {

    private Long greenhouseId;

    private Double temperature;

    private Double humidity;

}
