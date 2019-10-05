package codes.nopain.nopain.app.worker.crawl.utils;

import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.enums.ClassShift;
import codes.nopain.nopain.app.worker.global.enums.Weekday;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnumParser {
    public static Weekday parseWeekday(int id) {
        return Weekday.parseInt(id - 1);
    }

    public static ClassPeriodRange parsePeriodRange(String source) {
        List<Integer> numList = RegexUtils.parseNumbers(source);
        return ClassPeriodRange.builder()
                .start(numList.get(0))
                .end(numList.get(numList.size() - 1))
                .build();
    }

    public static List<ClassShift> parseClassShifts(String source) {
        List<ClassShift> shifts = new ArrayList<>();
        List<Integer> numList = RegexUtils.parseNumbers(source);
        Collections.sort(numList);

        for (int i = 0; i < numList.size(); i++) {
            int start = numList.get(i);

            if (i < numList.size() - 1) {
                while (numList.get(i) + 1 == numList.get(i + 1)) {
                    i++;

                    if (i > numList.size() - 2) {
                        break;
                    }

                    if (numList.get(i) - start == 2) {
                        break;
                    }
                }
            }

            shifts.add(ClassShift.parseInt(Math.floorDiv(start - 1, 3) + 1));
        }

        return shifts;
    }
}
