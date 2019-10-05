package codes.nopain.nopain.app.database.pojo.schedule;

import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClassTerm {
    private DateDuration duration;

    @Builder.Default
    @JsonProperty("data")
    private WeekData weekData = new WeekData();

    public int compareToByDuration(ClassTerm o) {
        int startCompare = this.getDuration().getStart().compareTo(o.getDuration().getStart());

        if (startCompare != 0) {
            return startCompare;
        }

        return this.getDuration().getEnd().compareTo(o.getDuration().getEnd());
    }

    public boolean isDataEqual(ClassTerm o) {
        return this.getWeekData().isEqual(o.getWeekData());
    }
}
