package com.smartgym.manager.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CoachTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Coach getCoachSample1() {
        return new Coach().id(1L).name("name1").specialty("specialty1");
    }

    public static Coach getCoachSample2() {
        return new Coach().id(2L).name("name2").specialty("specialty2");
    }

    public static Coach getCoachRandomSampleGenerator() {
        return new Coach().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).specialty(UUID.randomUUID().toString());
    }
}
