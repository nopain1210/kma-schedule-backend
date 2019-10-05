package codes.nopain.nopain.app.database.document;

import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "subject-classes")
@Getter
@Setter
@Builder
public class ClassSchedule {
    @Id
    private String id;

    private String semester;

    private String subject;

    private String classname;

    private String teacher;

    @Builder.Default
    private String group;

    @Builder.Default
    private String author;

    @Builder.Default
    private boolean verified = true;

    private List<ClassTerm> terms;
}
