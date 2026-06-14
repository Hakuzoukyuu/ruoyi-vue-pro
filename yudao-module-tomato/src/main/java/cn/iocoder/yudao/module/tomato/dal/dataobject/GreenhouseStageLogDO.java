package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("greenhouse_stage_log")
@KeySequence("greenhouse_stage_log_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreenhouseStageLogDO {

    @TableId
    private Long id;

    private Long greenhouseId;

    private Integer stage;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long operatorId;

    private LocalDateTime createTime;
}
