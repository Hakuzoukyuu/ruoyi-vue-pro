package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 自定义提醒创建请求 VO
 */
@Data
public class FarmingCustomReminderCreateReqVO {

    /** 大棚ID */
    private Long greenhouseId;

    /** 提醒名称 */
    private String reminderName;

    /** 提醒内容 */
    private String reminderContent;

    /** 提醒日期 */
    private LocalDate reminderDate;

    /** 备注 */
    private String remark;
}
