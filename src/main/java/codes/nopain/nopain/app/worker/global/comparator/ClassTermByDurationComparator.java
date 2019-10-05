package codes.nopain.nopain.app.worker.global.comparator;

import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;

import java.util.Comparator;

public class ClassTermByDurationComparator implements Comparator<ClassTerm> {
    @Override
    public int compare(ClassTerm o1, ClassTerm o2) {
        return o1.compareToByDuration(o2);
    }
}
