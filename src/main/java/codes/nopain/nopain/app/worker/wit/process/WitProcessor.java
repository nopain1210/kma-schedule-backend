package codes.nopain.nopain.app.worker.wit.process;

import codes.nopain.nopain.app.worker.wit.resource.response.WitResponse;
import codes.nopain.nopain.app.worker.wit.resource.response.WitResponseEntity;
import codes.nopain.nopain.app.worker.wit.process.command.WitCommands;
import codes.nopain.nopain.app.worker.wit.process.filter.command.WitCommandFilter;
import codes.nopain.nopain.app.worker.wit.process.filter.post.WitDateFilter;
import codes.nopain.nopain.app.worker.wit.process.filter.post.WitTimeFilter;
import codes.nopain.nopain.app.worker.wit.process.filter.regex.WitGenericFilter;
import codes.nopain.nopain.app.worker.wit.process.filter.regex.WitRegexFilter;
import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import codes.nopain.nopain.app.worker.wit.process.utils.WitUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class WitProcessor {
    private final WitRegexFilter witRegexFilter;
    private final WitDateFilter witDateFilter;
    private final WitTimeFilter witTimeFilter;
    private final WitCommandFilter witCommandFilter;

    private WitMessage witMessage;
    private WitCommands witCommands;
    private String postMessage;
    private String email;

    public WitProcessor user(String email) {
        this.email = email;
        return this;
    }

    public WitProcessor transform(WitResponse witResponse) {
        String text = witResponse.getText();
        WitMessage witMessage = WitMessage.builder()
                .text(text)
                .build();

        List<WitResponseEntity> responseEntities = WitUtils.getAllResponseEntity(witResponse.getWitResponseEntityMap());
        for (WitResponseEntity witResponseEntity : responseEntities) {
            WitEntity witEntity = WitEntity.builder()
                    .entityType(WitEntityType.findByName(witResponseEntity.getEntity()))
                    .value(witResponseEntity.getValue())
                    .start(witResponseEntity.getStart())
                    .end(witResponseEntity.getEnd())
                    .build();

            witMessage.getEntities().add(witEntity);
        }

        WitGenericFilter.fillNonClassified(witMessage);
        this.witMessage = witMessage;

        return this;
    }

    public WitProcessor regexFilter() {
        witRegexFilter
                .message(witMessage)
                .shortDateFilter()
                .longDateFilter()
                .monthFilter()
                .exactTimeFilter()
                .dayFilter();

        WitUtils.removeNonClassified(witMessage);

        return this;
    }

    public WitProcessor postFilter() {
        witDateFilter.message(witMessage)
                .relativeDateFilter()
                .mergeDate()
                .monthFilter()
                .monthDurationFilter()
                .monthToDurationFilter();
        witTimeFilter.message(witMessage)
                .user(email)
                .timeDurationToShiftFilter()
                .classShiftDurationFilter()
                .classShiftToPeriodFilter()
                .classPeriodDurationFilter();
        return this;
    }

    public WitProcessor commandFilter() {
        this.witCommands = this.witCommandFilter
                .message(witMessage)
                .processPostMessage()
                .fill()
                .combinePeriod()
                .getCommand();
        this.postMessage = this.witCommandFilter.getPostMessage();
        return this;
    }


}
