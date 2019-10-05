package codes.nopain.nopain.app.worker.wit.resource.response;

import com.google.api.client.util.Key;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WitResponseEntityMap {
    @Key("class_period")
    private List<WitResponseEntity> classPeriod;

    @Key("weekday")
    private List<WitResponseEntity> weekday;

    @Key("class_shift")
    private List<WitResponseEntity> classShift;

    @Key("time_adv")
    private List<WitResponseEntity> timeAdverb;

    @Key("part_of_day")
    private List<WitResponseEntity> partOfDay;

    @Key("relative_date")
    private List<WitResponseEntity> relativeDate;

    @Key("preposition")
    private List<WitResponseEntity> preposition;
}
