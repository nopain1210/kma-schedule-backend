package codes.nopain.nopain.app.worker.schedule.utils;

import codes.nopain.nopain.app.controller.exception.InternalServerErrorException;
import codes.nopain.nopain.app.controller.exception.ServiceUnavailableException;
import codes.nopain.nopain.app.controller.exception.UnauthorizedException;
import codes.nopain.nopain.app.worker.crawl.exception.SoupException;
import codes.nopain.nopain.app.worker.schedule.classes.ClassListBuilder;
import codes.nopain.nopain.app.worker.schedule.spreadsheet.builder.SpreadsheetBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class ScheduleUtils {
    private final SpreadsheetBuilder spreadsheetBuilder;
    private final ClassListBuilder classListBuilder;

    public void syncSchedule(Principal principal) {
        int error = 0;

        try {
            classListBuilder
                    .user(principal.getName())
                    .syncScheduleWithKmaServer();
            spreadsheetBuilder
                    .user(principal.getName())
                    .buildSpreadsheet();
        } catch (SoupException ex) {
            error = Integer.parseInt(ex.getMessage());
        } catch (Exception e) {
            error = 500;
        }

        if (error == 0) {
            return;
        }

        if (error == 401) {
            throw new UnauthorizedException();
        } else {
            throw new InternalServerErrorException();
        }
    }
}
