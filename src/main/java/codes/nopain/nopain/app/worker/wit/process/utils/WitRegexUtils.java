package codes.nopain.nopain.app.worker.wit.process.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WitRegexUtils {
    public static List<Integer> parseDay(String source) {
        List<Integer> days = new ArrayList<>();
        int idx = source.length() - 1;

        while (source.charAt(idx) == ' ' || Character.isDigit(source.charAt(idx))) {
            idx--;

            if (idx < 0) {
                idx = 0;
                break;
            }
        }

        String text = source.substring(idx);

        return extractNumber(text);
    }

    public static List<Integer> extractNumber(String source) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(source);
        List<Integer> numberList = new ArrayList<>();

        while (matcher.find()) {
            numberList.add(Integer.parseInt(matcher.group()));
        }

        return numberList;
    }
}
