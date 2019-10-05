package codes.nopain.nopain.app.worker.wit.api.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
public class TextMessage {
    @Builder.Default
    private String type = "text";
    @Builder.Default
    private String author = "bot";
    @Builder.Default
    private Map<String, String> data = new HashMap<>();
}
