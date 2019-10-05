package codes.nopain.nopain.app.worker.schedule.spreadsheet.builder;

import codes.nopain.nopain.app.controller.exception.NoContentException;
import codes.nopain.nopain.app.database.document.ClassSchedule;
import codes.nopain.nopain.app.database.document.UserSchedule;
import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.database.repository.UserSchedulesRepository;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.utils.DateUtils;
import codes.nopain.nopain.app.worker.schedule.spreadsheet.entity.Spreadsheet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SpreadsheetBuilder {
    private final UserSchedulesRepository schedulesRepository;

    private UserSchedule userSchedule;

    public SpreadsheetBuilder user(String email) {
        this.userSchedule = schedulesRepository.findByEmail(email)
                .orElseThrow(NoContentException::new);

        return this;
    }

    public void buildSpreadsheet() {
        Spreadsheet spreadsheet = buildWeekSpreadsheet();
        compressSpreadsheet(spreadsheet);
        userSchedule.setSpreadsheet(spreadsheet);

        userSchedule = schedulesRepository.save(userSchedule);
    }

    private void compressSpreadsheet(Spreadsheet spreadsheet) {
        List<ClassTerm> dstTerms = new ArrayList<>();
        List<ClassTerm> srcTerms = spreadsheet.getSheets();

        for (int idx = 0; idx < srcTerms.size(); idx++) {
            int start = idx;

            if (start < srcTerms.size() - 1) {
                while (srcTerms.get(idx).isDataEqual(srcTerms.get(idx + 1))) {
                    idx++;

                    if (idx > srcTerms.size() - 1) {
                        break;
                    }
                }
            }

            int end = idx;
            ClassTerm startTerm = srcTerms.get(start);
            ClassTerm endTerm = srcTerms.get(end);
            dstTerms.add(
                    ClassTerm.builder()
                            .duration(
                                    DateDuration.builder()
                                            .start(startTerm.getDuration().getStart())
                                            .end(endTerm.getDuration().getEnd())
                                            .build()
                            )
                            .weekData(startTerm.getWeekData())
                            .build()
            );
        }

        spreadsheet.setSheets(dstTerms);
    }

    private Spreadsheet buildWeekSpreadsheet() {
        List<ClassSchedule> classList = userSchedule.getRegisteredClasses();

        DateDuration semesterDuration = DateUtils.getDuration(classList);
        Spreadsheet spreadsheet = new Spreadsheet();

        for (LocalDate then = semesterDuration.getStart();
             then.compareTo(semesterDuration.getEnd()) <= 0;
             then = then.plusDays(7)) {
            DateDuration weekDuration = new DateDuration(then, then.plusDays(6));
            ClassTerm weekTerm = ClassTerm.builder()
                    .duration(weekDuration)
                    .build();

            for (ClassSchedule classSchedule : classList) {
                for (ClassTerm term : classSchedule.getTerms()) {
                    DateDuration duration = term.getDuration();

                    if (DateUtils.inside(duration, weekDuration)) {
                        weekTerm.getWeekData().putAllData(term.getWeekData());
                    }
                }
            }

            weekTerm.getWeekData().sortData();
            spreadsheet.getSheets().add(weekTerm);
        }

        return spreadsheet;
    }
}
