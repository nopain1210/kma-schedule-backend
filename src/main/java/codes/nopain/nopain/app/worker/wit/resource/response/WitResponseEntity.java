package codes.nopain.nopain.app.worker.wit.resource.response;

import com.google.api.client.util.Key;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WitResponseEntity {
    @Key("_entity")
    private String entity;

    @Key("value")
    private String value;

    @Key("_start")
    private int start;

    @Key("_end")
    private int end;
}
