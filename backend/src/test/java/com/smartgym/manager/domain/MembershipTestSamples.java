package com.smartgym.manager.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MembershipTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Membership getMembershipSample1() {
        return new Membership().id(1L).type("type1");
    }

    public static Membership getMembershipSample2() {
        return new Membership().id(2L).type("type2");
    }

    public static Membership getMembershipRandomSampleGenerator() {
        return new Membership().id(longCount.incrementAndGet()).type(UUID.randomUUID().toString());
    }
}
