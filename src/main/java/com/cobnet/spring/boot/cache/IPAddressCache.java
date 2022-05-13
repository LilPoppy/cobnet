package com.cobnet.spring.boot.cache;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash
@NoArgsConstructor
public class IPAddressCache extends SessionIndexedCache{

    @Indexed
    private String ipAddress;

    public IPAddressCache(String id, String ipAddress) {
        super(id);
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public IPAddressCache setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    @Override
    public String toString() {
        return "IPAddressCache{ " +
                "sessionId='" + this.getId() + "', " +
                "ipAddress='" + ipAddress + '\'' +
                "}";
    }
}
