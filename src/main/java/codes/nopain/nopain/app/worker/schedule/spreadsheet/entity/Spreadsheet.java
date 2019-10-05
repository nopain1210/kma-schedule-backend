package codes.nopain.nopain.app.worker.schedule.spreadsheet.entity;

import codes.nopain.nopain.app.database.pojo.schedule.ClassTerm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Spreadsheet {
    private String spreadSheetId;
    private List<ClassTerm> sheets;

    public Spreadsheet() {
        this.sheets = new ArrayList<>();
    }
}
