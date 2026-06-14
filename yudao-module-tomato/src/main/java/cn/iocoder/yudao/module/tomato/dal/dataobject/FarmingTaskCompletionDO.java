package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("farming_task_completion")
@KeySequence("farming_task_completion_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingTaskCompletionDO {

    @TableId
    private Long id;

    private Long greenhouseId;

    private Long templateId;

    private String taskType;

    private LocalDate completionDate;

    private Long farmingTaskId;

    private LocalDateTime createTime;
}
