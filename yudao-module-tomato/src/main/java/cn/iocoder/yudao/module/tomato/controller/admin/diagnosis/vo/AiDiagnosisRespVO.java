package cn.iocoder.yudao.module.tomato.controller.admin.diagnosis.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiDiagnosisRespVO {

    private Long id;

    private Long userId;

    private Long greenhouseId;

    private String imageUrl;

    private String diseaseName;

    private Float confidence;

    private String resultJson;

    private JSONObject resultJsonObj;

    private Integer isHelpful;

    private LocalDateTime createdAt;

}
