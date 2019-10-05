package codes.nopain.nopain.app.controller.controllers.test;

import codes.nopain.nopain.app.database.document.ClassSchedule;
import codes.nopain.nopain.app.database.pojo.setting.KmaAccount;
import codes.nopain.nopain.app.database.repository.ClassesRepository;
import codes.nopain.nopain.app.database.repository.UserSchedulesRepository;
import codes.nopain.nopain.app.config.ScheduleProperties;
import codes.nopain.nopain.app.worker.crawl.exception.SoupException;
import codes.nopain.nopain.app.worker.schedule.spreadsheet.builder.SpreadsheetBuilder;
import codes.nopain.nopain.app.worker.schedule.spreadsheet.entity.Spreadsheet;
import codes.nopain.nopain.app.worker.schedule.classes.ClassListBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class SoupTestController {

    private final SpreadsheetBuilder spreadsheetBuilder;
    private final ClassListBuilder classListBuilder;
    private final ClassesRepository classesRepository;
    private final UserSchedulesRepository schedulesRepository;
    private final ScheduleProperties scheduleProperties;

    @GetMapping("/api/schedule/class/get/{id}")
    public ClassSchedule getClass(Principal principal, @PathVariable("id") String id) {
        return classesRepository.findById(id)
                .orElse(null);
    }

    @GetMapping("/api/schedule/class/get")
    public List<ClassSchedule> getClass(Principal principal) {
        return classesRepository.findAll();
    }

    @GetMapping("/api/schedule/spread")
    public Spreadsheet getSpreadsheet(Principal principal) {
        return Objects.requireNonNull(schedulesRepository.findByEmail(principal.getName()).orElse(null)).getSpreadsheet();
    }

    @GetMapping("/api/schedule/update")
    public void update(Principal principal, @RequestParam("username") String username, @RequestParam("password") String password) throws Exception {
        try {
            classListBuilder
                    .user(principal.getName())
                    .syncScheduleWithKmaServer(new KmaAccount(username, password));
            spreadsheetBuilder
                    .user(principal.getName())
                    .buildSpreadsheet();
            System.out.println(scheduleProperties.getDefaultAuthor());
        } catch (SoupException e) {
            e.printStackTrace();
        } finally {
            classListBuilder.close();
        }

    }
}
