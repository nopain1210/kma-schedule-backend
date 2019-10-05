package codes.nopain.nopain.app.database.pojo.schedule;

import codes.nopain.nopain.app.worker.global.enums.Weekday;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WeekData {
    private Map<Weekday, DayOfWeekData> dataMap;

    public WeekData() {
        initData();
    }

    public void putData(Weekday weekday, PeriodOfDay periodOfDay) {
        dataMap.get(weekday).getData().add(periodOfDay);
    }

    public void putAllData(Weekday weekday, List<PeriodOfDay> periodOfDayList) {
        dataMap.get(weekday).getData().addAll(periodOfDayList);
    }

    public void putAllData(WeekData source) {
        for (Weekday weekday : Weekday.values()) {
            putAllData(weekday, source.getData(weekday).getData());
        }
    }

    public DayOfWeekData getData(Weekday weekday) {
        return dataMap.get(weekday);
    }

    private void initData() {
        dataMap = new HashMap<>();

        for (Weekday weekday : Weekday.values()) {
            dataMap.put(weekday, DayOfWeekData.builder().weekday(weekday).build());
        }
    }

    public boolean isEqual(WeekData o) {
        for (Weekday weekday : Weekday.values()) {
            DayOfWeekData d1 = this.getData(weekday);
            DayOfWeekData d2 = o.getData(weekday);
            boolean d1NotEqualD2 = !d1.isEqual(d2);

            if (d1NotEqualD2) {
                return false;
            }
        }

        return true;
    }

    public void sortData() {
        dataMap = new TreeMap<>(dataMap);
        for (Weekday weekday : Weekday.values()) {
            dataMap.get(weekday).sort();
        }
    }
}
