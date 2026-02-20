package com.smartgym.manager.domain;

import static com.smartgym.manager.domain.BookingTestSamples.*;
import static com.smartgym.manager.domain.ClassSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.smartgym.manager.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClassSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassSession.class);
        ClassSession classSession1 = getClassSessionSample1();
        ClassSession classSession2 = new ClassSession();
        assertThat(classSession1).isNotEqualTo(classSession2);

        classSession2.setId(classSession1.getId());
        assertThat(classSession1).isEqualTo(classSession2);

        classSession2 = getClassSessionSample2();
        assertThat(classSession1).isNotEqualTo(classSession2);
    }

    @Test
    void bookingsTest() {
        ClassSession classSession = getClassSessionRandomSampleGenerator();
        Booking bookingBack = getBookingRandomSampleGenerator();

        classSession.addBookings(bookingBack);
        assertThat(classSession.getBookings()).containsOnly(bookingBack);
        assertThat(bookingBack.getClassSession()).isEqualTo(classSession);

        classSession.removeBookings(bookingBack);
        assertThat(classSession.getBookings()).doesNotContain(bookingBack);
        assertThat(bookingBack.getClassSession()).isNull();

        classSession.bookings(new HashSet<>(Set.of(bookingBack)));
        assertThat(classSession.getBookings()).containsOnly(bookingBack);
        assertThat(bookingBack.getClassSession()).isEqualTo(classSession);

        classSession.setBookings(new HashSet<>());
        assertThat(classSession.getBookings()).doesNotContain(bookingBack);
        assertThat(bookingBack.getClassSession()).isNull();
    }
}
