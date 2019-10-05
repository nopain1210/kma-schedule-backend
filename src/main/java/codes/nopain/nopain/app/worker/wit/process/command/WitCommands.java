package codes.nopain.nopain.app.worker.wit.process.command;

import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class WitCommands {
    @Builder.Default
    private HashSet<Integer> periods = new HashSet<>();
    @Builder.Default
    private HashSet<PartOfDay> partOfDays = new HashSet<>();
    @Builder.Default
    private HashSet<Weekday> weekdays = new HashSet<>();
    @Builder.Default
    private List<DateDuration> durations = new ArrayList<>();
}
