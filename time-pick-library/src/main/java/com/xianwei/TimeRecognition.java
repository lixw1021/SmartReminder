package com.xianwei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Copyright [2017] [xianwei li]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 * Created by xianwei li on 10/30/2017.
 */
public class TimeRecognition {
    private String voiceInput;
    private List<String> wordsArray;
    private int pickedMinute = -1;
    private int pickedHour = -1;
    private int pickedDay = -1;
    private int pickedMonth = -1;
    private int pickedYear = -1;
    private int currentMinute;
    private int currentHour;
    private int currentDayOfMonth;
    private int currentDayOfWeek;
    private int currentMonth;
    private int currentYear;
    private long currentMillisecond;
    private long todayMillisecond;
    private long targetDayMillisecond = -1;
    private long targetMillisecond = -1;
    private boolean hasTime;
    private boolean hasDate;
    private Calendar calendar;

    public TimeRecognition(String voiceInput) {
        setupCurrentTime();

        if (voiceInput.contains("a.m.") || voiceInput.contains("p.m.")) {
            extractFromPatternOne(voiceInput);
        } else if (voiceInput.contains("hour") || voiceInput.contains("minute")) {
            extractFromPatternTwo(voiceInput);
        } else {
            extractDate(voiceInput);
        }

        if (targetMillisecond > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(targetMillisecond);
            pickedYear = calendar.get(Calendar.YEAR);
            pickedMonth = calendar.get(Calendar.MONTH);
            pickedDay = calendar.get(Calendar.DAY_OF_MONTH);
            pickedHour = calendar.get(Calendar.HOUR_OF_DAY);
            pickedMinute = calendar.get(Calendar.MINUTE);

        }
    }

    /*
    * pattern one: string has "a.m." or "p.m."
    */
    private void extractFromPatternOne(String voiceInput) {
        wordsArray = new ArrayList<>(Arrays.asList(voiceInput.split(" ")));
        extractTime(wordsArray);
        extractDate(voiceInput);
        if (hasTime && hasDate && targetMillisecond < 0) {
            targetDayMillisecond = todayMillisecond;
        }
        targetMillisecond = targetDayMillisecond + toMillisecond(0, pickedHour, pickedMinute);
    }

    private void extractDate(String voiceInput) {
        int weekday = ExtractUtil.extractWeekday(voiceInput);
        if (weekday > 0) {
            if (weekday <= currentDayOfWeek) {
                targetDayMillisecond = todayMillisecond + toMillisecond(weekday + 7 - currentDayOfWeek, 0, 0);
            } else {
                targetDayMillisecond = todayMillisecond + toMillisecond((weekday - currentDayOfWeek), 0, 0);
            }
        }

        if (targetDayMillisecond < 0){
            int[] monthAndDay = ExtractUtil.extractMonthAndDay(voiceInput);
            pickedMonth = monthAndDay[0];
            pickedDay = monthAndDay[1];
            if (pickedMonth < currentMonth && pickedMonth > -1) {
                pickedYear = currentYear + 1;
            } else if (pickedMonth == currentMonth && pickedDay <= currentDayOfMonth) {
                pickedYear = currentYear + 1;
            } else if (pickedMonth == currentMonth && pickedDay > currentDayOfMonth) {
                pickedYear = currentYear;
            } else if (pickedMonth > currentMonth){
                pickedYear = currentYear;
            }

            if (pickedMonth > -1 && pickedDay > -1) {
                Calendar cal = calendar;
                cal.set(pickedYear, pickedMonth, pickedDay, 0 , 0, 0);
                targetDayMillisecond = cal.getTimeInMillis();
            }
        }

        if (targetDayMillisecond < 0) {
            if (voiceInput.contains("day after tomorrow")) {
                targetDayMillisecond = todayMillisecond + toMillisecond(2, 0, 0);
            } else if (voiceInput.contains("tomorrow")) {
                targetDayMillisecond = todayMillisecond + toMillisecond(1, 0, 0);
            } else if (voiceInput.contains("today")) {
                targetDayMillisecond = todayMillisecond;
            }
        }

        if (targetDayMillisecond < 0) {
            if (voiceInput.contains("day")) {
                pickedDay = ExtractUtil.getDay(voiceInput);
            }
            if (pickedDay > 0) {
                targetDayMillisecond = todayMillisecond + toMillisecond(pickedDay, 0, 0);
            }
        }

        if (targetDayMillisecond > 0) {
            hasDate = true;
            targetMillisecond = targetDayMillisecond;
        }
    }

