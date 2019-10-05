package codes.nopain.nopain.app.controller.controllers.dashboard;

import codes.nopain.nopain.app.config.ScheduleProperties;
import codes.nopain.nopain.app.controller.exception.NoContentException;
import codes.nopain.nopain.app.controller.pojo.dashboard.DashboardEvent;
import codes.nopain.nopain.app.controller.pojo.dashboard.DashboardResponse;
import codes.nopain.nopain.app.database.document.UserSchedule;
import codes.nopain.nopain.app.database.document.UserSetting;
import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.database.pojo.schedule.DayOfWeekData;
import codes.nopain.nopain.app.database.pojo.schedule.PeriodOfDay;
import codes.nopain.nopain.app.database.pojo.schedule.WeekData;
import codes.nopain.nopain.app.database.repository.UserSchedulesRepository;
import codes.nopain.nopain.app.database.repository.UserSettingsRepository;
import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.entity.TimeDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import codes.nopain.nopain.app.worker.schedule.spreadsheet.entity.Spreadsheet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final UserSchedulesRepository schedulesRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final ScheduleProperties scheduleProperties;

    @GetMapping("/api/schedule/dashboard")
    public DashboardResponse getDashboardDate(Principal principal) {
        UserSchedule userSchedule = schedulesRepository.findByEmail(principal.getName())
                .orElseThrow(NoContentException::new);
        UserSetting userSetting = userSettingsRepository.findByEmail(principal.getName())
                .orElseThrow(NoContentException::new);

        Spreadsheet spreadsheet = userSchedule.getSpreadsheet();
        Map<String, String> colorMap = new HashMap<>();
        int totalPeriod = 0;
        List<DashboardEvent> events = new ArrayList<>();

        for (ClassTerm classTerm : spreadsheet.getSheets()) {
            DateDuration termDuration = classTerm.getDuration();
            WeekData weekData = classTerm.getWeekData();

            for (Weekday weekday : Weekday.values()) {
                DayOfWeekData dayOfWeekData = weekData.getData(weekday);
                List<PeriodOfDay> periodOfDays = dayOfWeekData.getData();

                for (PeriodOfDay periodOfDay : periodOfDays) {
                    totalPeriod += periodOfDay.getPeriodRange().getEnd() - periodOfDay.getPeriodRange().getStart() + 1;
                    LocalDate date = getDate(weekday, termDuration.getStart());

                    while (date.compareTo(termDuration.getEnd()) <= 0) {
                        String[] times = getTimes(userSetting, date, periodOfDay.getPeriodRange());
                        events.add(
                                DashboardEvent.builder()
                                        .name(periodOfDay.getSubject())
                                        .color(getSubjectColor(colorMap, periodOfDay.getClassname()))
                                        .start(times[0])
                                        .end(times[1])
                                        .classname(periodOfDay.getClassname())
                                        .classId(periodOfDay.getClassId())
                                        .startPeriod(periodOfDay.getPeriodRange().getStart())
                                        .endPeriod(periodOfDay.getPeriodRange().getEnd())
                                        .classroom(periodOfDay.getClassroom())
                                        .build()
                        );

                        date = date.plusDays(7);
                    }
                }
            }
        }

        LocalDate startTerm = spreadsheet.getSheets().get(0).getDuration().getStart();
        LocalDate endTerm = spreadsheet.getSheets().get(spreadsheet.getSheets().size() - 1).getDuration().getEnd();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        long weeks = ChronoUnit.WEEKS.between(startTerm, endTerm) + 1;

        int avgPerWeek = Math.toIntExact(totalPeriod / weeks);

        return DashboardResponse.builder()
                .registered(userSchedule.getRegisteredClasses().size())
                .avgPerWeek(avgPerWeek)
                .startTerm(dtf.format(startTerm))
                .endTerm(dtf.format(endTerm))
                .semester(getSemester(userSchedule.getRegisteredClasses().get(0).getSemester()))
                .totalPeriod(totalPeriod)
                .events(events)
                .build();

    }

    private String getSemester(String ori) {
        String[] arr = ori.split("_");
        return String.format("Học kỳ %s %s - %s", arr[0], arr[1], arr[2]);
    }

    private LocalDate getDate(Weekday weekday, LocalDate startTerm) {
        return startTerm.plusDays(weekday.getId() - 1);
    }

    private String[] getTimes(UserSetting userSetting, LocalDate startDate, ClassPeriodRange periodRange) {
        DateTimeFormatter iso = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TimeDuration d1 = userSetting.getPeriodDurationMap().get(periodRange.getStart());
        TimeDuration d2 = userSetting.getPeriodDurationMap().get(periodRange.getEnd());
        LocalTime startPeriod = d1.getStart();
        LocalTime endPeriod = d2.getEnd();

        return new String[]{
                LocalDateTime.of(startDate, startPeriod).format(iso),
                LocalDateTime.of(startDate, endPeriod).format(iso)
        };
    }

    private String getShortenSubject(String subject) {
        String[] arr = subject.split(" ");
        StringBuilder res = new StringBuilder();

        for (String w : arr) {
            res.append(w.charAt(0));
        }

        return res.toString().toUpperCase();
    }

    private String getSubjectColor(Map<String, String> colorMap, String subject) {
        String color = colorMap.get(subject);
        Random random = new Random();
        List<String> colors = scheduleProperties.getColors();

        if (color == null) {
            boolean exist;

            do {
                color = colors.get(random.nextInt(colors.size()));
                exist = false;

                for (String usedColor : colorMap.values()) {
                    if (color.equals(usedColor)) {
                        exist = true;
                        break;
                    }
                }
            } while (exist);

            colorMap.put(subject, color);
        }

        return color;
    }
}
