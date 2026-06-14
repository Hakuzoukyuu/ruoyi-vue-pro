package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

/**
 * 生长期切换请求 VO
 */
@Data
public class GreenhouseStageChangeReqVO {

    /** 大棚ID */
    private Long greenhouseId;

    /** 目标生长期 1-6 */
    private Integer targetStage;

    /** 变更原因备注 */
    private String reason;
}
