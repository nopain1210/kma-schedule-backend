package codes.nopain.nopain.app.worker.wit.process.utils;

import codes.nopain.nopain.app.worker.wit.resource.response.WitResponseEntity;
import codes.nopain.nopain.app.worker.wit.resource.response.WitResponseEntityMap;
import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WitUtils {
    public static List<WitResponseEntity> getAllResponseEntity(WitResponseEntityMap entityMap) {
        List<WitResponseEntity> entities = new ArrayList<>();
        List<WitResponseEntity> classPeriod = entityMap.getClassPeriod();
        List<WitResponseEntity> weekday = entityMap.getWeekday();
        List<WitResponseEntity> classShift = entityMap.getClassShift();
        List<WitResponseEntity> timeAdverb = entityMap.getTimeAdverb();
        List<WitResponseEntity> partOfDay = entityMap.getPartOfDay();
        List<WitResponseEntity> relativeDate = entityMap.getRelativeDate();
        List<WitResponseEntity> preposition = entityMap.getPreposition();

        if (classPeriod != null) {
            entities.addAll(classPeriod);
        }

        if (weekday != null) {
            entities.addAll(weekday);
        }

        if (classShift != null) {
            entities.addAll(classShift);
        }

        if (timeAdverb != null) {
            entities.addAll(timeAdverb);
        }

        if (partOfDay != null) {
            entities.addAll(partOfDay);
        }

        if (relativeDate != null) {
            entities.addAll(relativeDate);
        }

        if (preposition != null) {
            entities.addAll(preposition);
        }

        entities.sort(Comparator.comparingInt(WitResponseEntity::getStart));

        return entities;
    }

    public static void removeNonClassified(WitMessage message) {
        List<WitEntity> ori = message.getEntities();
        List<WitEntity> dst = new ArrayList<>();

        for (WitEntity entity : ori) {
            if (entity.getEntityType() != WitEntityType.UNCLASSIFIED) {
                dst.add(entity);
            }
        }

        message.setEntities(dst);
    }
}
