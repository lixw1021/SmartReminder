package com.xianwei;

import java.util.Calendar;

/**
 * Created by xianwei li on 10/30/2017.
 */

public class ExtractUtil {
    public static int getHour(String time) {
        String[] timeArray = time.split(":");
        if (timeArray.length > 0) {
            String hourString = timeArray[0];
            if (hourString.matches("\\d+")) {
                return Integer.parseInt(hourString);
            }
        }
        return 0;
    }

    public static int getMinute(String time) {
        String[] timeArray = time.split(":");
        if (timeArray.length > 1) {
            String hourString = timeArray[1];
            if (hourString.matches("\\d+")) {
                return Integer.parseInt(hourString);
            }
        }
        return 0;
    }

    public static int getDay(String voiceInput) {
        String[] wordsArray = voiceInput.split(" ");
        for (int i = 1; i < wordsArray.length; i++) {
            if (wordsArray[i].equals("day") || wordsArray[i].equals("days") ) {
                if (wordsArray[i - 1].matches("\\d+")) {
                    return Integer.parseInt(wordsArray[i -1]);
                }
            }
        }
        return -1;
    }

    public static int extractWeekday(String voiceInput) {
        if (voiceInput == null || voiceInput.isEmpty()) return -1;

        if (voiceInput.contains("monday")) {
            return Calendar.MONDAY;
        } else if (voiceInput.contains("tuesday")) {
            return Calendar.TUESDAY;
        } else if (voiceInput.contains("wednesday")) {
            return Calendar.WEDNESDAY;
        } else if (voiceInput.contains("thursday")) {
            return Calendar.THURSDAY;
        } else if (voiceInput.contains("friday")) {
            return Calendar.FRIDAY;
        } else if (voiceInput.contains("saturday")) {
            return Calendar.SATURDAY;
        } else if (voiceInput.contains("sunday")) {
            return Calendar.SUNDAY;
        }
        return -1;
    }

    public static int[] extractMonthAndDay(String voiceInput) {
        int month = -1;
        int day = -1;
        int[] monthAndDay = new int[2];
        monthAndDay[0] = month;
        monthAndDay[1] = day;
        if (voiceInput == null || voiceInput.isEmpty()) return monthAndDay;

        if (voiceInput.contains("january")) {
            monthAndDay[0] = Calendar.JANUARY;
            monthAndDay[1] = getDayOfMonth(voiceInput, "january");
            return monthAndDay;
        } else if (voiceInput.contains("february")) {
            monthAndDay[0] = Calendar.FEBRUARY;
            monthAndDay[1] = getDayOfMonth(voiceInput, "february");
            return monthAndDay;
        } else if (voiceInput.contains("march")) {
            monthAndDay[0] = Calendar.MARCH;
            monthAndDay[1] = getDayOfMonth(voiceInput, "march");
            return monthAndDay;
        } else if (voiceInput.contains("april")) {
            monthAndDay[0] = Calendar.APRIL;
            monthAndDay[1] = getDayOfMonth(voiceInput, "april");
            return monthAndDay;
        } else if (voiceInput.contains("may")) {
            monthAndDay[0] = Calendar.MAY;
            monthAndDay[1] = getDayOfMonth(voiceInput, "may");
            return monthAndDay;
        } else if (voiceInput.contains("june")) {
            monthAndDay[0] = Calendar.JUNE;
            monthAndDay[1] = getDayOfMonth(voiceInput, "june");
            return monthAndDay;
        } else if (voiceInput.contains("july")) {
            monthAndDay[0] = Calendar.JULY;
            monthAndDay[1] = getDayOfMonth(voiceInput, "july");
            return monthAndDay;
        } else if (voiceInput.contains("august")) {
            monthAndDay[0] = Calendar.AUGUST;
            monthAndDay[1] = getDayOfMonth(voiceInput, "august");
            return monthAndDay;
        } else if (voiceInput.contains("september")) {
            monthAndDay[0] = Calendar.SEPTEMBER;
            monthAndDay[1] = getDayOfMonth(voiceInput, "september");
            return monthAndDay;
        } else if (voiceInput.contains("october")) {
            monthAndDay[0] = Calendar.OCTOBER;
            monthAndDay[1] = getDayOfMonth(voiceInput, "october");
            return monthAndDay;
        } else if (voiceInput.contains("november")) {
            monthAndDay[0] = Calendar.NOVEMBER;
            monthAndDay[1] = getDayOfMonth(voiceInput, "november");
            return monthAndDay;
        } else if (voiceInput.contains("december")) {
            monthAndDay[0] = Calendar.DECEMBER;
            monthAndDay[1] = getDayOfMonth(voiceInput, "december");
            return monthAndDay;
        }
        return monthAndDay;
    }

    private static int getDayOfMonth(String voiceInput, String month) {
        String[] words = voiceInput.split(" ");
        int wordsLength = words.length;
        if (wordsLength < 2) return -1;

        for (int i = 0; i < wordsLength; i++) {
            if (words[i].equals(month)) {
                if (i == 0 && i + 1 < wordsLength) {
                    return getDayFromString(words[i + 1]);

                } else if (i > 0 && i + 1 < wordsLength) {
                    return getDayFromString(words[i - 1]) > -1 ?
                            getDayFromString(words[i - 1]) : getDayFromString(words[i + 1]);

                } else if (i == wordsLength - 1 && i - 1 > 0) {
                    return getDayFromString(words[i - 1]);
                }
            }
        }
        return -1;
    }

    private static int getDayFromString(String word) {
        int day = -1;
        if (word.isEmpty()) return day;
        char[] charArray = word.toCharArray();
        if (Character.isDigit(charArray[0])) {
            day = Character.getNumericValue(charArray[0]);
        }
        if (charArray.length > 1 && Character.isDigit(charArray[1])) {
            day = day * 10 + Character.getNumericValue(charArray[1]);
        }
        return day;
    }
}
