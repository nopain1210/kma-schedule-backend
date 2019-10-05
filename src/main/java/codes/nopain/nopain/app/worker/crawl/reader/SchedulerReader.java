package codes.nopain.nopain.app.worker.crawl.reader;

import codes.nopain.nopain.app.worker.crawl.client.SoupClient;
import codes.nopain.nopain.app.worker.crawl.excel.ScheduleRow;
import codes.nopain.nopain.app.worker.crawl.exception.SoupException;
import codes.nopain.nopain.app.worker.crawl.utils.EnumParser;
import codes.nopain.nopain.app.worker.crawl.utils.RegexUtils;
import codes.nopain.nopain.app.worker.global.entity.ClassPeriodRange;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import codes.nopain.nopain.app.worker.schedule.utils.GroupBy;
import codes.nopain.nopain.app.worker.schedule.utils.TableUtils;
import codes.nopain.nopain.app.database.pojo.setting.KmaAccount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class SchedulerReader implements AutoCloseable {
    private static final int WEEKDAY_COL = 0;
    private static final int SUBJECT_ID_COL = 1;
    private static final int SUBJECT_NAME_COL = 3;
    private static final int CLASSNAME_COL = 4;
    private static final int TEACHER_COL = 7;
    private static final int CLASS_SHIFTS_COL = 8;
    private static final int CLASSROOM_COL = 9;
    private static final int DURATION_COL = 10;
    private static final int START_ROW = 10;
    private final SoupClient client;
    private POIFSFileSystem pfs;
    private Workbook workbook;
    private InputStream scheduleAsStream;
    @Getter
    private String scheduleSemester;
    @Getter
    private List<ScheduleRow> scheduleAsTable;

    public SchedulerReader authenticate(KmaAccount account) throws SoupException {
        try {
            this.client.login(account.getUsername(), account.getPassword());
        } catch (SoupException se) {
            throw se;
        } catch (Exception ex) {
            throw new SoupException(500);
        }
        return this;
    }

    public SchedulerReader getDataFromKmaServer() throws IOException, SoupException {
        this.scheduleAsStream = client
                .getScheduleAsStream()
                .getInputStream();
        this.scheduleSemester = client.getSemester();
        return this;
    }

    public SchedulerReader getDataFromScheduleStream() throws SoupException {
        try {
            pfs = new POIFSFileSystem(this.scheduleAsStream);
            this.workbook = new HSSFWorkbook(pfs);

            Sheet sheet = workbook.getSheetAt(0);
            this.scheduleAsTable = new ArrayList<>();

            for (int rowIdx = START_ROW; sheet.getRow(rowIdx) != null; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                Weekday weekday = EnumParser.parseWeekday(
                        Integer.parseInt(getCellValue(row, WEEKDAY_COL))
                );
                String subjectName = getCellValue(row, SUBJECT_NAME_COL);
                String className = getCellValue(row, CLASSNAME_COL);
                String classroom = getCellValue(row, CLASSROOM_COL);
                String teacher = getCellValue(row, TEACHER_COL);
                ClassPeriodRange periodRange = EnumParser.parsePeriodRange(getCellValue(row, CLASS_SHIFTS_COL));
                DateDuration duration = RegexUtils.parseDateDuration(getCellValue(row, DURATION_COL));

                this.scheduleAsTable.add(
                        ScheduleRow.builder()
                                .weekday(weekday)
                                .subject(subjectName)
                                .className(className)
                                .classroom(classroom)
                                .teacher(teacher)
                                .periodRange(periodRange)
                                .dateDuration(duration)
                                .build()
                );
            }

            sortSchedule();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SoupException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return this;
    }

    private void sortSchedule() {
        this.scheduleAsTable.sort(new ScheduleRowComparatorByClassname());

        for (List<ScheduleRow> sub : TableUtils.slice(this.scheduleAsTable, GroupBy.CLASS_NAME)) {
            sub.sort(new ScheduleRowComparatorByDuration());
        }
    }

    private String getCellValue(Row row, int cellIdx) {
        Cell cell = row.getCell(cellIdx);

        if (cell == null) {
            return "";
        }

        return cell.getStringCellValue().trim();
    }

    @Override
    public void close() {
        try {
            this.workbook.close();
            this.pfs.close();
            this.scheduleAsStream.close();
        } catch (Exception ignored) {

        }
    }
}
