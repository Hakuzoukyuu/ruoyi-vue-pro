package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("farming_custom_reminder")
@KeySequence("farming_custom_reminder_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingCustomReminderDO {

    @TableId
    private Long id;

    private Long greenhouseId;

    private String reminderName;

    private String reminderContent;

    private LocalDate reminderDate;

    private Integer isCompleted;

    private String remark;

    private Long createBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
