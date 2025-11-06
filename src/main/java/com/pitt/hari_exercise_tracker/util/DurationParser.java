package com.pitt.hari_exercise_tracker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {
    /**
     * Parses duration strings like "1h30m", "45m", "2h", "90" into total minutes.
     * Returns 0 if parsing fails.
     */
    public static int parseToMinutes(String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return 0;
        }

        duration = duration.trim().toLowerCase();
        int totalMinutes = 0;

        try {
            // Pattern: matches "1h30m", "2h", "45m", etc.
            Pattern pattern = Pattern.compile("(\\d+)\\s*h|(\\d+)\\s*m");
            Matcher matcher = pattern.matcher(duration);

            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    // Hours found
                    totalMinutes += Integer.parseInt(matcher.group(1)) * 60;
                }
                if (matcher.group(2) != null) {
                    // Minutes found
                    totalMinutes += Integer.parseInt(matcher.group(2));
                }
            }

            // If no pattern matched, try parsing as plain number (assume minutes)
            if (totalMinutes == 0 && duration.matches("\\d+")) {
                totalMinutes = Integer.parseInt(duration);
            }

            return totalMinutes;
        } catch (Exception e) {
            return 0;
        }
    }
}
