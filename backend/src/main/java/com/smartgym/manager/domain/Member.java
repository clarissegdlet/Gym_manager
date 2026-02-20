package com.smartgym.manager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Member.
 */
@Entity
@Table(name = "member")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "member", "classSession" }, allowSetters = true)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "member" }, allowSetters = true)
    private Set<CheckIn> checkIns = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "payments", "member" }, allowSetters = true)
    private Set<Membership> memberships = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Member id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Member name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public Member email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Member phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Member active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Booking> getBookings() {
        return this.bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        if (this.bookings != null) {
            this.bookings.forEach(i -> i.setMember(null));
        }
        if (bookings != null) {
            bookings.forEach(i -> i.setMember(this));
        }
        this.bookings = bookings;
    }

    public Member bookings(Set<Booking> bookings) {
        this.setBookings(bookings);
        return this;
    }

    public Member addBookings(Booking booking) {
        this.bookings.add(booking);
        booking.setMember(this);
        return this;
    }

    public Member removeBookings(Booking booking) {
        this.bookings.remove(booking);
        booking.setMember(null);
        return this;
    }

    public Set<CheckIn> getCheckIns() {
        return this.checkIns;
    }

    public void setCheckIns(Set<CheckIn> checkIns) {
        if (this.checkIns != null) {
            this.checkIns.forEach(i -> i.setMember(null));
        }
        if (checkIns != null) {
            checkIns.forEach(i -> i.setMember(this));
        }
        this.checkIns = checkIns;
    }

    public Member checkIns(Set<CheckIn> checkIns) {
        this.setCheckIns(checkIns);
        return this;
    }

    public Member addCheckIns(CheckIn checkIn) {
        this.checkIns.add(checkIn);
        checkIn.setMember(this);
        return this;
    }

    public Member removeCheckIns(CheckIn checkIn) {
        this.checkIns.remove(checkIn);
        checkIn.setMember(null);
        return this;
    }

    public Set<Membership> getMemberships() {
        return this.memberships;
    }

    public void setMemberships(Set<Membership> memberships) {
        if (this.memberships != null) {
            this.memberships.forEach(i -> i.setMember(null));
        }
        if (memberships != null) {
            memberships.forEach(i -> i.setMember(this));
        }
        this.memberships = memberships;
    }

    public Member memberships(Set<Membership> memberships) {
        this.setMemberships(memberships);
        return this;
    }

    public Member addMemberships(Membership membership) {
        this.memberships.add(membership);
        membership.setMember(this);
        return this;
    }

    public Member removeMemberships(Membership membership) {
        this.memberships.remove(membership);
        membership.setMember(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        return getId() != null && getId().equals(((Member) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Member{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
