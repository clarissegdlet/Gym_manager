package com.smartgym.manager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartgym.manager.domain.enumeration.ClassStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A ClassSession.
 */
@Entity
@Table(name = "class_session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "date_time", nullable = false)
    private Instant dateTime;

    @NotNull
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClassStatus status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "classSession")
    @JsonIgnoreProperties(value = { "member", "classSession" }, allowSetters = true)
    private Set<Booking> bookings = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Coach coach;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClassSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ClassSession title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getDateTime() {
        return this.dateTime;
    }

    public ClassSession dateTime(Instant dateTime) {
        this.setDateTime(dateTime);
        return this;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public ClassSession capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public ClassStatus getStatus() {
        return this.status;
    }

    public ClassSession status(ClassStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public Set<Booking> getBookings() {
        return this.bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        if (this.bookings != null) {
            this.bookings.forEach(i -> i.setClassSession(null));
        }
        if (bookings != null) {
            bookings.forEach(i -> i.setClassSession(this));
        }
        this.bookings = bookings;
    }

    public ClassSession bookings(Set<Booking> bookings) {
        this.setBookings(bookings);
        return this;
    }

    public ClassSession addBookings(Booking booking) {
        this.bookings.add(booking);
        booking.setClassSession(this);
        return this;
    }

    public ClassSession removeBookings(Booking booking) {
        this.bookings.remove(booking);
        booking.setClassSession(null);
        return this;
    }

    public Coach getCoach() {
        return this.coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public ClassSession coach(Coach coach) {
        this.setCoach(coach);
        return this;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ClassSession room(Room room) {
        this.setRoom(room);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassSession)) {
            return false;
        }
        return getId() != null && getId().equals(((ClassSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassSession{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", dateTime='" + getDateTime() + "'" +
            ", capacity=" + getCapacity() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
