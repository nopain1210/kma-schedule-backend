package codes.nopain.nopain.app.worker.global.utils;

import codes.nopain.nopain.app.database.document.ClassSchedule;
import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;

import java.time.LocalDate;
import java.util.List;

public class DateUtils {
    public static boolean inside(DateDuration parent, DateDuration child) {
        LocalDate pStart = parent.getStart();
        LocalDate pEnd = parent.getEnd();
        LocalDate cStart = child.getStart();
        LocalDate cEnd = child.getEnd();

        return pStart.compareTo(cStart) <= 0 && pEnd.compareTo(cEnd) >= 0;
    }

    public static DateDuration getDuration(List<ClassSchedule> classList) {
        LocalDate start = LocalDate.of(3000, 1, 1);
        LocalDate end = LocalDate.of(2000, 1, 1);

        for (ClassSchedule classSchedule : classList) {
            for (ClassTerm term : classSchedule.getTerms()) {
                LocalDate termStart = term.getDuration().getStart();
                LocalDate termEnd = term.getDuration().getEnd();

                if (termStart.compareTo(start) <= 0) {
                    start = termStart;
                }

                if (termEnd.compareTo(end) >= 0) {
                    end = termEnd;
                }
            }
        }

        return DateDuration.builder()
                .start(start)
                .end(end)
                .build();
    }

}
