package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("farming_sop_template")
@KeySequence("farming_sop_template_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingSopTemplateDO {

    @TableId
    private Long id;

    private Integer stage;

    private String taskType;

    private String taskName;

    private Integer suggestDaysStart;

    private Integer suggestDaysEnd;

    private String suggestContent;

    private String suggestDosage;

    private Integer priority;

    private Integer isSystem;

    private Integer isEnabled;

    private Integer sortOrder;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;
}
