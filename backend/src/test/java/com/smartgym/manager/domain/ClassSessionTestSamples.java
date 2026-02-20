package com.smartgym.manager.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClassSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ClassSession getClassSessionSample1() {
        return new ClassSession().id(1L).title("title1").capacity(1);
    }

    public static ClassSession getClassSessionSample2() {
        return new ClassSession().id(2L).title("title2").capacity(2);
    }

    public static ClassSession getClassSessionRandomSampleGenerator() {
        return new ClassSession().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).capacity(intCount.incrementAndGet());
    }
}
