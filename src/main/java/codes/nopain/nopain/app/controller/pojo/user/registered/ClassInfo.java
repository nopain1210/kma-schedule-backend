package codes.nopain.nopain.app.controller.pojo.user.registered;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ClassInfo {
    private String classId;
    private String semester;
    private String subject;
    private String classname;
    private String teacher;
    private String group;
    private String author;
    private boolean verified;
    private List<ClassInfoTerm> terms;
}
