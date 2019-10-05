package codes.nopain.nopain.app.worker.global.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClassPeriodRange implements Comparable<ClassPeriodRange> {
    private int start, end;

    @Override
    public int compareTo(ClassPeriodRange other) {
        return this.start == other.start && this.end == other.end ? 0
                : this.start - other.start;
    }
}
