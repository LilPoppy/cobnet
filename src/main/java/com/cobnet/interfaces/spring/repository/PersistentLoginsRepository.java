package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.PersistentLogins;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PersistentLoginsRepository extends JPABaseRepository<PersistentLogins, String>, PersistentTokenRepository {

    default void createNewToken(PersistentRememberMeToken token) {

        this.save(new PersistentLogins(token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate()));
    }

    default void updateToken(String series, String tokenValue, Date lastUsed) {
        PersistentLogins logins = this.findBySeries(series);
        logins.setToken(tokenValue);
        logins.setLastUsed(lastUsed);
        this.save(logins);
    }

    default PersistentRememberMeToken getTokenForSeries(String seriesId) {

        PersistentLogins logins = this.findBySeries(seriesId);

        if(logins != null) {

            return new PersistentRememberMeToken(logins.getUsername(), logins.getSeries(), logins.getToken(), logins.getLastUsed());
        }
        return null;
    }

    @Cacheable(value = "PersistentLogins")
    public PersistentLogins findBySeries(String series);

    @Cacheable(value = "PersistentLogins")
    public PersistentLogins findByUsernameEqualsIgnoreCase(String username);

    @CacheEvict(cacheNames = "PersistentLogins", key = "#username")
    @Override
    default void removeUserTokens(String username) {

        RedisCacheManager manager = ProjectBeanHolder.getRedisCacheManager();

        if(manager != null) {

            Cache cache = manager.getCache("PersistentLogins");

            if(cache != null) {

                cache.evict(username);
            }
        }

        this.deleteById(username);
    }
}
