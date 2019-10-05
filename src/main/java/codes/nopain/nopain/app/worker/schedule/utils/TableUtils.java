package codes.nopain.nopain.app.worker.schedule.utils;

import codes.nopain.nopain.app.worker.crawl.excel.ScheduleRow;

import java.util.ArrayList;
import java.util.List;

public class TableUtils {
    private static boolean isEqual(ScheduleRow r1, ScheduleRow r2, GroupBy groupBy) {
        return groupBy == GroupBy.DURATION ? r1.isDurationEqual(r2) : r1.isClassNameEqual(r2);
    }

    public static List<List<ScheduleRow>> slice(List<ScheduleRow> source, GroupBy groupBy) {
        List<List<ScheduleRow>> dst = new ArrayList<>();
        int rowNum = source.size();

        for (int i = 0; i < rowNum; i++) {
            int start = i;

            if (i < rowNum - 1) {
                while (isEqual(source.get(i), source.get(i + 1), groupBy)) {
                    i++;

                    if (i == rowNum - 1) {
                        break;
                    }
                }
            }

            int end = i;
            dst.add(source.subList(start, end + 1));
        }

        return dst;
    }
}
