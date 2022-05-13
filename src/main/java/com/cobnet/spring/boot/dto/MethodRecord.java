package com.cobnet.spring.boot.dto;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
public class MethodRecord implements Serializable {

    private int count;

    private Date latest;

    public MethodRecord(int count, Date latest) {
        this.count = count;
        this.latest = latest;
    }

    public int getCount() {
        return count;
    }

    public Date getLatest() {
        return latest;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setLatest(Date latest) {
        this.latest = latest;
    }

    public static MethodRecord of(int count, Date latest) {

        return new MethodRecord(count, latest);
    }

    @Override
    public String toString() {
        return "MethodRecord{" +
                "count=" + count +
                ", latest=" + latest +
                '}';
    }
}
