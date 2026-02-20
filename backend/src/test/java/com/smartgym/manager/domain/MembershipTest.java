package com.smartgym.manager.domain;

import static com.smartgym.manager.domain.MemberTestSamples.*;
import static com.smartgym.manager.domain.MembershipTestSamples.*;
import static com.smartgym.manager.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.smartgym.manager.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MembershipTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Membership.class);
        Membership membership1 = getMembershipSample1();
        Membership membership2 = new Membership();
        assertThat(membership1).isNotEqualTo(membership2);

        membership2.setId(membership1.getId());
        assertThat(membership1).isEqualTo(membership2);

        membership2 = getMembershipSample2();
        assertThat(membership1).isNotEqualTo(membership2);
    }

    @Test
    void paymentsTest() {
        Membership membership = getMembershipRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        membership.addPayments(paymentBack);
        assertThat(membership.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getMembership()).isEqualTo(membership);

        membership.removePayments(paymentBack);
        assertThat(membership.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getMembership()).isNull();

        membership.payments(new HashSet<>(Set.of(paymentBack)));
        assertThat(membership.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getMembership()).isEqualTo(membership);

        membership.setPayments(new HashSet<>());
        assertThat(membership.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getMembership()).isNull();
    }

    @Test
    void memberTest() {
        Membership membership = getMembershipRandomSampleGenerator();
        Member memberBack = getMemberRandomSampleGenerator();

        membership.setMember(memberBack);
        assertThat(membership.getMember()).isEqualTo(memberBack);

        membership.member(null);
        assertThat(membership.getMember()).isNull();
    }
}