    private void extractTime(List<String> wordsArray) {
        for (int i = 1; i < wordsArray.size(); i++) {
            if (wordsArray.get(i).equals("a.m.")) {
                String time = wordsArray.get(i - 1);
                pickedHour = ExtractUtil.getHour(time);
                pickedMinute = ExtractUtil.getMinute(time);
                hasDate = true;
                hasTime = true;
                wordsArray.remove(i);
                wordsArray.remove(i - 1);
                break;
            }
            if (wordsArray.get(i).equals("p.m.")) {
                String time = wordsArray.get(i - 1);
                pickedHour = ExtractUtil.getHour(time) + 12;
                pickedMinute = ExtractUtil.getMinute(time);
                hasDate = true;
                hasTime = true;
                wordsArray.remove(i);
                wordsArray.remove(i - 1);
                break;
            }
        }
    }

    private long toMillisecond(int days, int hours, int minute) {
        return (long) ((days * 24 + hours) * 60 + minute) * 60 * 1000;
    }

    /*
    *pattern two: string had "hour" or "minute"
    */
    private void extractFromPatternTwo(String voiceInput) {

        wordsArray = new ArrayList<>(Arrays.asList(voiceInput.split(" ")));
        for (int i = 1; i < wordsArray.size(); i++) {
            if (wordsArray.get(i).equals("minutes") || wordsArray.get(i).equals("minute")) {
                String minuteString = wordsArray.get(i - 1);
                if (minuteString.matches("\\d+")) {
                    int minute = Integer.parseInt(minuteString);
                    pickedMinute = minute % 60;
                    if (pickedHour < 0) pickedHour = 0;
                    pickedHour = pickedHour + minute / 60;
                    hasDate = true;
                    hasTime = true;
                }
            }
            if (wordsArray.get(i).equals("hours") || wordsArray.get(i).equals("hour")) {
                String hourString = wordsArray.get(i - 1);
                if (hourString.matches("\\d+")) {
                    int hour = Integer.parseInt(hourString);
                    if (pickedHour < 0) pickedHour = 0;
                    pickedHour = hour + pickedHour;
                    hasDate = true;
                    hasTime = true;
                } else if (hourString.equals("half")) {
                    pickedMinute = pickedMinute + 30;
                    hasDate = true;
                    hasTime = true;
                }
            }
        }

        if (hasTime && hasDate) {
            targetMillisecond = currentMillisecond + toMillisecond(0, pickedHour, pickedMinute);
        }
    }


    private void getTime(List<String> wordsArray) {
        if (wordsArray.size() < 2) return;
        for (int i = 1; i < wordsArray.size(); i++) {
            if (wordsArray.get(i).equals("a.m.")) {
                String time = wordsArray.get(i - 1);
                pickedHour = ExtractUtil.getHour(time);
                pickedMinute = ExtractUtil.getMinute(time);
                hasDate = true;
                hasTime = true;
                wordsArray.remove(i);
                wordsArray.remove(i - 1);
                break;
            }
            if (wordsArray.get(i).equals("p.m.")) {
                String time = wordsArray.get(i - 1);
                pickedHour = ExtractUtil.getHour(time) + 12;
                pickedMinute = ExtractUtil.getMinute(time);
                hasDate = true;
                hasTime = true;
                wordsArray.remove(i);
                wordsArray.remove(i - 1);
                break;
            }
            if (wordsArray.get(i).equals("minutes") || wordsArray.get(i).equals("minute")) {
                String minuteString = wordsArray.get(i - 1);
                if (minuteString.matches("\\d+")) {
                    int minute = Integer.parseInt(minuteString);
                    pickedMinute = minute % 60;
                    pickedHour = minute / 60;
                    hasDate = true;
                    hasTime = true;
                }
            }
            if (wordsArray.get(i).equals("hours") || wordsArray.get(i).equals("hour")) {
                String hourString = wordsArray.get(i - 1);
                if (hourString.matches("\\d+")) {
                    int hour = Integer.parseInt(hourString);
                    pickedHour = hour + pickedHour;
                    hasDate = true;
                    hasTime = true;
                } else if (hourString.equals("half")) {
                    pickedMinute = pickedMinute + 30;
                    hasDate = true;
                    hasTime = true;
                }
            }
        }
    }


    private void setupCurrentTime() {
        calendar = Calendar.getInstance();
        currentMillisecond = calendar.getTimeInMillis();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, currentDayOfMonth, 0, 0, 0);
        todayMillisecond = cal.getTimeInMillis();
    }

    public int getPickedMinute() {
        return pickedMinute;
    }

    public int getPickedHour() {
        return pickedHour;
    }

    public int getPickedDay() {
        return pickedDay;
    }

    public int getPickedMonth() {
        return pickedMonth;
    }

    public int getPickedYear() {
        return pickedYear;
    }

    public boolean hasTime() {
        return hasTime;
    }

    public boolean hasDate() {
        return hasDate;
    }

    public long getTargetMillisecond() {
        return targetMillisecond;
    }

    public long getTodayMillisecond() {
        return todayMillisecond;
    }
}
