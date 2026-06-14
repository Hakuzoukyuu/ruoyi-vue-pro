package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("farming_statistics_daily")
@KeySequence("farming_statistics_daily_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingStatisticsDailyDO {

    @TableId
    private Long id;

    private Long greenhouseId;

    private LocalDate statDate;

    private String taskType;

    private Integer completionCount;

    private BigDecimal totalDosage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
