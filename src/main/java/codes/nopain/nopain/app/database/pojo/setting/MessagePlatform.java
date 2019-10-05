package codes.nopain.nopain.app.database.pojo.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessagePlatform {
    private final MessageProvider provider;
    private final String senderID;
}
