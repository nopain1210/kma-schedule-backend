package codes.nopain.nopain.app.worker.wit.process.filter.post;

import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitRelativeDate;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import codes.nopain.nopain.app.worker.wit.process.utils.WitDateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class WitDateFilter {
    private static final DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static LocalDate now = LocalDate.now();

    private WitMessage message;

    public WitDateFilter message(WitMessage message) {
        this.message = message;

        return this;
    }

    public WitDateFilter monthToDurationFilter() {
        List<WitEntity> src = message.getEntities();
        List<Integer> founds = new ArrayList<>();

        for (int idx = 0; idx < src.size(); idx++) {
            if (src.get(idx).getEntityType().equals(WitEntityType.EXACT_MONTH)) {
                founds.add(idx);
            }
        }

        founds.sort(Collections.reverseOrder());

        for (int idx : founds) {
            WitEntity entity = src.get(idx);
            src.remove(idx);
            int month = Integer.parseInt(entity.getValue());
            LocalDate start = LocalDate.of(now.getYear(), month, 1);
            LocalDate end = LocalDate.of(start.getYear(), start.getMonth(), start.lengthOfMonth());

            src.add(idx,
                    WitEntity.builder()
                            .entityType(WitEntityType.EXACT_DATE)
                            .value(start.format(fullDateTimeFormatter))
                            .build()
            );
            src.add(idx + 1,
                    WitEntity.builder()
                            .entityType(WitEntityType.PREPOSITION)
                            .value("to")
                            .build()
            );
            src.add(idx + 2,
                    WitEntity.builder()
                            .entityType(WitEntityType.EXACT_DATE)
                            .value(end.format(fullDateTimeFormatter))
                            .build()
            );
        }

        return this;
    }

    public WitDateFilter monthDurationFilter() {
        List<WitEntity> src = message.getEntities();

        for (int idx = 0; idx < src.size(); idx++) {
            if (idx > src.size() - 3) {
                break;
            }

            WitEntity startEntity = src.get(idx);

            if (startEntity.getEntityType().equals(WitEntityType.EXACT_MONTH)) {
                WitEntity prep = src.get(idx + 1);
                WitEntity endEntity = src.get(idx + 2);
                LocalDate startDate = LocalDate.of(now.getYear(), Integer.parseInt(startEntity.getValue()), 1);

                if (prep.getEntityType().equals(WitEntityType.PREPOSITION)) {
                    if (endEntity.getEntityType().equals(WitEntityType.EXACT_MONTH)) {
                        LocalDate endDate = LocalDate.of(now.getYear(), Integer.parseInt(endEntity.getValue()), 1);
                        endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.lengthOfMonth());

                        src.get(idx).setEntityType(WitEntityType.EXACT_DATE);
                        src.get(idx).setValue(startDate.format(fullDateTimeFormatter));

                        src.get(idx + 2).setEntityType(WitEntityType.EXACT_DATE);
                        src.get(idx + 2).setValue(endDate.format(fullDateTimeFormatter));
                    }

                    if (endEntity.getEntityType().equals(WitEntityType.EXACT_DATE)) {
                        src.get(idx).setEntityType(WitEntityType.EXACT_DATE);
                        src.get(idx).setValue(startDate.format(fullDateTimeFormatter));
                    }
                }


            }
        }

        return this;
    }

    public WitDateFilter monthFilter() {
        List<WitEntity> src = message.getEntities();
        List<Integer> founds = new ArrayList<>();

        for (WitEntity entity : src) {
            if (entity.getEntityType().equals(WitEntityType.EXACT_MONTH)) {
                int idx = src.indexOf(entity);

                if (idx > 1) {
                    WitEntity prep = src.get(idx - 1);

                    if (prep.getEntityType().equals(WitEntityType.PREPOSITION)) {
                        WitEntity prvEntity = src.get(idx - 2);

                        if (prvEntity.getEntityType().equals(WitEntityType.EXACT_DATE)) {
                            founds.add(idx);
                        }
                    }
                }
            }
        }

        founds.sort(Collections.reverseOrder());

        for (int idx : founds) {
            WitEntity endEntity = src.get(idx);
            int month = Integer.parseInt(endEntity.getValue());
            src.remove(idx);
            src.remove(idx - 1);
            int startIdx = idx - 2;

            while (src.get(startIdx).getEntityType().equals(WitEntityType.EXACT_DATE)) {
                if (startIdx < 0) {
                    break;
                }

                int ins = startIdx;
                WitEntity startEntity = src.get(startIdx);
                LocalDate startDate = WitDateUtils.parseDate(startEntity.getValue());
                LocalDate endDate = LocalDate.of(startDate.getYear(), month, 1);
                ins++;
                src.add(ins,
                        WitEntity.builder()
                                .entityType(WitEntityType.PREPOSITION)
                                .value("to")
                                .build());
                ins++;
                src.add(ins,
                        WitEntity.builder()
                                .entityType(WitEntityType.EXACT_DATE)
                                .value(endDate.format(fullDateTimeFormatter))
                                .build());

                startIdx--;
            }

        }

        return this;
    }

    public WitDateFilter mergeDate() {
        List<WitEntity> src = message.getEntities();

        for (WitEntity entity : src) {
            if (entity.getEntityType().equals(WitEntityType.EXACT_DAY)) {
                int idx = src.indexOf(entity);

                while (!src.get(idx).getEntityType().equals(WitEntityType.EXACT_DATE)) {
                    idx++;

                    if (idx > src.size() - 1) {
                        idx = src.size() - 1;
                        break;
                    }
                }

                WitEntity m = src.get(idx);
                WitEntityType type = m.getEntityType();

                if (type.equals(WitEntityType.EXACT_DATE)) {
                    LocalDate date = WitDateUtils.parseDate(m.getValue());
                    entity.setEntityType(WitEntityType.EXACT_DATE);
                    entity.setValue(
                            LocalDate.of(
                                    date.getYear(),
                                    date.getMonth(),
                                    Integer.parseInt(entity.getValue())).format(fullDateTimeFormatter)
                    );
                }
            }
        }

        return this;
    }

    public WitDateFilter relativeDateFilter() {
        List<WitEntity> ori = message.getEntities();
        List<WitEntity> dst = new ArrayList<>();

        for (int idx = 0; idx < ori.size(); idx++) {
            WitEntity entity = ori.get(idx);

            if (entity.getEntityType().equals(WitEntityType.RELATIVE_DATE)) {
                WitRelativeDate witRelativeDate = WitRelativeDate.parseString(entity.getValue());

                if (witRelativeDate == null) {
                    continue;
                }

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                WitEntityType type = WitEntityType.EXACT_DATE;
                LocalDate now = LocalDate.now();

                switch (witRelativeDate) {
                    case TODAY:
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now))
                                        .build()
                        );
                        break;
                    case TOMORROW:
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.plusDays(1)))
                                        .build()
                        );
                        break;
                    case AFTER_TOMORROW:
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.plusDays(2)))
                                        .build()
                        );
                        break;
                    case THIS_WEEK:
                        int dayOfWeek = now.getDayOfWeek().getValue();
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.minusDays(dayOfWeek - 1)))
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.PREPOSITION)
                                        .value("to")
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.plusDays(7 - dayOfWeek)))
                                        .build()
                        );
                        break;
                    case NEXT_WEEK:
                        dayOfWeek = now.getDayOfWeek().getValue();
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.plusDays(7 - dayOfWeek + 1)))
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.PREPOSITION)
                                        .value("to")
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.plusDays(7 - dayOfWeek + 7)))
                                        .build()
                        );
                        break;
                    case AFTER_NEXT_WEEK:
                        dayOfWeek = now.getDayOfWeek().getValue();
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.plusDays(7 - dayOfWeek + 8)))
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.PREPOSITION)
                                        .value("to")
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now.plusDays(7 - dayOfWeek + 14)))
                                        .build()
                        );
                        break;
                    case THIS_MONTH:
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(now))
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.PREPOSITION)
                                        .value("to")
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(LocalDate.of(now.getYear(), now.getMonth(), now.lengthOfMonth())))
                                        .build()
                        );
                        break;
                    case NEXT_MONTH:
                        LocalDate nextMonth = LocalDate.of(now.getYear(), now.getMonth().plus(1), 1);
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(nextMonth))
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.PREPOSITION)
                                        .value("to")
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), nextMonth.lengthOfMonth())))
                                        .build()
                        );
                        break;
                    case AFTER_NEXT_MONTH:
                        LocalDate afterNextMonth = LocalDate.of(now.getYear(), now.getMonth().plus(2), 1);
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(afterNextMonth))
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.PREPOSITION)
                                        .value("to")
                                        .build()
                        );
                        dst.add(
                                WitEntity.builder()
                                        .entityType(type)
                                        .value(dtf.format(LocalDate.of(afterNextMonth.getYear(), afterNextMonth.getMonth(), afterNextMonth.lengthOfMonth())))
                                        .build()
                        );
                        break;
                    case MID_MONTH:
                        if (idx == ori.size() - 1) {
                            break;
                        }

                        WitEntity nxt = ori.get(idx + 1);

                        if (nxt.getEntityType() != WitEntityType.EXACT_MONTH) {
                            break;
                        }

                        int month = Integer.parseInt(nxt.getValue());

                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.EXACT_DATE)
                                        .value(dtf.format(LocalDate.of(now.getYear(), month, 15)))
                                        .build()
                        );
                        idx++;
                        break;
                    case OVER_MONTH:
                        if (idx == ori.size() - 1) {
                            break;
                        }

                        nxt = ori.get(idx + 1);

                        if (nxt.getEntityType() != WitEntityType.EXACT_MONTH) {
                            break;
                        }

                        month = Integer.parseInt(nxt.getValue());

                        dst.add(
                                WitEntity.builder()
                                        .entityType(WitEntityType.EXACT_DATE)
                                        .value(dtf.format(LocalDate.of(
                                                now.getYear(),
                                                month,
                                                LocalDate.of(now.getYear(), month, 1).lengthOfMonth()))
                                        )
                                        .build()
                        );
                        idx++;
                        break;
                }
            } else {
                dst.add(entity);
            }
        }

        message.setEntities(dst);

        return this;
    }
}
