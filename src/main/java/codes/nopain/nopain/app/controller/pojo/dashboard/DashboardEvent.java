package codes.nopain.nopain.app.controller.pojo.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DashboardEvent {
    private String name, classname, classId, classroom, color, start, end;
    private int startPeriod, endPeriod;
}
