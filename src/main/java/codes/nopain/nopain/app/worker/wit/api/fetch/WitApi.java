package codes.nopain.nopain.app.worker.wit.api.fetch;

import codes.nopain.nopain.app.controller.exception.NoContentException;
import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.database.pojo.schedule.PeriodOfDay;
import codes.nopain.nopain.app.database.pojo.setting.PeriodDurationMap;
import codes.nopain.nopain.app.database.repository.UserSchedulesRepository;
import codes.nopain.nopain.app.database.repository.UserSettingsRepository;
import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.entity.TimeDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import codes.nopain.nopain.app.worker.wit.api.message.KsMessage;
import codes.nopain.nopain.app.worker.wit.api.message.KsMessageLine;
import codes.nopain.nopain.app.worker.wit.api.message.KsMessageSentence;
import codes.nopain.nopain.app.worker.wit.api.message.TextMessage;
import codes.nopain.nopain.app.worker.wit.api.utils.MessageUtils;
import codes.nopain.nopain.app.worker.wit.process.WitProcessor;
import codes.nopain.nopain.app.worker.wit.process.command.WitCommands;
import codes.nopain.nopain.app.worker.wit.process.utils.WitDateUtils;
import codes.nopain.nopain.app.worker.wit.resource.WitResource;
import codes.nopain.nopain.app.worker.wit.resource.response.WitResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class WitApi {
    private final WitResource witResource;
    private final WitProcessor witProcessor;
    private final UserSchedulesRepository userSchedulesRepository;
    private final UserSettingsRepository userSettingsRepository;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private String email;
    private List<ClassTerm> terms;
    private List<KsMessage> ksMessages;
    private PeriodDurationMap periodDurationMap;

    public WitApi user(String email) {
        this.email = email;
        this.terms = userSchedulesRepository.findByEmail(email)
                .orElseThrow(NoContentException::new)
                .getSpreadsheet()
                .getSheets();
        this.periodDurationMap = userSettingsRepository.findByEmail(email)
                .orElseThrow(NoContentException::new)
                .getPeriodDurationMap();
        return this;
    }

    public WitApi getKsMessages(String message) throws IOException {
        List<KsMessage> ksMessageList = new ArrayList<>();
        WitResponse response = witResource.fetch(message);
        WitCommands command = witProcessor
                .user(email)
                .transform(response)
                .regexFilter()
                .postFilter()
                .commandFilter()
                .getWitCommands();

        for (DateDuration duration : command.getDurations()) {
            HashSet<Weekday> weekdaysTmp = new HashSet<>();
            LocalDate startDate = duration.getStart();
            LocalDate endDate = duration.getEnd();

            for (LocalDate date = startDate; date.compareTo(endDate) <= 0; date = date.plusDays(1)) {
                weekdaysTmp.add(Weekday.parseInt(date.getDayOfWeek().getValue()));
            }

            HashSet<Weekday> cmdWeekdays = command.getWeekdays();
            HashSet<Weekday> weekdays = new HashSet<>();

            for (Weekday weekday : cmdWeekdays) {
                if (weekdaysTmp.contains(weekday)) {
                    weekdays.add(weekday);
                }
            }

            KsMessage ksMessage = KsMessage.builder()
                    .duration(duration)
                    .build();
            ksMessageList.add(ksMessage);

            for (ClassTerm term : terms) {
                if (WitDateUtils.cross(duration, term.getDuration())) {
                    ksMessage.getLines().add(MessageUtils.toMessageLine(term));
                }
            }

            MessageUtils.weekdayFilter(ksMessage, weekdays);
            MessageUtils.periodFilter(ksMessage, command.getPeriods());
            MessageUtils.emptySentenceFilter(ksMessage);
            MessageUtils.emptyLineFilter(ksMessage);

            boolean dup = duration.getStart().isEqual(duration.getEnd());

            ksMessage.setPostMessage(witProcessor.getPostMessage()
                    + (dup ? "ngày " : "từ ")
                    + duration.getStart().format(dtf)
                    + (dup ? "" : " dến " + duration.getEnd().format(dtf))
            );
        }

        this.ksMessages = ksMessageList;

        return this;
    }

    public List<TextMessage> toTextMessages() {
        List<String> messages = new ArrayList<>();
        String BOLD = "<span style='font-weight: bold'>";

        for (KsMessage ksMessage : ksMessages) {
            messages.add(BOLD + ksMessage.getPostMessage() + "</span>");

            if (ksMessage.getLines().size() == 0) {
                messages.add("<i>Không có môn học nào </i>");
            }

            for (KsMessageLine line : ksMessage.getLines()) {
                messages.add(BOLD + "* " + MessageUtils.toText(line.getDuration()) + "</span>");
                StringBuilder mes = new StringBuilder();

                for (KsMessageSentence sentence : line.getMessageSentences()) {
                    mes.append(BOLD).append(sentence.getWeekday().getText()).append("</span> <br/>");

                    for (PeriodOfDay periodOfDay : sentence.getPeriods()) {
                        ClassPeriodRange range = periodOfDay.getPeriodRange();
                        TimeDuration p1 = periodDurationMap.get(range.getStart());
                        TimeDuration p2 = periodDurationMap.get(range.getEnd());
                        mes.append(BOLD).append("- Tiết ").append(range.getStart()).append(" đến Tiết ").append(range.getEnd())
                                .append(" (").append(p1.getStart().format(timeFormatter)).append(" - ").append(p2.getEnd().format(timeFormatter))
                                .append("</span>")
                                .append("): [")
                        .append(periodOfDay.getClassroom()).append("] ").append(periodOfDay.getClassname()).append("</br>");
                    }
                }

                messages.add(mes.toString());
            }
        }

        List<TextMessage> textMessages = new ArrayList<>();

        for (String message : messages) {
            TextMessage textMessage = TextMessage.builder().build();
            textMessage.getData().put("text", message);

            textMessages.add(textMessage);
        }

        return textMessages;
    }

}
