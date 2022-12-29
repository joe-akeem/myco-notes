package ch.joeakeem.myconotes.domain;

import java.time.LocalDate;

public class RowBuilder {

    private String taskId;
    private String taskName;
    private String resource;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long duration;
    private Integer percentComplete = 100;
    private String dependencies;

    public RowBuilder setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public RowBuilder setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public RowBuilder setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public RowBuilder setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public RowBuilder setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public RowBuilder setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public RowBuilder setPercentComplete(Integer percentComplete) {
        this.percentComplete = percentComplete;
        return this;
    }

    public RowBuilder setDependencies(String dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public Row createRow() {
        return new Row(taskId, taskName, resource, startDate, endDate, duration, percentComplete, dependencies);
    }
}
