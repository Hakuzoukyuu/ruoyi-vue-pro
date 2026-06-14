package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 农事记录创建请求 VO
 */
@Data
public class FarmingTaskCreateReqVO {

    /** 大棚ID */
    private Long greenhouseId;

    /** 任务类型 */
    private String taskType;

    /** 作业时间 */
    private LocalDateTime taskTime;

    /** 用量/浓度 */
    private String dosage;

    /** 备注 */
    private String remark;
}
