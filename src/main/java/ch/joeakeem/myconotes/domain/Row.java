package ch.joeakeem.myconotes.domain;

import java.time.LocalDate;
import javax.annotation.Nonnull;

public class Row {

    @Nonnull
    private final String taskId;

    @Nonnull
    private final String taskName;

    private final String resource;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Long duration;
    private final Integer percentComplete;
    private final String dependencies;

    Row(
        @Nonnull String taskId,
        @Nonnull String taskName,
        String resource,
        LocalDate startDate,
        LocalDate endDate,
        Long duration,
        Integer percentComplete,
        String dependencies
    ) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.resource = resource;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.percentComplete = percentComplete;
        this.dependencies = dependencies;
    }

    @Nonnull
    public String getTaskId() {
        return taskId;
    }

    @Nonnull
    public String getTaskName() {
        return taskName;
    }

    public String getResource() {
        return resource;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Long getDuration() {
        return duration;
    }

    public Integer getPercentComplete() {
        return percentComplete;
    }

    public String getDependencies() {
        return dependencies;
    }
}
