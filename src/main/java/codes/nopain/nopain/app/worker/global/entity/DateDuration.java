package codes.nopain.nopain.app.worker.global.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DateDuration implements Comparable<DateDuration> {
    private LocalDate start;
    private LocalDate end;

    public DateDuration deepClone() {
        return new DateDuration(start, end);
    }

    public boolean isEqual(DateDuration o) {
        return this.getStart().isEqual(o.getStart()) && this.getEnd().isEqual(o.getEnd());
    }

    @Override
    public int compareTo(DateDuration o) {
        return this.getEnd().compareTo(o.getStart());
    }
}
