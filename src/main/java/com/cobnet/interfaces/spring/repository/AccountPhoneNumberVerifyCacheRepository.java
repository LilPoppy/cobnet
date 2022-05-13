package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.AccountPhoneNumberVerifyCache;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountPhoneNumberVerifyCacheRepository extends RedisRepository<AccountPhoneNumberVerifyCache, String> {


}
