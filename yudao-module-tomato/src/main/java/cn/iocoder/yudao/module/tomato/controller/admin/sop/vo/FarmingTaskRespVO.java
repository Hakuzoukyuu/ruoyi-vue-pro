package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 农事记录 Response VO
 */
@Data
public class FarmingTaskRespVO {

    private Long id;

    private Long greenhouseId;

    private String greenhouseName;

    private String taskType;

    private LocalDateTime taskTime;

    private String dosage;

    private Long operatorId;

    private String remark;

    private LocalDateTime createTime;
}
