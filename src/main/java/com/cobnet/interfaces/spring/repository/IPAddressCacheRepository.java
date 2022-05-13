package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.IPAddressCache;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPAddressCacheRepository extends IndexedRedisRepository<IPAddressCache, String> {

    List<IPAddressCache> findByIpAddressEquals(String address);
}
