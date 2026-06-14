package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 待办提醒项 Response VO
 */
@Data
public class SopTodoItemRespVO {

    /** 模板ID */
    private Long templateId;

    /** 生长期 */
    private Integer stage;

    /** 任务类型 */
    private String taskType;

    /** 任务名称 */
    private String taskName;

    /** 建议内容 */
    private String suggestContent;

    /** 建议用量 */
    private String suggestDosage;

    /** 优先级：1-高 2-中 3-低 */
    private Integer priority;

    /** 状态: pending-待完成 overdue-逾期 completed-已完成 */
    private String status;

    /** 逾期天数 (0表示未逾期) */
    private Integer overdueDays;

    /** 建议开始日期 */
    private LocalDate suggestStartDate;

    /** 建议结束日期 */
    private LocalDate suggestEndDate;
}
