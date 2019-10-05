package codes.nopain.nopain.app.controller.pojo.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DashboardResponse {
    private int registered, avgPerWeek, totalPeriod;
    private String startTerm, endTerm, semester;
    private List<DashboardEvent> events;
}
