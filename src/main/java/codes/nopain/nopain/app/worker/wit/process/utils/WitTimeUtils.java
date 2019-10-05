package codes.nopain.nopain.app.worker.wit.process.utils;

import codes.nopain.nopain.app.database.pojo.setting.PeriodDurationMap;
import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.TimeDuration;
import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WitTimeUtils {

    public static ClassPeriodRange getPeriodRange(int classPeriod) {
        int start = (classPeriod - 1) * 3 + 1;
        int end = classPeriod * 3;

        if (classPeriod == 5) {
            end++;
        }

        return new ClassPeriodRange(start, end);
    }

    public static ClassPeriodRange getPeriodRange(LocalTime startTime, LocalTime endTime, PeriodDurationMap periodDurationMap) {
        System.out.println(startTime + " " + endTime);
        return new ClassPeriodRange(
                getPeriod(startTime, periodDurationMap, true),
                getPeriod(endTime, periodDurationMap, true));
    }

    private static int getPeriod(LocalTime time, PeriodDurationMap periodDurationMap, boolean approx) {
        int res = 0;

        for (int period = 1; period < 16; period++) {
            TimeDuration duration1 = periodDurationMap.get(period);
            TimeDuration duration2 = periodDurationMap.get(period + 1);

            if (duration1.getStart().compareTo(time) <= 0 && time.compareTo(duration1.getEnd()) <= 0) {
                return period;
            }

            if (duration2.getStart().compareTo(time) <= 0 && time.compareTo(duration2.getEnd()) <= 0) {
                return period + 1;
            }

            if (approx) {
                int gap1 = duration1.getEnd().compareTo(time);
                int gap2 = time.compareTo(duration2.getStart());

                if (gap1 < 0 && gap2 < 0) {
                    return  Math.abs(gap1) < Math.abs(gap2) ? period : period + 1;
                }
            }

        }

        if (periodDurationMap.get(16).getEnd().isBefore(time)) {
            res = 17;
        }

        if (approx) {
            return res == 0 ? 1 : 16;
        }

        return res;
    }

    public static LocalTime parseTime(String source) {
        List<Integer> nums = WitRegexUtils.extractNumber(source);

        if (nums.size() < 1) {
            return LocalTime.now();
        }

        if (nums.size() < 2) {
            nums.add(0);
        }

        return LocalTime.of(nums.get(0), nums.get(1));
    }
}
