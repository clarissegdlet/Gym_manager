package com.smartgym.manager.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.smartgym.manager.domain.CheckIn} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckInDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant checkInTime;

    @NotNull
    private MemberDTO member;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Instant checkInTime) {
        this.checkInTime = checkInTime;
    }

    public MemberDTO getMember() {
        return member;
    }

    public void setMember(MemberDTO member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckInDTO)) {
            return false;
        }

        CheckInDTO checkInDTO = (CheckInDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, checkInDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CheckInDTO{" +
            "id=" + getId() +
            ", checkInTime='" + getCheckInTime() + "'" +
            ", member=" + getMember() +
            "}";
    }
}
