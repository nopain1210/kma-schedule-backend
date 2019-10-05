package codes.nopain.nopain.app.worker.wit.resource.response;

import com.google.api.client.util.Key;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WitResponse {
    @Key("_text")
    private String text;

    @Key("entities")
    private WitResponseEntityMap witResponseEntityMap;
}
