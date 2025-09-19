package com.madebawojo.nysc.ppa.clearance.util;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public interface DateFormatUtil {

    /**
     * Takes in a LocalDate and converts it to a readable string
     * with an ordinal day suffix (e.g., "15th September 2025")
     */
    static String formatDateToReadableString(LocalDate date) {
        String dayWithSuffix = getDayWithOrdinal(date.getDayOfMonth());
        String monthAndYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        return dayWithSuffix + " " + monthAndYear;
    }

    /**
     * Returns the day of the month with its ordinal suffix (e.g., "1st", "2nd", "3rd", "4th", etc.)
     */
    private static String getDayWithOrdinal(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        return switch (day % 10) {
            case 1 -> day + "st";
            case 2 -> day + "nd";
            case 3 -> day + "rd";
            default -> day + "th";
        };
    }
}


//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;

//@Service
//public interface DateFormatUtil {
//    /**
//     * Takes in a LocalDate type and converts it a readable string
//     * e.g 2025-09-15 is converted 15 September 2025
//     * */
//    public static String formatDateToReadableString(LocalDate date){
//        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
//        return customFormatter.format(date);
//    }
//}
