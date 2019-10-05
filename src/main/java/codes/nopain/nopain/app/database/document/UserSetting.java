package codes.nopain.nopain.app.database.document;

import codes.nopain.nopain.app.database.pojo.setting.KmaAccount;
import codes.nopain.nopain.app.database.pojo.setting.PeriodDurationMap;
import codes.nopain.nopain.app.worker.global.entity.TimeDuration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Document(collection = "user-settings")
@Getter
@Setter
@Builder
public class UserSetting {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private KmaAccount kmaAccount;

    @Builder.Default
    private PeriodDurationMap periodDurationMap = new PeriodDurationMap();

}
