package codes.nopain.nopain.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("wit")
@Getter
@Setter
public class WitProperties {
    private String accessToken;
}
