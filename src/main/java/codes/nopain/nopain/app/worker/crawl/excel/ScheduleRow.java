package codes.nopain.nopain.app.worker.crawl.excel;

import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ScheduleRow {
    private Weekday weekday;
    private String subject;
    private String className;
    private String classroom;
    private String teacher;
    private ClassPeriodRange periodRange;
    private DateDuration dateDuration;

    public boolean isClassNameEqual(ScheduleRow o) {
        return this.getClassName().equals(o.getClassName());
    }

    public boolean isDurationEqual(ScheduleRow o) {
        return this.getDateDuration().getStart().isEqual(o.getDateDuration().getStart())
                && this.getDateDuration().getEnd().isEqual(o.getDateDuration().getEnd());
    }
}
