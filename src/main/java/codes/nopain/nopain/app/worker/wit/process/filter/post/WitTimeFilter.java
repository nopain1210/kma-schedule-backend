package codes.nopain.nopain.app.worker.wit.process.filter.post;

import codes.nopain.nopain.app.controller.exception.NoContentException;
import codes.nopain.nopain.app.database.document.UserSetting;
import codes.nopain.nopain.app.database.pojo.setting.PeriodDurationMap;
import codes.nopain.nopain.app.database.repository.UserSettingsRepository;
import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import codes.nopain.nopain.app.worker.wit.process.utils.WitTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WitTimeFilter {
    private final UserSettingsRepository userSettingsRepository;

    private PeriodDurationMap periodDurationMap;
    private WitMessage message;

    public WitTimeFilter user(String email) {
        UserSetting userSetting = userSettingsRepository.findByEmail(email)
                .orElseThrow(NoContentException::new);
        this.periodDurationMap = userSetting.getPeriodDurationMap();

        return this;
    }

    public WitTimeFilter message(WitMessage message) {
        this.message = message;

        return this;
    }

    public WitTimeFilter classPeriodDurationFilter() {
        List<WitEntity> src = this.message.getEntities();
        List<Integer> founds = new ArrayList<>();

        for (int idx = 0; idx < src.size() - 2; idx++) {

            WitEntity start = src.get(idx);
            WitEntity prep = src.get(idx + 1);
            WitEntity end = src.get(idx + 2);

            if (start.getEntityType().equals(WitEntityType.CLASS_PERIOD)
                    && prep.getEntityType().equals(WitEntityType.PREPOSITION)
                    && end.getEntityType().equals(WitEntityType.CLASS_PERIOD)) {

                founds.add(idx);
            }
        }

        founds.sort(Collections.reverseOrder());

        for (int idx : founds) {
            WitEntity start = src.get(idx);
            WitEntity end = src.get(idx + 2);
            src.remove(idx);
            src.remove(idx);
            src.remove(idx);
            int startPeriod = Integer.parseInt(start.getValue());
            int endPeriod= Integer.parseInt(end.getValue());
            int ins = idx;

            for (int period = startPeriod; period <= endPeriod; period++) {
                src.add(ins,
                        WitEntity.builder()
                                .entityType(WitEntityType.CLASS_PERIOD)
                                .value(String.valueOf(period))
                                .build()
                );
                ins++;
            }
        }

        return this;
    }

    public WitTimeFilter classShiftDurationFilter() {
        List<WitEntity> src = this.message.getEntities();
        List<Integer> founds = new ArrayList<>();

        for (int idx = 0; idx < src.size() - 2; idx++) {

            WitEntity start = src.get(idx);
            WitEntity prep = src.get(idx + 1);
            WitEntity end = src.get(idx + 2);

            if (start.getEntityType().equals(WitEntityType.CLASS_SHIFT)
                    && prep.getEntityType().equals(WitEntityType.PREPOSITION)
                    && end.getEntityType().equals(WitEntityType.CLASS_SHIFT)) {

                founds.add(idx);
            }
        }

        founds.sort(Collections.reverseOrder());

        for (int idx : founds) {
            WitEntity start = src.get(idx);
            WitEntity end = src.get(idx + 2);
            src.remove(idx);
            src.remove(idx);
            src.remove(idx);
            int startShift = Integer.parseInt(start.getValue());
            int endShift = Integer.parseInt(end.getValue());
            int ins = idx;

            for (int shift = startShift; shift <= endShift; shift++) {
                src.add(ins,
                        WitEntity.builder()
                                .entityType(WitEntityType.CLASS_SHIFT)
                                .value(String.valueOf(shift))
                                .build()
                );
                ins++;
            }
        }

        return this;
    }

    public WitTimeFilter classShiftToPeriodFilter() {
        List<WitEntity> src = this.message.getEntities();
        List<WitEntity> dst = new ArrayList<>();

        for (WitEntity entity : src) {
            if (entity.getEntityType().equals(WitEntityType.CLASS_SHIFT)) {
                ClassPeriodRange periodRange = WitTimeUtils.getPeriodRange(Integer.parseInt(entity.getValue()));

                for (int period = periodRange.getStart(); period <= periodRange.getEnd(); period++) {
                    dst.add(
                            WitEntity.builder()
                                    .entityType(WitEntityType.CLASS_PERIOD)
                                    .value(String.valueOf(period))
                                    .build()
                    );
                }
            } else {
                dst.add(entity);
            }
        }

        this.message.setEntities(dst);

        return this;
    }

    public WitTimeFilter timeDurationToShiftFilter() {
        List<WitEntity> src = this.message.getEntities();

        for (int idx = 0; idx < src.size() - 2; idx++) {
            WitEntity start = src.get(idx);
            WitEntity prep = src.get(idx + 1);
            WitEntity end = src.get(idx + 2);

            if (start.getEntityType().equals(WitEntityType.EXACT_TIME)
                    && prep.getEntityType().equals(WitEntityType.PREPOSITION)
                    && end.getEntityType().equals(WitEntityType.EXACT_TIME)) {
                LocalTime startTime = WitTimeUtils.parseTime(start.getValue());
                LocalTime endTime = WitTimeUtils.parseTime(end.getValue());

                ClassPeriodRange range = WitTimeUtils.getPeriodRange(startTime, endTime, periodDurationMap);

                src.get(idx).setEntityType(WitEntityType.CLASS_PERIOD);
                src.get(idx).setValue(String.valueOf(range.getStart()));

                src.get(idx + 2).setEntityType(WitEntityType.CLASS_PERIOD);
                src.get(idx + 2).setValue(String.valueOf(range.getEnd()));
            }
        }

        return this;
    }
}
