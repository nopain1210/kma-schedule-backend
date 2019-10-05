package codes.nopain.nopain.app.worker.wit.api.message;

import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class KsMessageLine {
    private DateDuration duration;

    @Builder.Default
    private List<KsMessageSentence> messageSentences = new ArrayList<>();
}
