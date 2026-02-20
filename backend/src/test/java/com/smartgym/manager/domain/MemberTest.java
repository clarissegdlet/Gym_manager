package com.smartgym.manager.domain;

import static com.smartgym.manager.domain.BookingTestSamples.*;
import static com.smartgym.manager.domain.CheckInTestSamples.*;
import static com.smartgym.manager.domain.MemberTestSamples.*;
import static com.smartgym.manager.domain.MembershipTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.smartgym.manager.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Member.class);
        Member member1 = getMemberSample1();
        Member member2 = new Member();
        assertThat(member1).isNotEqualTo(member2);

        member2.setId(member1.getId());
        assertThat(member1).isEqualTo(member2);

        member2 = getMemberSample2();
        assertThat(member1).isNotEqualTo(member2);
    }

    @Test
    void bookingsTest() {
        Member member = getMemberRandomSampleGenerator();
        Booking bookingBack = getBookingRandomSampleGenerator();

        member.addBookings(bookingBack);
        assertThat(member.getBookings()).containsOnly(bookingBack);
        assertThat(bookingBack.getMember()).isEqualTo(member);

        member.removeBookings(bookingBack);
        assertThat(member.getBookings()).doesNotContain(bookingBack);
        assertThat(bookingBack.getMember()).isNull();

        member.bookings(new HashSet<>(Set.of(bookingBack)));
        assertThat(member.getBookings()).containsOnly(bookingBack);
        assertThat(bookingBack.getMember()).isEqualTo(member);

        member.setBookings(new HashSet<>());
        assertThat(member.getBookings()).doesNotContain(bookingBack);
        assertThat(bookingBack.getMember()).isNull();
    }

    @Test
    void checkInsTest() {
        Member member = getMemberRandomSampleGenerator();
        CheckIn checkInBack = getCheckInRandomSampleGenerator();

        member.addCheckIns(checkInBack);
        assertThat(member.getCheckIns()).containsOnly(checkInBack);
        assertThat(checkInBack.getMember()).isEqualTo(member);

        member.removeCheckIns(checkInBack);
        assertThat(member.getCheckIns()).doesNotContain(checkInBack);
        assertThat(checkInBack.getMember()).isNull();

        member.checkIns(new HashSet<>(Set.of(checkInBack)));
        assertThat(member.getCheckIns()).containsOnly(checkInBack);
        assertThat(checkInBack.getMember()).isEqualTo(member);

        member.setCheckIns(new HashSet<>());
        assertThat(member.getCheckIns()).doesNotContain(checkInBack);
        assertThat(checkInBack.getMember()).isNull();
    }

    @Test
    void membershipsTest() {
        Member member = getMemberRandomSampleGenerator();
        Membership membershipBack = getMembershipRandomSampleGenerator();

        member.addMemberships(membershipBack);
        assertThat(member.getMemberships()).containsOnly(membershipBack);
        assertThat(membershipBack.getMember()).isEqualTo(member);

        member.removeMemberships(membershipBack);
        assertThat(member.getMemberships()).doesNotContain(membershipBack);
        assertThat(membershipBack.getMember()).isNull();

        member.memberships(new HashSet<>(Set.of(membershipBack)));
        assertThat(member.getMemberships()).containsOnly(membershipBack);
        assertThat(membershipBack.getMember()).isEqualTo(member);

        member.setMemberships(new HashSet<>());
        assertThat(member.getMemberships()).doesNotContain(membershipBack);
        assertThat(membershipBack.getMember()).isNull();
    }
}
