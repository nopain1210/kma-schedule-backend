package codes.nopain.nopain.app.database.pojo.setting;

import codes.nopain.nopain.app.worker.global.entity.TimeDuration;

import java.time.LocalTime;
import java.util.HashMap;

public class PeriodDurationMap extends HashMap<Integer, TimeDuration> {
    public PeriodDurationMap() {
        this.put(1,
                TimeDuration.builder()
                        .start(LocalTime.of(7, 0))
                        .end(LocalTime.of(7, 45))
                        .build());
        this.put(2,
                TimeDuration.builder()
                        .start(LocalTime.of(7, 50))
                        .end(LocalTime.of(8, 35))
                        .build());
        this.put(3,
                TimeDuration.builder()
                        .start(LocalTime.of(8, 40))
                        .end(LocalTime.of(9, 25))
                        .build());
        this.put(4,
                TimeDuration.builder()
                        .start(LocalTime.of(9, 35))
                        .end(LocalTime.of(10, 20))
                        .build());
        this.put(5,
                TimeDuration.builder()
                        .start(LocalTime.of(10, 25))
                        .end(LocalTime.of(11, 10))
                        .build());
        this.put(6,
                TimeDuration.builder()
                        .start(LocalTime.of(11, 15))
                        .end(LocalTime.of(12, 0))
                        .build());
        this.put(7,
                TimeDuration.builder()
                        .start(LocalTime.of(12, 30))
                        .end(LocalTime.of(13, 15))
                        .build());
        this.put(8,
                TimeDuration.builder()
                        .start(LocalTime.of(13, 20))
                        .end(LocalTime.of(14, 5))
                        .build());
        this.put(9,
                TimeDuration.builder()
                        .start(LocalTime.of(14, 10))
                        .end(LocalTime.of(14, 55))
                        .build());
        this.put(10,
                TimeDuration.builder()
                        .start(LocalTime.of(15, 05))
                        .end(LocalTime.of(15, 50))
                        .build());
        this.put(11,
                TimeDuration.builder()
                        .start(LocalTime.of(15, 55))
                        .end(LocalTime.of(16, 40))
                        .build());
        this.put(12,
                TimeDuration.builder()
                        .start(LocalTime.of(16, 45))
                        .end(LocalTime.of(17, 30))
                        .build());
        this.put(13,
                TimeDuration.builder()
                        .start(LocalTime.of(18, 0))
                        .end(LocalTime.of(18, 45))
                        .build());
        this.put(14,
                TimeDuration.builder()
                        .start(LocalTime.of(18, 45))
                        .end(LocalTime.of(19, 30))
                        .build());
        this.put(15,
                TimeDuration.builder()
                        .start(LocalTime.of(19, 45))
                        .end(LocalTime.of(20, 30))
                        .build());
        this.put(16,
                TimeDuration.builder()
                        .start(LocalTime.of(20, 30))
                        .end(LocalTime.of(21, 15))
                        .build());
    }
}
