package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("farming_task")
@KeySequence("farming_task_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingTaskDO {

    @TableId
    private Long id;

    private Long greenhouseId;

    private String taskType;

    private LocalDateTime taskTime;

    private String dosage;

    private Long operatorId;

    private String remark;

    private LocalDateTime createTime;
}
