package codes.nopain.nopain.app.controller.utils;

import codes.nopain.nopain.app.database.document.ClassSchedule;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassListUtils {
    public static Set<String> getSemesterSet(List<ClassSchedule> classList) {

        return classList.stream()
                .map(ClassSchedule::getSemester).collect(Collectors.toSet());
    }

    public static Set<String> getGroupSet(List<ClassSchedule> classList) {

        return classList.stream()
                .map(ClassSchedule::getGroup).collect(Collectors.toSet());
    }

    public static Set<String> getSubjectSet(List<ClassSchedule> classList) {

        return classList.stream()
                .map(ClassSchedule::getSubject).collect(Collectors.toSet());
    }

    public static List<ClassSchedule> getClassesBySemester(List<ClassSchedule> classList, String semester) {

        return classList.stream()
                .filter(ksClass -> ksClass.getSemester().equals(semester)).collect(Collectors.toList());
    }

    public static List<ClassSchedule> getClassesByGroup(List<ClassSchedule> classList, String group) {

        return classList.stream()
                .filter(ksClass -> ksClass.getGroup().equals(group)).collect(Collectors.toList());
    }

    public static List<ClassSchedule> getClassesBySubject(List<ClassSchedule> classList, String subject) {

        return classList.stream()
                .filter(ksClass -> ksClass.getSubject().equals(subject)).collect(Collectors.toList());
    }
}
