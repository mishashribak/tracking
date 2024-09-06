package com.blazma.logistics.utilities;

public class TimeUtil {

    public static String convertToHoursAndMinutes(Integer minutes) {
        if(minutes==null) return "-";
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        String result = "";
        if (hours > 0) {
            result += hours + " hour";
        }
        result += " " +remainingMinutes + " minutes";

        return result.trim();
    }
}
