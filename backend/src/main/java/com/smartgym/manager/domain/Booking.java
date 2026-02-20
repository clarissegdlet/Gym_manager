package com.smartgym.manager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartgym.manager.domain.enumeration.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Booking.
 */
@Entity
@Table(name = "booking")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;

    @NotNull
    @Column(name = "booking_time", nullable = false)
    private Instant bookingTime;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bookings", "checkIns", "memberships" }, allowSetters = true)
    private Member member;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bookings" }, allowSetters = true)
    private ClassSession classSession;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Booking id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookingStatus getBookingStatus() {
        return this.bookingStatus;
    }

    public Booking bookingStatus(BookingStatus bookingStatus) {
        this.setBookingStatus(bookingStatus);
        return this;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Instant getBookingTime() {
        return this.bookingTime;
    }

    public Booking bookingTime(Instant bookingTime) {
        this.setBookingTime(bookingTime);
        return this;
    }

    public void setBookingTime(Instant bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Member getMember() {
        return this.member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Booking member(Member member) {
        this.setMember(member);
        return this;
    }

    public ClassSession getClassSession() {
        return this.classSession;
    }

    public void setClassSession(ClassSession classSession) {
        this.classSession = classSession;
    }

    public Booking classSession(ClassSession classSession) {
        this.setClassSession(classSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return getId() != null && getId().equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Booking{" +
            "id=" + getId() +
            ", bookingStatus='" + getBookingStatus() + "'" +
            ", bookingTime='" + getBookingTime() + "'" +
            "}";
    }
}
