package codes.nopain.nopain.app.database.pojo.schedule;

import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PeriodOfDay implements Comparable<PeriodOfDay> {
    private ClassPeriodRange periodRange;
    private String classroom;
    private String classId;
    private String subject;
    private String classname;
    private String teacher;

    public boolean isEqual(PeriodOfDay o) {
        return this.getPeriodRange().compareTo(o.getPeriodRange()) == 0
                && this.classroom.equals(o.getClassroom())
                && this.classId.equals(o.getClassId());
    }

    @Override
    public int compareTo(PeriodOfDay o) {
        return this.getPeriodRange().getStart() - o.getPeriodRange().getStart();
    }
}
