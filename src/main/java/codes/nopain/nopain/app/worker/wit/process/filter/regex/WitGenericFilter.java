package codes.nopain.nopain.app.worker.wit.process.filter.regex;

import codes.nopain.nopain.app.worker.wit.process.message.entity.WitEntity;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import codes.nopain.nopain.app.worker.wit.process.message.enums.WitEntityType;

import java.util.ArrayList;
import java.util.List;

public class WitGenericFilter {
    public static void fillNonClassified(WitMessage message) {
        String text = message.getText();
        List<WitEntity> source = message.getEntities();
        List<WitEntity> dest = new ArrayList<>();

        if (source.size() == 0) {
            message.getEntities().add(
                    WitEntity.builder()
                            .entityType(WitEntityType.UNCLASSIFIED)
                            .value("")
                            .start(0)
                            .end(message.getText().length())
                            .build()
            );

            return;
        }

        WitEntity first = source.get(0);

        if (first.getStart() != 0) {
            WitEntity entity = WitEntity.builder()
                    .entityType(WitEntityType.UNCLASSIFIED)
                    .start(0)
                    .value("")
                    .end(first.getStart())
                    .build();
            dest.add(entity);
        }

        dest.add(first);

        for (int i = 1; i < source.size(); i++) {
            WitEntity curr = source.get(i);
            WitEntity prev = source.get(i - 1);

            if (curr.getStart() != prev.getEnd()) {
                WitEntity entity = WitEntity.builder()
                        .entityType(WitEntityType.UNCLASSIFIED)
                        .value("")
                        .start(prev.getEnd())
                        .end(curr.getStart())
                        .build();
                dest.add(entity);
            }

            dest.add(curr);
        }

        WitEntity last = source.get(source.size() - 1);

        if (last.getEnd() != text.length()) {
            WitEntity entity = WitEntity.builder()
                    .entityType(WitEntityType.UNCLASSIFIED)
                    .value("")
                    .start(last.getEnd())
                    .end(text.length())
                    .build();
            dest.add(entity);
        }

        message.setEntities(dest);
    }
}
