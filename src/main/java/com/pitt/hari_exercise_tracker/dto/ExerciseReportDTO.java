package com.pitt.hari_exercise_tracker.dto;

public class ExerciseReportDTO {
    private int totalExercises;
    private int totalMinutes;
    private String startDate;
    private String endDate;

    public ExerciseReportDTO(int totalExercises, int totalMinutes, String startDate, String endDate) {
        this.totalExercises = totalExercises;
        this.totalMinutes = totalMinutes;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getTotalExercises() { return totalExercises; }
    public void setTotalExercises(int totalExercises) { this.totalExercises = totalExercises; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
