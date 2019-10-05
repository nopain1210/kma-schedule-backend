package codes.nopain.nopain.app.worker.schedule.classes;

import codes.nopain.nopain.app.database.document.ClassSchedule;
import codes.nopain.nopain.app.database.document.UserSchedule;
import codes.nopain.nopain.app.database.document.UserSetting;
import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.database.pojo.schedule.PeriodOfDay;
import codes.nopain.nopain.app.database.pojo.setting.KmaAccount;
import codes.nopain.nopain.app.database.repository.ClassesRepository;
import codes.nopain.nopain.app.database.repository.UserSchedulesRepository;
import codes.nopain.nopain.app.database.repository.UserSettingsRepository;
import codes.nopain.nopain.app.config.ScheduleProperties;
import codes.nopain.nopain.app.worker.crawl.excel.ScheduleRow;
import codes.nopain.nopain.app.worker.crawl.exception.SoupException;
import codes.nopain.nopain.app.worker.crawl.reader.SchedulerReader;
import codes.nopain.nopain.app.worker.global.comparator.ClassTermByDurationComparator;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import codes.nopain.nopain.app.worker.schedule.utils.GroupBy;
import codes.nopain.nopain.app.worker.schedule.utils.TableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClassListBuilder implements AutoCloseable {
    private final SchedulerReader schedulerReader;
    private final ClassesRepository classesRepository;
    private final UserSchedulesRepository schedulesRepository;
    private final UserSettingsRepository settingsRepository;
    private final ScheduleProperties scheduleProperties;

    private UserSetting setting;
    private UserSchedule schedule;

    public ClassListBuilder user(String email) {
        this.setting = settingsRepository.findByEmail(email)
                .orElseGet(() -> settingsRepository.save(
                        UserSetting.builder()
                                .email(email)
                                .build())
                );

        this.schedule = schedulesRepository.findByEmail(email)
                .orElseGet(() -> schedulesRepository.save(
                        UserSchedule.builder()
                                .email(email)
                                .build()
                ));

        return this;
    }

    public ClassListBuilder syncScheduleWithKmaServer() throws SoupException {
        return syncScheduleWithKmaServer(setting.getKmaAccount());
    }

    public ClassListBuilder syncScheduleWithKmaServer(KmaAccount account) throws SoupException {
        try {
            List<ScheduleRow> scheduleAsTable = schedulerReader
                    .authenticate(account)
                    .getDataFromKmaServer()
                    .getDataFromScheduleStream()
                    .getScheduleAsTable();
            removeOldSemester(schedulerReader.getScheduleSemester());

            List<List<ScheduleRow>> classNameGroups = TableUtils.slice(scheduleAsTable, GroupBy.CLASS_NAME);
            List<List<List<ScheduleRow>>> groups = new ArrayList<>();

            for (List<ScheduleRow> classNameGroup : classNameGroups) {
                List<List<ScheduleRow>> durationGroups = TableUtils.slice(classNameGroup, GroupBy.DURATION);
                groups.add(durationGroups);
            }

            for (List<List<ScheduleRow>> classNameGroup : groups) {
                ScheduleRow sample = classNameGroup.get(0).get(0);
                String subject = sample.getSubject();
                String className = sample.getClassName();
                String teacher = sample.getTeacher();
                ClassSchedule classSchedule = classesRepository.findByClassname(className)
                        .orElseGet(() -> classesRepository.save(
                                ClassSchedule.builder()
                                        .semester(schedulerReader.getScheduleSemester())
                                        .classname(className)
                                        .subject(subject)
                                        .teacher(teacher)
                                        .group(scheduleProperties.getDefaultGroup())
                                        .author(scheduleProperties.getDefaultAuthor())
                                        .terms(new ArrayList<>())
                                        .build()
                        ));

                classSchedule.setTerms(new ArrayList<>());

                for (List<ScheduleRow> durationGroup : classNameGroup) {
                    DateDuration duration = durationGroup.get(0).getDateDuration().deepClone();
                    ClassTerm term = ClassTerm.builder()
                            .duration(duration)
                            .build();

                    for (ScheduleRow row : durationGroup) {
                        Weekday weekday = row.getWeekday();
                        PeriodOfDay place = PeriodOfDay.builder()
                                .periodRange(row.getPeriodRange())
                                .classroom(row.getClassroom())
                                .classId(classSchedule.getId())
                                .classname(classSchedule.getClassname())
                                .subject(classSchedule.getSubject())
                                .teacher(classSchedule.getTeacher())
                                .build();

                        term.getWeekData().putData(weekday, place);
                    }

                    classSchedule.getTerms().add(term);
                }

                classSchedule.getTerms().sort(new ClassTermByDurationComparator());
                classSchedule = classesRepository.save(classSchedule);

                int indexOf = indexOf(classSchedule);

                if (indexOf != -1) {
                    this.schedule.getRegisteredClasses().remove(indexOf);
                }

                this.schedule.getRegisteredClasses().add(classSchedule);
            }

            this.schedule = schedulesRepository.save(this.schedule);

            this.setting.setKmaAccount(account);
            this.setting = settingsRepository.save(this.setting);
        } catch (SoupException se) {
            throw se;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SoupException(500);
        }

        return this;
    }

    private int indexOf(ClassSchedule checkClass) {
        for (ClassSchedule classSchedule : schedule.getRegisteredClasses()) {
            if (classSchedule.getClassname().equals(checkClass.getClassname())) {
                return schedule.getRegisteredClasses().indexOf(classSchedule);
            }
        }

        return -1;
    }

    private void removeOldSemester(String semester) {
        for (ClassSchedule classSchedule : schedule.getRegisteredClasses()) {
            if ((!semester.equals(classSchedule.getSemester()))
                    && (!"KMA".equals(classSchedule.getAuthor()))) {
                schedule.getRegisteredClasses().remove(classSchedule);
            }
        }
    }

    @Override
    public void close() {
        schedulerReader.close();
    }
}
