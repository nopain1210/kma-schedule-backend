package codes.nopain.nopain.app.controller.controllers.user.registered;

import codes.nopain.nopain.app.database.document.ClassSchedule;
import codes.nopain.nopain.app.controller.exception.NoContentException;
import codes.nopain.nopain.app.controller.pojo.user.add.ClassesByGroupMap;
import codes.nopain.nopain.app.controller.pojo.user.add.ClassesBySubject;
import codes.nopain.nopain.app.controller.pojo.user.registered.ClassPlace;
import codes.nopain.nopain.app.controller.pojo.user.registered.StringTimeRange;
import codes.nopain.nopain.app.controller.pojo.user.registered.ClassInfo;
import codes.nopain.nopain.app.controller.pojo.user.registered.ClassInfoTerm;
import codes.nopain.nopain.app.database.document.UserSchedule;
import codes.nopain.nopain.app.database.document.UserSetting;
import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.database.pojo.schedule.DayOfWeekData;
import codes.nopain.nopain.app.database.pojo.schedule.PeriodOfDay;
import codes.nopain.nopain.app.database.pojo.schedule.WeekData;
import codes.nopain.nopain.app.database.repository.ClassesRepository;
import codes.nopain.nopain.app.database.repository.UserSchedulesRepository;
import codes.nopain.nopain.app.database.repository.UserSettingsRepository;
import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.entity.TimeDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class RegisteredScheduleController {
    private final UserSchedulesRepository schedulesRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final ClassesRepository classesRepository;

    @GetMapping("/api/schedule/classes")
    public ClassesByGroupMap getClassGroup(Principal principal) {
        String email = principal.getName();
        UserSchedule userSchedule = schedulesRepository.findByEmail(email)
                .orElseThrow(NoContentException::new);
        UserSetting userSetting = userSettingsRepository.findByEmail(email)
                .orElseThrow(NoContentException::new);
        List<ClassSchedule> registeredClasses = userSchedule.getRegisteredClasses();
        List<ClassSchedule> allClasses = classesRepository.findAll();
        HashMap<String, HashSet<String>> groupsAndSubjectsMap = new HashMap<>();

        for (ClassSchedule classSchedule : allClasses) {
            String group = classSchedule.getGroup();
            String subject = classSchedule.getSubject();
            groupsAndSubjectsMap.computeIfAbsent(group, k -> new HashSet<>());
            groupsAndSubjectsMap.get(group).add(subject);
        }

        ClassesByGroupMap classesByGroupMap = new ClassesByGroupMap();

        for (String group : groupsAndSubjectsMap.keySet()) {
            ClassesBySubject classesBySubject = new ClassesBySubject();

            for (String subject : groupsAndSubjectsMap.get(group)) {
                List<ClassSchedule> classSchedule = getScheduleByGroupAndSubject(allClasses, group, subject);
                classesBySubject.put(subject, toClassInfoList(userSetting, classSchedule));
            }

            classesByGroupMap.put(group, classesBySubject);
        }

        return classesByGroupMap;
    }

    @GetMapping("/api/schedule/registered")
    public List<ClassInfo> getRegisteredClasses(Principal principal) {
        String email = principal.getName();

        UserSchedule userSchedule = schedulesRepository.findByEmail(email)
                .orElseThrow(NoContentException::new);
        UserSetting userSetting = userSettingsRepository.findByEmail(email)
                .orElseThrow(NoContentException::new);

        return toClassInfoList(userSetting, userSchedule.getRegisteredClasses());
    }

    private List<ClassSchedule> getScheduleByGroupAndSubject(List<ClassSchedule> classSchedules, String group, String subject) {
        List<ClassSchedule> dst = new ArrayList<>();

        for (ClassSchedule classSchedule : classSchedules) {
            if (classSchedule.getGroup().equals(group) && classSchedule.getSubject().equals(subject)) {
                dst.add(classSchedule);
            }
        }

        return dst;
    }

    private List<ClassInfo> toClassInfoList(UserSetting userSetting, List<ClassSchedule> schedule) {
        List<ClassInfo> classInfos = new ArrayList<>();

        for (ClassSchedule classSchedule : schedule) {
            classInfos.add(
                    ClassInfo.builder()
                            .classId(classSchedule.getId())
                            .semester(classSchedule.getSemester())
                            .subject(classSchedule.getSubject())
                            .classname(classSchedule.getClassname())
                            .teacher(classSchedule.getTeacher())
                            .group(classSchedule.getGroup())
                            .author(classSchedule.getAuthor())
                            .verified(classSchedule.isVerified())
                            .terms(toClassInfoTermList(userSetting, classSchedule.getTerms()))
                            .build()
            );
        }

        return classInfos;
    }

    private List<ClassInfoTerm> toClassInfoTermList(UserSetting userSetting, List<ClassTerm> src) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");
        List<ClassInfoTerm> classInfoTerms = new ArrayList<>();

        for (ClassTerm classTerm : src) {
            DateDuration dateDuration = classTerm.getDuration();
            String duration = dateDuration.getStart().format(dtf) + " - " + dateDuration.getEnd().format(dtf);
            ClassInfoTerm term = ClassInfoTerm.builder()
                    .duration(duration)
                    .termData(toClassPlaceMap(userSetting, classTerm.getWeekData()))
                    .build();
            classInfoTerms.add(term);

            List<Integer> panelModel = new ArrayList<>();

            for (int i = 1; i < 8; i++) {
                Weekday weekday;

                if (i < 7) {
                    weekday = Weekday.parseInt(i);
                } else {
                    weekday = Weekday.SUNDAY;
                }

                if (term.getTermData().get(weekday).size() > 0) {
                    int idx = weekday.getId() - 1;

                    if (idx == -1) {
                        idx = 7;
                    }

                    panelModel.add(idx);
                }
            }

            term.setPanelModel(panelModel);
        }

        return classInfoTerms;
    }

    private Map<Weekday, List<ClassPlace>> toClassPlaceMap(UserSetting userSetting, WeekData weekData) {
        Map<Weekday, List<ClassPlace>> res = new HashMap<>();

        for (int i = 1; i < 8; i++) {
            Weekday weekday;

            if (i == 7) {
                weekday = Weekday.SUNDAY;
            } else {
                weekday = Weekday.parseInt(i);
            }
            DayOfWeekData dayOfWeekData = weekData.getData(weekday);
            List<ClassPlace> places = new ArrayList<>();

            for (PeriodOfDay periodOfDay : dayOfWeekData.getData()) {
                places.add(
                        ClassPlace.builder()
                                .periodRange(periodOfDay.getPeriodRange())
                                .timeRange(getTimeRange(userSetting, periodOfDay.getPeriodRange()))
                                .classroom(periodOfDay.getClassroom())
                                .build()
                );
            }

            res.put(weekday, places);
        }

        return res;
    }

    private StringTimeRange getTimeRange(UserSetting userSetting, ClassPeriodRange periodRange) {
        Map<Integer, TimeDuration> periodDurations = userSetting.getPeriodDurationMap();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        TimeDuration t1 = periodDurations.get(periodRange.getStart());
        TimeDuration t2 = periodDurations.get(periodRange.getEnd());
        String start = t1.getStart().format(dtf);
        String end = t2.getEnd().format(dtf);

        return new StringTimeRange(start, end);
    }
}
