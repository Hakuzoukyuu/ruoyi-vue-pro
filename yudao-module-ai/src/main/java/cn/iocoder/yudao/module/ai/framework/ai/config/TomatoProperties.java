package cn.iocoder.yudao.module.ai.framework.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "yudao.ai")
public class TomatoProperties {

    private Tomato tomato = new Tomato();
    
    private DeepSeek deepseek = new DeepSeek();

    @Data
    public static class Tomato {
        private String apiUrl = "http://127.0.0.1:5000";
    }

    @Data
    public static class DeepSeek {
        private String apiKey = "";
        private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
    }

}
