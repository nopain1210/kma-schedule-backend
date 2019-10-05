package codes.nopain.nopain.app.worker.wit.process.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WitRelativeDate {
    TODAY("today"),
    TOMORROW("tomorrow"),
    AFTER_TOMORROW("after_tomorrow"),
    THIS_WEEK("this_week"),
    NEXT_WEEK("next_week"),
    AFTER_NEXT_WEEK("after_next_week"),
    THIS_MONTH("this_month"),
    NEXT_MONTH("next_month"),
    AFTER_NEXT_MONTH("after_next_month"),
    MID_MONTH("mid_month"),
    OVER_MONTH("over_month");

    private String value;

    public static WitRelativeDate parseString(String value) {
        for (WitRelativeDate witRelativeDate : WitRelativeDate.values()) {
            if (witRelativeDate.getValue().equals(value)) {
                return witRelativeDate;
            }
        }

        return null;
    }
}
