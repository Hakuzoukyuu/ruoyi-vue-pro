package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("farming_suggestion")
@KeySequence("farming_suggestion_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmingSuggestionDO {

    @TableId
    private Long id;

    private Integer stage;

    private String suggestion;

    private String updateBy;

    private LocalDateTime updateTime;
}
