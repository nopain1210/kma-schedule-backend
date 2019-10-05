package codes.nopain.nopain.app.worker.wit.api.utils;

import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.database.pojo.schedule.DayOfWeekData;
import codes.nopain.nopain.app.database.pojo.schedule.PeriodOfDay;
import codes.nopain.nopain.app.database.pojo.schedule.WeekData;
import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import codes.nopain.nopain.app.worker.wit.api.message.KsMessage;
import codes.nopain.nopain.app.worker.wit.api.message.KsMessageLine;
import codes.nopain.nopain.app.worker.wit.api.message.KsMessageSentence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MessageUtils {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static String toText(DateDuration duration) {
        LocalDate start = duration.getStart();
        LocalDate end = duration.getEnd();
        boolean dup = start.isEqual(end);

        return dup ? "" : start.format(dtf) + " - " + end.format(dtf);
    }
    public static void emptyLineFilter(KsMessage message) {
        List<KsMessageLine> lines = new ArrayList<>();

        for (KsMessageLine line : message.getLines()) {
            if (line.getMessageSentences().size() > 0) {
                lines.add(line);
            }
        }

        message.setLines(lines);
    }

    public static void emptySentenceFilter(KsMessage message) {
        for (KsMessageLine line : message.getLines()) {
            List<KsMessageSentence> sentences = new ArrayList<>();

            for (KsMessageSentence sentence : line.getMessageSentences()) {
                if (sentence.getPeriods().size() > 0) {
                    sentences.add(sentence);
                }
            }

            line.setMessageSentences(sentences);
        }
    }

    public static void periodFilter(KsMessage message, HashSet<Integer> periods) {
        for (KsMessageLine line : message.getLines()) {
            for (KsMessageSentence sentence : line.getMessageSentences()) {
                List<PeriodOfDay> periodOfDays = new ArrayList<>();

                for (PeriodOfDay periodOfDay : sentence.getPeriods()) {
                    if (periodCross(periodOfDay.getPeriodRange(), periods)) {
                        periodOfDays.add(periodOfDay);
                    }
                }

                sentence.setPeriods(periodOfDays);
            }
        }
    }

    private static boolean periodCross(ClassPeriodRange periodRange, HashSet<Integer> periods) {
        for (int period : periods) {
            if (period >= periodRange.getStart() && period <= periodRange.getEnd()) {
                return true;
            }
        }

        return false;
    }

    public static void weekdayFilter(KsMessage message, HashSet<Weekday> weekdays) {
        List<KsMessageLine> lines = message.getLines();

        for (KsMessageLine line : lines) {
            List<KsMessageSentence> sentences = new ArrayList<>();

            for (KsMessageSentence sentence : line.getMessageSentences()) {
                if (weekdays.contains(sentence.getWeekday())) {
                    sentences.add(sentence);
                }
            }

            line.setMessageSentences(sentences);
        }

    }

    public static KsMessageLine toMessageLine(ClassTerm term) {
        return KsMessageLine.builder()
                .duration(term.getDuration())
                .messageSentences(toMessageSentenceList(term.getWeekData()))
                .build();
    }

    public static List<KsMessageSentence> toMessageSentenceList(WeekData weekData) {
        List<KsMessageSentence> lines = new ArrayList<>();

        for (Weekday weekday : Weekday.values()) {
            DayOfWeekData dayOfWeekData = weekData.getData(weekday);
            if (dayOfWeekData.getData().size() > 0) {
                lines.add(
                        KsMessageSentence.builder()
                                .periods(dayOfWeekData.getData())
                                .weekday(weekday)
                                .build());
            }
        }

        return lines;
    }
}
