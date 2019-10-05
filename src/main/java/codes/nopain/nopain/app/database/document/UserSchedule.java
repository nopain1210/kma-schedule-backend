package codes.nopain.nopain.app.database.document;

import codes.nopain.nopain.app.worker.schedule.spreadsheet.entity.Spreadsheet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("user-schedules")
@Getter
@Setter
@Builder
public class UserSchedule {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    @Builder.Default
    private List<ClassSchedule> registeredClasses = new ArrayList<>();

    private Spreadsheet spreadsheet;
}
