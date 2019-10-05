package codes.nopain.nopain.app.worker.wit.process.utils;

import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.wit.process.filter.regex.WitRegexFilter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WitDateUtils {

    public static boolean cross(DateDuration d1, DateDuration d2) {
        if (d1.getStart().isBefore(d2.getStart())) {
            return !d1.getEnd().isBefore(d2.getStart());
        } else  {
            return !d2.getEnd().isBefore(d1.getStart());
        }
    }

    public static DateDuration getCurrentWeek() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate end = start.plusDays(6);

        return new DateDuration(start, end);
    }

    public static LocalDate parseDate(String date) {
        List<Integer> numList = WitRegexUtils.extractNumber(date);

        if (numList.size() < 2) {
            return LocalDate.now();
        }

        if (numList.size() < 3) {
            numList.add(LocalDate.now().getYear());
        }

        int dd = numList.get(0);
        int MM = numList.get(1);
        int yyyy = numList.get(2);

        return LocalDate.of(yyyy, MM, dd);
    }
}
