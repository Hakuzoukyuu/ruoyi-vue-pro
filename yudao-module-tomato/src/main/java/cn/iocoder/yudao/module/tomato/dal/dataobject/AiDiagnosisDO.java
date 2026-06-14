package cn.iocoder.yudao.module.tomato.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("ai_diagnosis")
@KeySequence("ai_diagnosis_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiDiagnosisDO {

    @TableId
    private Long id;

    private Long userId;

    private Long greenhouseId;

    private String imageUrl;

    private String diseaseName;

    private Float confidence;

    private String resultJson;

    private Integer isHelpful;

    private LocalDateTime createdAt;

}
