package ch.joeakeem.myconotes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ch.joeakeem.myconotes.domain.Image} entity. This class is used
 * in {@link ch.joeakeem.myconotes.web.rest.ImageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /images?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private LongFilter observationId;

    private LongFilter strainId;

    private LongFilter tekId;

    private Boolean distinct;

    public ImageCriteria() {}

    public ImageCriteria(ImageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.observationId = other.observationId == null ? null : other.observationId.copy();
        this.strainId = other.strainId == null ? null : other.strainId.copy();
        this.tekId = other.tekId == null ? null : other.tekId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ImageCriteria copy() {
        return new ImageCriteria(this);
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

    public LongFilter getObservationId() {
        return observationId;
    }

    public LongFilter observationId() {
        if (observationId == null) {
            observationId = new LongFilter();
        }
        return observationId;
    }

    public void setObservationId(LongFilter observationId) {
        this.observationId = observationId;
    }

    public LongFilter getStrainId() {
        return strainId;
    }

    public LongFilter strainId() {
        if (strainId == null) {
            strainId = new LongFilter();
        }
        return strainId;
    }

    public void setStrainId(LongFilter strainId) {
        this.strainId = strainId;
    }

    public LongFilter getTekId() {
        return tekId;
    }

    public LongFilter tekId() {
        if (tekId == null) {
            tekId = new LongFilter();
        }
        return tekId;
    }

    public void setTekId(LongFilter tekId) {
        this.tekId = tekId;
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
        final ImageCriteria that = (ImageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(observationId, that.observationId) &&
            Objects.equals(strainId, that.strainId) &&
            Objects.equals(tekId, that.tekId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, observationId, strainId, tekId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (observationId != null ? "observationId=" + observationId + ", " : "") +
            (strainId != null ? "strainId=" + strainId + ", " : "") +
            (tekId != null ? "tekId=" + tekId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
