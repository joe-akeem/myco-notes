package ch.joeakeem.myconotes.domain;

import java.time.LocalDate;
import java.util.Objects;
import javax.annotation.Nonnull;

public class Row {

    private final String from;

    private final String to;

    private final Integer weight;

    private final String tooltip;

    public Row(@Nonnull String from, @Nonnull String to, @Nonnull Integer weight, @Nonnull String tooltip) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.weight = Objects.requireNonNull(weight);
        this.tooltip = Objects.requireNonNull(tooltip);
    }

    @Nonnull
    public String getFrom() {
        return from;
    }

    @Nonnull
    public String getTo() {
        return to;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getTooltip() {
        return tooltip;
    }
}
