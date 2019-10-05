package codes.nopain.nopain.app.controller.pojo.user.registered;

import codes.nopain.nopain.app.worker.global.enums.Weekday;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class ClassInfoTerm {
    private String duration;
    private Map<Weekday, List<ClassPlace>> termData;
    private List<Integer> panelModel;
}
