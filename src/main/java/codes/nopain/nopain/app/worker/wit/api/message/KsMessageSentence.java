package codes.nopain.nopain.app.worker.wit.api.message;

import codes.nopain.nopain.app.database.pojo.schedule.PeriodOfDay;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class KsMessageSentence {
    private Weekday weekday;
    @Builder.Default
    private List<PeriodOfDay> periods = new ArrayList<>();
}
