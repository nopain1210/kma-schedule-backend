package codes.nopain.nopain.app.worker.wit.process.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PartOfDay {
    MORNING(0, "morning", "Buổi sáng"),
    AFTERNOON(1, "afternoon", "Buổi chiều"),
    EVENING(2, "evening", "Buổi tối");

    private final int id;
    private final String value;
    private final String text;

    public static PartOfDay parseString(String value) {
        for (PartOfDay partOfDay : PartOfDay.values()) {
            if (partOfDay.getValue().equals(value)) {
                return partOfDay;
            }
        }

        return null;
    }

    public static PartOfDay parseId(int id) {
        for (PartOfDay partOfDay : PartOfDay.values()) {
            if (partOfDay.getId() == id) {
                return partOfDay;
            }
        }

        return null;
    }
}
