package codes.nopain.nopain.app.worker.wit.process.filter.command;

import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import codes.nopain.nopain.app.worker.wit.process.command.PartOfDay;
import codes.nopain.nopain.app.worker.wit.process.command.WitCommands;
import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import codes.nopain.nopain.app.worker.wit.process.utils.WitDateUtils;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
@Getter
public class WitCommandFilter {
    private WitCommands command;
    private String postMessage;

    private void addPeriodRange(int start, int end) {
        for (int p = start; p <= end; p++) {
            command.getPeriods().add(p);
        }
    }

    private void fillPeriods() {
        if (command.getPeriods().size() == 0) {
            for (PartOfDay partOfDay : command.getPartOfDays()) {
                if (partOfDay == PartOfDay.MORNING) {
                    addPeriodRange(1, 6);
                }

                if (partOfDay == PartOfDay.AFTERNOON) {
                    addPeriodRange(7, 12);
                }

                if (partOfDay == PartOfDay.EVENING) {
                    addPeriodRange(13, 16);
                }
            }
        }
    }

    private void fillPartOfDay() {
        if (command.getPartOfDays().size() == 0) {
            for (PartOfDay partOfDay : PartOfDay.values()) {
                command.getPartOfDays().add(partOfDay);
            }
        }
    }

    private void fillWeekDay() {
        if (command.getWeekdays().size() == 0) {
            for (Weekday weekday : Weekday.values()) {
                command.getWeekdays().add(weekday);
            }
        }
    }

    public WitCommandFilter combinePeriod() {
        for (PartOfDay partOfDay : command.getPartOfDays()) {
            for (int period : command.getPeriods()) {
                command.getPeriods().add(transformPeriod(period, partOfDay));
            }
        }

        return this;
    }

    private int transformPeriod(int src, PartOfDay partOfDay) {
        if (partOfDay == PartOfDay.AFTERNOON) {
            if (src < 7) {
                return src + 6;
            }
        }

        if (partOfDay == PartOfDay.EVENING) {
            if (src < 4) {
                return src + 12;
            }
        }

        return src;
    }

    public WitCommandFilter fill() {
        if (command.getDurations().size() == 0) {
            if (command.getWeekdays().size() == 0) {
                command.getDurations().add(new DateDuration(LocalDate.now(), LocalDate.now()));
            } else {
                command.getDurations().add(WitDateUtils.getCurrentWeek());
            }
        } else {
            fillWeekDay();
        }

        fillPartOfDay();
        fillPeriods();
        return this;
    }

    public WitCommandFilter message(WitMessage message) {
        WitCommands command = WitCommands.builder().build();
        List<WitEntity> src = message.getEntities();
        int size = src.size();

        for (int idx = 0; idx < size; idx++) {
            WitEntity entity = src.get(idx);
            WitEntityType type = entity.getEntityType();

            if (type.equals(WitEntityType.CLASS_PERIOD)) {
                command.getPeriods().add(Integer.parseInt(entity.getValue()));
            }

            if (type.equals(WitEntityType.PART_OF_DAY)) {
                command.getPartOfDays().add(PartOfDay.parseString(entity.getValue()));
            }

            if (type.equals(WitEntityType.WEEKDAY)) {
                command.getWeekdays().add(Weekday.parseString(entity.getValue()));
            }

            if (type.equals(WitEntityType.EXACT_DATE)) {
                LocalDate startDate = WitDateUtils.parseDate(entity.getValue());
                LocalDate endDate;
                if (idx > size - 3) {
                    endDate = startDate;
                } else {
                    endDate = WitDateUtils.parseDate(src.get(idx + 2).getValue());
                    idx += 2;
                }

                command.getDurations().add(new DateDuration(startDate, endDate));
            }
        }

        this.command = command;

        return this;
    }

    public WitCommandFilter processPostMessage() {
        postMessage = "Thời khóa biểu ";

        List<ClassPeriodRange> periodRanges = getRange(command.getPeriods());

        for (ClassPeriodRange period : periodRanges) {
            boolean dup = period.getStart() == period.getEnd();
            String add = (dup ? "" : "từ ")
                    + "Tiết " + period.getStart()
                    + (dup ? "" : " đến " + "Tiết " + period.getEnd());
            postMessage = String.format("%s%s", postMessage, String.format("%s ", add));
        }

        HashSet<PartOfDay> partOfDays = command.getPartOfDays();
        boolean exists = false;
        for (int i = 0; i < 3; i++) {
            PartOfDay partOfDay = PartOfDay.parseId(i);

            if (partOfDays.contains(PartOfDay.parseId(i))) {
                if (partOfDay != null) {
                    postMessage = String.format("%s%s", postMessage, String.format("%s, ", partOfDay.getText()));
                    exists = true;
                }
            }
        }

        if (exists) {
            postMessage = postMessage.substring(0, postMessage.length() - 2) + " ";
        }

        exists = false;
        HashSet<Weekday> weekdays = command.getWeekdays();
        for (int i = 0; i < 7; i++) {
            Weekday weekday = Weekday.parseInt(i);
            if (weekdays.contains(weekday)) {
                postMessage = String.format("%s%s", postMessage, String.format("%s, ", weekday.getText()));
                exists = true;
            }
        }

        if (exists) {
            postMessage = postMessage.substring(0, postMessage.length() - 2) + " ";
        }

        return this;
    }

    private List<ClassPeriodRange> getRange(Set<Integer> periodSet) {
        List<ClassPeriodRange> res = new ArrayList<>();
        List<Integer> periods = new ArrayList<>(periodSet);
        Collections.sort(periods);

        for (int idx = 0; idx < periods.size(); idx++) {
            int start = idx;

            if (idx < periods.size() - 1) {
                while (periods.get(idx + 1) - periods.get(idx) == 1) {
                    idx++;

                    if (idx == periods.size() - 1) {
                        break;
                    }
                }
            }

            int end = idx;
            res.add(ClassPeriodRange.builder()
                    .start(periods.get(start))
                    .end(periods.get(end))
                    .build());
        }

        return res;
    }
}
