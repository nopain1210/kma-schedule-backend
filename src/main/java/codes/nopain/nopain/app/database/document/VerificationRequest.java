package codes.nopain.nopain.app.database.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "verification-requests")
@Getter
@Setter
@Builder
public class VerificationRequest {
    @Id
    private String id;
    private ClassSchedule requestClass;
    private String description;
}
