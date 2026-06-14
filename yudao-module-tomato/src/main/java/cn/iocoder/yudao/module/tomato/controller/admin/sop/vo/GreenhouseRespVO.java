package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 大棚信息 Response VO
 */
@Data
public class GreenhouseRespVO {

    private Long id;

    private String code;

    private String name;

    private BigDecimal area;

    private String variety;

    private LocalDate plantingDate;

    private Integer currentStage;

    private String currentStageName;

    private BigDecimal totalYield;

    private Long ownerId;

    private Long managerId;

    private String notes;

    /** 当前生长期已进入天数 */
    private Integer stageDays;

    /** 静态农事建议 */
    private String suggestion;
}
