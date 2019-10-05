package codes.nopain.nopain.app.worker.global.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Weekday {
    @JsonProperty("sun") SUNDAY(0, "sun", "Chủ Nhật"),
    @JsonProperty("mon") MONDAY(1, "mon", "Thứ Hai"),
    @JsonProperty("tue") TUESDAY(2, "tue", "Thứ Ba"),
    @JsonProperty("wed") WEDNESDAY(3, "wed", "Thứ Tư"),
    @JsonProperty("thu") THURSDAY(4, "thu", "Thứ Năm"),
    @JsonProperty("fri") FRIDAY(5, "fri", "Thứ Sáu"),
    @JsonProperty("sat") SATURDAY(6, "sat", "Thứ Bảy");

    private final int id;
    private final String value;
    private final String text;

    public static Weekday parseString(String value) {
        for (Weekday weekday : Weekday.values()) {
            if (weekday.getValue().equals(value)) {
                return weekday;
            }
        }

        return Weekday.SUNDAY;
    }

    public static Weekday parseInt(int id) {
        for (Weekday weekday : Weekday.values()) {
            if (weekday.getId() == id) {
                return weekday;
            }
        }

        return Weekday.SUNDAY;
    }
}
