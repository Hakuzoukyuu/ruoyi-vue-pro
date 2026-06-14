package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

/**
 * 完成任务请求 VO
 */
@Data
public class SopCompleteTaskReqVO {

    /** 大棚ID */
    private Long greenhouseId;

    /** 模板ID */
    private Long templateId;

    /** 用量/浓度 */
    private String dosage;

    /** 备注 */
    private String remark;
}
