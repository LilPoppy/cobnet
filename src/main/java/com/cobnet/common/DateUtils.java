package com.cobnet.common;

import java.time.Duration;
import java.util.Date;

public class DateUtils {

    public static Date addDuration(Date target, Duration duration) {

        return Date.from(target.toInstant().plusMillis(duration.toMillis()));
    }

    public static Duration getInterval(Date from, Date to) {

        return Duration.ofMillis(Math.abs(to.getTime() - from.getTime()));
    }

    public static Date now() {

        return new Date(System.currentTimeMillis());
    }
}
