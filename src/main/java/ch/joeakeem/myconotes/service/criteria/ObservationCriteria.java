package ch.joeakeem.myconotes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ch.joeakeem.myconotes.domain.Observation} entity. This class is used
 * in {@link ch.joeakeem.myconotes.web.rest.ObservationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /observations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ObservationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter observationDate;

    private StringFilter title;

    private LongFilter imagesId;

    private LongFilter experimentId;

    private Boolean distinct;

    public ObservationCriteria() {}

    public ObservationCriteria(ObservationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.observationDate = other.observationDate == null ? null : other.observationDate.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.imagesId = other.imagesId == null ? null : other.imagesId.copy();
        this.experimentId = other.experimentId == null ? null : other.experimentId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ObservationCriteria copy() {
        return new ObservationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getObservationDate() {
        return observationDate;
    }

    public LocalDateFilter observationDate() {
        if (observationDate == null) {
            observationDate = new LocalDateFilter();
        }
        return observationDate;
    }

    public void setObservationDate(LocalDateFilter observationDate) {
        this.observationDate = observationDate;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public LongFilter getImagesId() {
        return imagesId;
    }

    public LongFilter imagesId() {
        if (imagesId == null) {
            imagesId = new LongFilter();
        }
        return imagesId;
    }

    public void setImagesId(LongFilter imagesId) {
        this.imagesId = imagesId;
    }

    public LongFilter getExperimentId() {
        return experimentId;
    }

    public LongFilter experimentId() {
        if (experimentId == null) {
            experimentId = new LongFilter();
        }
        return experimentId;
    }

    public void setExperimentId(LongFilter experimentId) {
        this.experimentId = experimentId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ObservationCriteria that = (ObservationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(observationDate, that.observationDate) &&
            Objects.equals(title, that.title) &&
            Objects.equals(imagesId, that.imagesId) &&
            Objects.equals(experimentId, that.experimentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, observationDate, title, imagesId, experimentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObservationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (observationDate != null ? "observationDate=" + observationDate + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (imagesId != null ? "imagesId=" + imagesId + ", " : "") +
            (experimentId != null ? "experimentId=" + experimentId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
