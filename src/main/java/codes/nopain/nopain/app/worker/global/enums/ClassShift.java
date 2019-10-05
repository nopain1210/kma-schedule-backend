package codes.nopain.nopain.app.worker.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@RequiredArgsConstructor
@Getter
public enum ClassShift {
    SHIFT_1(1, "shift_1", LocalTime.of(7, 0), LocalTime.of(9, 25)),
    SHIFT_2(2, "shift_2", LocalTime.of(9, 25), LocalTime.of(12, 0)),
    SHIFT_3(3, "shift_3", LocalTime.of(12, 0), LocalTime.of(14, 55)),
    SHIFT_4(4, "shift_4", LocalTime.of(14, 55), LocalTime.of(17, 30)),
    SHIFT_5(5, "shift_5", LocalTime.of(17, 30), LocalTime.of(21, 15));

    private final int id;
    private final String value;
    private final LocalTime start;
    private final LocalTime end;

    public static ClassShift parseString(String value) {
        for (ClassShift shift : ClassShift.values()) {
            if (shift.getValue().equals(value)) {
                return shift;
            }
        }

        return ClassShift.SHIFT_1;
    }

    public static ClassShift parseInt(int id) {
        for (ClassShift shift : ClassShift.values()) {
            if (shift.getId() == id) {
                return shift;
            }
        }

        return ClassShift.SHIFT_1;
    }
}
