package codes.nopain.nopain.app.worker.crawl.utils;

import codes.nopain.nopain.app.worker.global.entity.DateDuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static List<Integer> parseNumbers(String source) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        List<Integer> numList = new ArrayList<>();

        while (matcher.find()) {
            numList.add(Integer.parseInt(matcher.group()));
        }

        return numList;
    }

    public static DateDuration parseDateDuration(String source) {
        String regex = "\\d{1,2}/\\d{1,2}/\\d{4}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<LocalDate> dates = new ArrayList<>();

        while (matcher.find()) {
            dates.add(LocalDate.parse(matcher.group(), dtf));
        }

        return new DateDuration(dates.get(0), dates.get(1));
    }
}
