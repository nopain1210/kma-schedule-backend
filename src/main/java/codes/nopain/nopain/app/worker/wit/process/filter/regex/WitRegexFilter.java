package codes.nopain.nopain.app.worker.wit.process.filter.regex;

import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;
import codes.nopain.nopain.app.worker.wit.process.utils.WitDateUtils;
import codes.nopain.nopain.app.worker.wit.process.utils.WitRegexUtils;
import codes.nopain.nopain.app.worker.wit.process.utils.WitTimeUtils;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Getter
public class WitRegexFilter {
    private static final String EXACT_TIME_PATTERN = "(\\d{1,2}(h|g)(\\d{1,2})*)|(\\d{1,2}[ ]*(giờ)[ ]*(\\d{1,2})*)";
    private static final String SHORT_DATE_PATTERN = "\\d{1,2}/\\d{1,2}(/\\d{4})*";
    private static final String LONG_DATE_PATTERN = "\\d{1,2}[ ](tháng)[ ]*\\d{1,2}";
    private static final String MONTH_PATTERN = "(tháng)[ ]*\\d{1,2}";

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ttf = DateTimeFormatter.ofPattern("HH:mm");

    private WitMessage message;

    public WitRegexFilter message(WitMessage message) {
        this.message = message;

        return this;
    }

    public WitRegexFilter dayFilter() {
        String text = message.getText();
        List<WitEntity> src = message.getEntities();
        List<Integer> founds = new ArrayList<>();

        for (WitEntity entity : src) {
            int idx = src.indexOf(entity);

            if (entity.getEntityType().equals(WitEntityType.EXACT_DATE)) {
                if (idx == 0) {
                    continue;
                }

                int prv = idx - 1;

                while (src.get(prv).getEntityType().equals(WitEntityType.PREPOSITION)
                        || src.get(prv).getEntityType().equals(WitEntityType.UNCLASSIFIED)) {
                    prv--;

                    if (prv < 0) {
                        prv = 0;
                        break;
                    }
                }

                for (int i = prv; i < idx; i++) {
                    WitEntity current = src.get(i);

                    if (current.getEntityType().equals(WitEntityType.UNCLASSIFIED)) {
                        founds.add(i);
                    }
                }
            }
        }

        founds.sort(Collections.reverseOrder());

        for (int idx : founds) {
            WitEntity entity = src.get(idx);
            String source = text.substring(entity.getStart(), entity.getEnd());
            src.remove(idx);
            List<Integer> days = WitRegexUtils.parseDay(source);

            for (int i = 0; i < days.size(); i++) {
                src.add(idx + i,
                        WitEntity.builder()
                                .entityType(WitEntityType.EXACT_DAY)
                                .value(String.valueOf(days.get(i)))
                                .build());
            }
        }

        removeAndPrep();

        return this;
    }

    private void removeAndPrep() {
        List<WitEntity> src = message.getEntities();
        for (int idx = src.size() - 1; idx >= 0; idx--) {
            WitEntity entity = src.get(idx);

            if (entity.getEntityType().equals(WitEntityType.PREPOSITION)) {
                if (entity.getValue().equals("and")) {
                    src.remove(idx);
                }
            }
        }
    }

    public WitRegexFilter monthFilter() {
        Pattern pattern = Pattern.compile(MONTH_PATTERN);
        String text = message.getText();
        List<WitEntity> dst = new ArrayList<>();

        for (WitEntity entity : message.getEntities()) {
            if (entity.getEntityType().equals(WitEntityType.UNCLASSIFIED)) {
                String sub = text.substring(entity.getStart(), entity.getEnd());
                Matcher matcher = pattern.matcher(sub);

                while (matcher.find()) {
                    String group = matcher.group();
                    int start = entity.getStart() + matcher.start();
                    int end = start + group.length();
                    String value = WitRegexUtils.extractNumber(group).get(0).toString();

                    WitEntity monthEntity = WitEntity.builder()
                            .entityType(WitEntityType.EXACT_MONTH)
                            .value(value)
                            .start(start)
                            .end(end)
                            .build();
                    dst.add(monthEntity);
                }
            } else {
                dst.add(entity);
            }
        }

        message.setEntities(dst);
        WitGenericFilter.fillNonClassified(message);

        return this;
    }

    public WitRegexFilter exactTimeFilter() {
        Pattern pattern = Pattern.compile(EXACT_TIME_PATTERN);
        String text = message.getText();
        List<WitEntity> dst = new ArrayList<>();

        for (WitEntity entity : message.getEntities()) {
            if (entity.getEntityType().equals(WitEntityType.UNCLASSIFIED)) {
                String sub = text.substring(entity.getStart(), entity.getEnd());
                Matcher matcher = pattern.matcher(sub);

                while (matcher.find()) {
                    String group = matcher.group();
                    int start = entity.getStart() + matcher.start();
                    int end = start + group.length();
                    String value = WitTimeUtils.parseTime(group).format(ttf);

                    WitEntity dateEntity = WitEntity.builder()
                            .entityType(WitEntityType.EXACT_TIME)
                            .value(value)
                            .start(start)
                            .end(end)
                            .build();
                    dst.add(dateEntity);
                }
            } else {
                dst.add(entity);
            }
        }

        message.setEntities(dst);
        WitGenericFilter.fillNonClassified(message);

        return this;
    }

    public WitRegexFilter longDateFilter() {
        dateFilter(LONG_DATE_PATTERN);

        return this;
    }

    public WitRegexFilter shortDateFilter() {
        dateFilter(SHORT_DATE_PATTERN);

        return this;
    }

    private void dateFilter(String regex) {
        Pattern pattern = Pattern.compile(regex);
        String text = message.getText();
        List<WitEntity> dst = new ArrayList<>();

        for (WitEntity entity : message.getEntities()) {
            if (entity.getEntityType().equals(WitEntityType.UNCLASSIFIED)) {
                String sub = text.substring(entity.getStart(), entity.getEnd());
                Matcher matcher = pattern.matcher(sub);

                while (matcher.find()) {
                    String group = matcher.group();
                    int start = entity.getStart() + matcher.start();
                    int end = start + group.length();
                    String value = WitDateUtils.parseDate(group).format(dtf);

                    WitEntity dateEntity = WitEntity.builder()
                            .entityType(WitEntityType.EXACT_DATE)
                            .value(value)
                            .start(start)
                            .end(end)
                            .build();
                    dst.add(dateEntity);
                }
            } else {
                dst.add(entity);
            }
        }

        message.setEntities(dst);
        WitGenericFilter.fillNonClassified(message);
    }
}
