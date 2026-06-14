package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("greenhouse")
@KeySequence("greenhouse_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreenhouseDO {

    @TableId
    private Long id;

    private String code;

    private String name;

    private BigDecimal area;

    private String variety;

    private LocalDate plantingDate;

    private Integer currentStage;

    private BigDecimal totalYield;

    private LocalDate harvestEndDate;

    private Long ownerId;

    private Long managerId;

    private String notes;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;
}
