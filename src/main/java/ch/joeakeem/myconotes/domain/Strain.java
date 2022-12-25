package ch.joeakeem.myconotes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A Strain.
 */
@Entity
@Table(name = "strain")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "strain")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Strain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "isolated_at", nullable = false)
    private LocalDate isolatedAt;

    @Column(name = "fruiting")
    private Boolean fruiting;

    @OneToMany(mappedBy = "strain")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "observation", "strain", "tek" }, allowSetters = true)
    private Set<Image> images = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "genus" }, allowSetters = true)
    private Species species;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "observations", "tek", "conductedBy", "involvedStrains", "precedingExperiments", "followupExperiments" },
        allowSetters = true
    )
    private Experiment origin;

    @ManyToMany(mappedBy = "involvedStrains")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = { "observations", "tek", "conductedBy", "involvedStrains", "precedingExperiments", "followupExperiments" },
        allowSetters = true
    )
    private Set<Experiment> experiments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Strain id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Strain name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Strain description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getIsolatedAt() {
        return this.isolatedAt;
    }

    public Strain isolatedAt(LocalDate isolatedAt) {
        this.setIsolatedAt(isolatedAt);
        return this;
    }

    public void setIsolatedAt(LocalDate isolatedAt) {
        this.isolatedAt = isolatedAt;
    }

    public Boolean getFruiting() {
        return this.fruiting;
    }

    public Strain fruiting(Boolean fruiting) {
        this.setFruiting(fruiting);
        return this;
    }

    public void setFruiting(Boolean fruiting) {
        this.fruiting = fruiting;
    }

    public Set<Image> getImages() {
        return this.images;
    }

    public void setImages(Set<Image> images) {
        if (this.images != null) {
            this.images.forEach(i -> i.setStrain(null));
        }
        if (images != null) {
            images.forEach(i -> i.setStrain(this));
        }
        this.images = images;
    }

    public Strain images(Set<Image> images) {
        this.setImages(images);
        return this;
    }

    public Strain addImages(Image image) {
        this.images.add(image);
        image.setStrain(this);
        return this;
    }

    public Strain removeImages(Image image) {
        this.images.remove(image);
        image.setStrain(null);
        return this;
    }

    public Species getSpecies() {
        return this.species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public Strain species(Species species) {
        this.setSpecies(species);
        return this;
    }

    public Experiment getOrigin() {
        return this.origin;
    }

    public void setOrigin(Experiment experiment) {
        this.origin = experiment;
    }

    public Strain origin(Experiment experiment) {
        this.setOrigin(experiment);
        return this;
    }

    public Set<Experiment> getExperiments() {
        return this.experiments;
    }

    public void setExperiments(Set<Experiment> experiments) {
        if (this.experiments != null) {
            this.experiments.forEach(i -> i.removeInvolvedStrains(this));
        }
        if (experiments != null) {
            experiments.forEach(i -> i.addInvolvedStrains(this));
        }
        this.experiments = experiments;
    }

    public Strain experiments(Set<Experiment> experiments) {
        this.setExperiments(experiments);
        return this;
    }

    public Strain addExperiments(Experiment experiment) {
        this.experiments.add(experiment);
        experiment.getInvolvedStrains().add(this);
        return this;
    }

    public Strain removeExperiments(Experiment experiment) {
        this.experiments.remove(experiment);
        experiment.getInvolvedStrains().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Strain)) {
            return false;
        }
        return id != null && id.equals(((Strain) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Strain{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", isolatedAt='" + getIsolatedAt() + "'" +
            ", fruiting='" + getFruiting() + "'" +
            "}";
    }
}
