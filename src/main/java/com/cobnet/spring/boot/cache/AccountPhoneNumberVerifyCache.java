package com.cobnet.spring.boot.cache;

import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.spring.boot.service.PhoneNumberSmsVerifyService;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@RedisHash
@NoArgsConstructor
public class AccountPhoneNumberVerifyCache {

    @Id
    private String id;

    private int code;

    private Date creationTime;

    private PhoneNumberSmsType type;

    private int count;

    private boolean verified;

    public static final String PhoneNumberSmsVerifyServiceKey = PhoneNumberSmsVerifyService.class.getSimpleName();

    public AccountPhoneNumberVerifyCache(String id, int code, Date creationTime, PhoneNumberSmsType type, int count, boolean verified) {
        this.id = id;
        this.code = code;
        this.creationTime = creationTime;
        this.type = type;
        this.count = count;
        this.verified = verified;
    }

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public PhoneNumberSmsType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setType(PhoneNumberSmsType type) {
        this.type = type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
