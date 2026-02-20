package com.smartgym.manager.domain;

import static com.smartgym.manager.domain.MembershipTestSamples.*;
import static com.smartgym.manager.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.smartgym.manager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void membershipTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        Membership membershipBack = getMembershipRandomSampleGenerator();

        payment.setMembership(membershipBack);
        assertThat(payment.getMembership()).isEqualTo(membershipBack);

        payment.membership(null);
        assertThat(payment.getMembership()).isNull();
    }
}
