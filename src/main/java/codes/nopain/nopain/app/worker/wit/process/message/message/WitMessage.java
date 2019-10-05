package codes.nopain.nopain.app.worker.wit.process.message.message;

import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class WitMessage {
    private String text;
    @Builder.Default
    private List<WitEntity> entities = new ArrayList<>();
}
