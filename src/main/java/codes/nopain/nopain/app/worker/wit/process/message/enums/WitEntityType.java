package codes.nopain.nopain.app.worker.wit.process.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WitEntityType {
    INTENT("intent"),
    RELATIVE_TIME("relative_time"),
    WEEKDAY("weekday"),
    TIME_ADVERB("time_adv"),
    RELATIVE_DATE("relative_date"),
    PREPOSITION("preposition"),
    EXACT_TIME("exact_time"),
    EXACT_DATE("exact_date"),
    EXACT_MONTH("exact_month"),
    EXACT_DAY("exact_day"),
    CLASS_SHIFT("class_shift"),
    CLASS_PERIOD("class_period"),
    PART_OF_DAY("part_of_day"),
    UNCLASSIFIED("non_classified");

    private String entityName;

    public static WitEntityType findByName(String entityName) {
        for (codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType WitEntityType : WitEntityType.values()) {
            if (WitEntityType.getEntityName().equals(entityName)) {
                return WitEntityType;
            }
        }

        return null;
    }
}
