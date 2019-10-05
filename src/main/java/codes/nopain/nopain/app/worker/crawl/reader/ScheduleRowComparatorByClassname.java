package codes.nopain.nopain.app.worker.crawl.reader;

import codes.nopain.nopain.app.worker.crawl.excel.ScheduleRow;

import java.util.Comparator;

public class ScheduleRowComparatorByClassname implements Comparator<ScheduleRow> {
    @Override
    public int compare(ScheduleRow t1, ScheduleRow t2) {
        return t1.getClassName().compareTo(t2.getClassName());
    }
}
