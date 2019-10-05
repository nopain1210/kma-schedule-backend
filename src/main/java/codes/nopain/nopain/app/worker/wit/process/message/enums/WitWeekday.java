package codes.nopain.nopain.app.worker.wit.process.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WitWeekday {
    MONDAY(0, "mon"),
    TUESDAY(1, "tue"),
    WEDNESDAY(2, "wed"),
    THURSDAY(3, "thu"),
    FRIDAY(4, "fri"),
    SATURDAY(5, "sat"),
    SUNDAY(6, "sun"),
    NULL(-1, "null");

    private int id;
    private String value;

    public static WitWeekday parseString(String value) {
        for (WitWeekday witWeekday : WitWeekday.values()) {
            if (witWeekday.getValue().equals(value)) {
                return witWeekday;
            }
        }

        return WitWeekday.NULL;
    }

    public static WitWeekday parseInt(int id) {
        for (WitWeekday witWeekday : WitWeekday.values()) {
            if (witWeekday.getId() == id) {
                return witWeekday;
            }
        }

        return WitWeekday.NULL;
    }
}
