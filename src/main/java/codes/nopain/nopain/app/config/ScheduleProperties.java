package codes.nopain.nopain.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("kma-schedule")
@Getter
@Setter
public class ScheduleProperties {
    private List<String> colors;
    private String defaultAuthor;
    private String defaultGroup;
}
