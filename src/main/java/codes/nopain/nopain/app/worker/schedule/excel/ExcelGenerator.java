package codes.nopain.nopain.app.worker.schedule.excel;

import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import codes.nopain.nopain.app.database.pojo.schedule.DayOfWeekData;
import codes.nopain.nopain.app.database.pojo.schedule.PeriodOfDay;
import codes.nopain.nopain.app.database.pojo.schedule.WeekData;
import codes.nopain.nopain.app.worker.global.entity.DateDuration;
import codes.nopain.nopain.app.worker.global.enums.Weekday;
import codes.nopain.nopain.app.worker.schedule.spreadsheet.entity.Spreadsheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class ExcelGenerator {
    public void generate(Spreadsheet spreadsheet, OutputStream outputStream) throws IOException {
        List<ClassTerm> sheets = spreadsheet.getSheets();
        ClassPathResource classPathResource = new ClassPathResource("excel/Template.xlsx");
        File template = classPathResource.getFile();
        XSSFWorkbook workbook = new XSSFWorkbook(classPathResource.getInputStream());
        XSSFCreationHelper factory = workbook.getCreationHelper();
        DateTimeFormatter dtf;

        for (ClassTerm sheetData : sheets) {
            DateDuration duration = sheetData.getDuration();
            WeekData weekData = sheetData.getWeekData();
            XSSFSheet sheet = workbook.cloneSheet(0);
            dtf = DateTimeFormatter.ofPattern("dd-MM");

            String sheetName = String.format("%s đến %s", duration.getStart().format(dtf), duration.getEnd().format(dtf));
            int idx = workbook.getSheetIndex(sheet);
            workbook.setSheetName(idx, sheetName);

            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String sheetTitle = String.format("Từ %s đến %s", duration.getStart().format(dtf), duration.getEnd().format(dtf));
            sheet.getRow(1).getCell(1).setCellValue(sheetTitle);

            XSSFDrawing drawing = sheet.createDrawingPatriarch();

            for (Weekday weekday : Weekday.values()) {
                DayOfWeekData dayOfWeekData = weekData.getData(weekday);
                int weekId = weekday.getId();

                if (weekId == 0) {
                    weekId = 7;
                }

                int col = weekId + 1;

                for (PeriodOfDay periodOfDay : dayOfWeekData.getData()) {
                    int startRow = periodOfDay.getPeriodRange().getStart() + 3;
                    int endRow = periodOfDay.getPeriodRange().getEnd() + 3;

                    XSSFCell cell = sheet.getRow(startRow).getCell(col);
                    String value = String.format("%s \n %s", periodOfDay.getClassname(), periodOfDay.getClassroom());
                    cell.setCellValue(value);

                    XSSFClientAnchor anchor = factory.createClientAnchor();
                    anchor.setCol1(col);
                    anchor.setCol2(col + 1);
                    anchor.setRow1(startRow);
                    anchor.setRow2(endRow + 1);

                    XSSFComment comment = drawing.createCellComment(anchor);
                    comment.setString(periodOfDay.getClassroom());

                    cell.setCellComment(comment);

                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, col, col));
                }
            }
        }

        workbook.removeSheetAt(0);
        workbook.write(outputStream);
        workbook.close();
    }
}
