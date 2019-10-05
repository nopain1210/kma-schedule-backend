package codes.nopain.nopain.app.controller.pojo.user.registered;

import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClassPlace {
    private ClassPeriodRange periodRange;
    private StringTimeRange timeRange;
    private String classroom;
}
