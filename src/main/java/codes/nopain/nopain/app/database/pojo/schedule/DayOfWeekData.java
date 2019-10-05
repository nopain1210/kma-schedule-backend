package codes.nopain.nopain.app.database.pojo.schedule;

import codes.nopain.nopain.app.worker.global.enums.Weekday;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class DayOfWeekData {
    private Weekday weekday;

    @Builder.Default
    @JsonProperty("data")
    private List<PeriodOfDay> data = new ArrayList<>();

    public void sort() {
        Collections.sort(data);
    }

    public boolean isEqual(DayOfWeekData o) {
        if (!this.getWeekday().equals(o.getWeekday())) {
            return false;
        }

        if (this.getData().size() != o.getData().size()) {
            return false;
        }

        for (PeriodOfDay c1 : this.getData()) {
            boolean notExist = true;

            for (PeriodOfDay c2 : o.getData()) {
                if (c1.isEqual(c2)) {
                    notExist = false;
                    break;
                }
            }

            if (notExist) {
                return false;
            }
        }

        return true;
    }
}
