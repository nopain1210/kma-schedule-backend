package codes.nopain.nopain.app.database.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messengers")
public class MessengerId {
    @Id
    private String id;

    @Indexed(unique = true)
    private String senderId;

    @Indexed
    private String email;
}
