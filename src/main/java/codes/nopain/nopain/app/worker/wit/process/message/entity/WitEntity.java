package codes.nopain.nopain.app.worker.wit.process.message.entity;

import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WitEntity {
    private WitEntityType entityType;
    private String value;
    private int start, end;

}
