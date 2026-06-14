package cn.iocoder.yudao.module.tomato.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 番茄模块配置属性
 *
 * 主应用类已通过 scanBasePackages 扫描 cn.iocoder.yudao.module，
 * 因此不需要额外的 @ComponentScan 或 spring.factories。
 */
@Configuration
@ConfigurationProperties(prefix = "yudao.tomato")
@Data
public class TomatoConfig {

    private String apiUrl = "http://127.0.0.1:5000";

    private DeepSeekConfig deepseek = new DeepSeekConfig();

    @Data
    public static class DeepSeekConfig {
        private String apiKey = "";
        private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
    }

}