package codes.nopain.nopain.app.worker.crawl.reader;

import codes.nopain.nopain.app.worker.crawl.excel.ScheduleRow;

import java.util.Comparator;

public class ScheduleRowComparatorByDuration implements Comparator<ScheduleRow> {

    @Override
    public int compare(ScheduleRow r1, ScheduleRow r2) {
        return r1.getDateDuration().getStart().compareTo(r2.getDateDuration().getStart());
    }
}
