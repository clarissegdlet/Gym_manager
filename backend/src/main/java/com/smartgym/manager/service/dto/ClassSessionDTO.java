package com.smartgym.manager.service.dto;

import com.smartgym.manager.domain.enumeration.ClassStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.smartgym.manager.domain.ClassSession} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassSessionDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Instant dateTime;

    @NotNull
    private Integer capacity;

    @NotNull
    private ClassStatus status;

    private CoachDTO coach;

    private RoomDTO room;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public CoachDTO getCoach() {
        return coach;
    }

    public void setCoach(CoachDTO coach) {
        this.coach = coach;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassSessionDTO)) {
            return false;
        }

        ClassSessionDTO classSessionDTO = (ClassSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, classSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassSessionDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", dateTime='" + getDateTime() + "'" +
            ", capacity=" + getCapacity() +
            ", status='" + getStatus() + "'" +
            ", coach=" + getCoach() +
            ", room=" + getRoom() +
            "}";
    }
}
