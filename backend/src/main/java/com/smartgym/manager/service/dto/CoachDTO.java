package com.smartgym.manager.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.smartgym.manager.domain.Coach} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CoachDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String specialty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoachDTO)) {
            return false;
        }

        CoachDTO coachDTO = (CoachDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, coachDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CoachDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", specialty='" + getSpecialty() + "'" +
            "}";
    }
}
