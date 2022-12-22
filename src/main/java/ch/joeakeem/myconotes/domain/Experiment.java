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
 * A Experiment.
 */
@Entity
@Table(name = "experiment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "experiment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Experiment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "notes")
    private String notes;

    @NotNull
    @Column(name = "conducted_at", nullable = false)
    private LocalDate conductedAt;

    @OneToMany(mappedBy = "experiment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "images", "experiment" }, allowSetters = true)
    private Set<Observation> observations = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "instructions" }, allowSetters = true)
    private Tek instructions;

    @ManyToOne(optional = false)
    @NotNull
    private User conductedBy;

    @ManyToMany
    @JoinTable(
        name = "rel_experiment__involved_strains",
        joinColumns = @JoinColumn(name = "experiment_id"),
        inverseJoinColumns = @JoinColumn(name = "involved_strains_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "images", "species", "origin", "experiments" }, allowSetters = true)
    private Set<Strain> involvedStrains = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_experiment__preceding_experiments",
        joinColumns = @JoinColumn(name = "experiment_id"),
        inverseJoinColumns = @JoinColumn(name = "preceding_experiments_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "observations", "instructions", "conductedBy", "involvedStrains", "precedingExperiments", "followupExperiments" },
        allowSetters = true
    )
    private Set<Experiment> precedingExperiments = new HashSet<>();

    @ManyToMany(mappedBy = "precedingExperiments")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = { "observations", "instructions", "conductedBy", "involvedStrains", "precedingExperiments", "followupExperiments" },
        allowSetters = true
    )
    private Set<Experiment> followupExperiments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Experiment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Experiment title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return this.notes;
    }

    public Experiment notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getConductedAt() {
        return this.conductedAt;
    }

    public Experiment conductedAt(LocalDate conductedAt) {
        this.setConductedAt(conductedAt);
        return this;
    }

    public void setConductedAt(LocalDate conductedAt) {
        this.conductedAt = conductedAt;
    }

    public Set<Observation> getObservations() {
        return this.observations;
    }

    public void setObservations(Set<Observation> observations) {
        if (this.observations != null) {
            this.observations.forEach(i -> i.setExperiment(null));
        }
        if (observations != null) {
            observations.forEach(i -> i.setExperiment(this));
        }
        this.observations = observations;
    }

    public Experiment observations(Set<Observation> observations) {
        this.setObservations(observations);
        return this;
    }

    public Experiment addObservations(Observation observation) {
        this.observations.add(observation);
        observation.setExperiment(this);
        return this;
    }

    public Experiment removeObservations(Observation observation) {
        this.observations.remove(observation);
        observation.setExperiment(null);
        return this;
    }

    public Tek getInstructions() {
        return this.instructions;
    }

    public void setInstructions(Tek tek) {
        this.instructions = tek;
    }

    public Experiment instructions(Tek tek) {
        this.setInstructions(tek);
        return this;
    }

    public User getConductedBy() {
        return this.conductedBy;
    }

    public void setConductedBy(User user) {
        this.conductedBy = user;
    }

    public Experiment conductedBy(User user) {
        this.setConductedBy(user);
        return this;
    }

    public Set<Strain> getInvolvedStrains() {
        return this.involvedStrains;
    }

    public void setInvolvedStrains(Set<Strain> strains) {
        this.involvedStrains = strains;
    }

    public Experiment involvedStrains(Set<Strain> strains) {
        this.setInvolvedStrains(strains);
        return this;
    }

    public Experiment addInvolvedStrains(Strain strain) {
        this.involvedStrains.add(strain);
        strain.getExperiments().add(this);
        return this;
    }

    public Experiment removeInvolvedStrains(Strain strain) {
        this.involvedStrains.remove(strain);
        strain.getExperiments().remove(this);
        return this;
    }

    public Set<Experiment> getPrecedingExperiments() {
        return this.precedingExperiments;
    }

    public void setPrecedingExperiments(Set<Experiment> experiments) {
        this.precedingExperiments = experiments;
    }

    public Experiment precedingExperiments(Set<Experiment> experiments) {
        this.setPrecedingExperiments(experiments);
        return this;
    }

    public Experiment addPrecedingExperiments(Experiment experiment) {
        this.precedingExperiments.add(experiment);
        experiment.getFollowupExperiments().add(this);
        return this;
    }

    public Experiment removePrecedingExperiments(Experiment experiment) {
        this.precedingExperiments.remove(experiment);
        experiment.getFollowupExperiments().remove(this);
        return this;
    }

    public Set<Experiment> getFollowupExperiments() {
        return this.followupExperiments;
    }

    public void setFollowupExperiments(Set<Experiment> experiments) {
        if (this.followupExperiments != null) {
            this.followupExperiments.forEach(i -> i.removePrecedingExperiments(this));
        }
        if (experiments != null) {
            experiments.forEach(i -> i.addPrecedingExperiments(this));
        }
        this.followupExperiments = experiments;
    }

    public Experiment followupExperiments(Set<Experiment> experiments) {
        this.setFollowupExperiments(experiments);
        return this;
    }

    public Experiment addFollowupExperiments(Experiment experiment) {
        this.followupExperiments.add(experiment);
        experiment.getPrecedingExperiments().add(this);
        return this;
    }

    public Experiment removeFollowupExperiments(Experiment experiment) {
        this.followupExperiments.remove(experiment);
        experiment.getPrecedingExperiments().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Experiment)) {
            return false;
        }
        return id != null && id.equals(((Experiment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Experiment{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", notes='" + getNotes() + "'" +
            ", conductedAt='" + getConductedAt() + "'" +
            "}";
    }
}
