package com.smartgym.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Coach.
 */
@Entity
@Table(name = "coach")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Coach implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "specialty")
    private String specialty;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Coach id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Coach name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return this.specialty;
    }

    public Coach specialty(String specialty) {
        this.setSpecialty(specialty);
        return this;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coach)) {
            return false;
        }
        return getId() != null && getId().equals(((Coach) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Coach{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", specialty='" + getSpecialty() + "'" +
            "}";
    }
}
