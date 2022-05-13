package com.cobnet.spring.boot.cache;

import lombok.NoArgsConstructor;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpSession;


public class SessionIndexedCache extends AbstractIndexedCache<String> {

    protected SessionIndexedCache() {}

    protected SessionIndexedCache(String s) {
        super(s);
    }

    @Override
    public String resolve(Object object) {

        if(object instanceof HttpSession session) {

            return session.getId();
        }

        if(object instanceof WebAuthenticationDetails details) {

            return details.getSessionId();
        }

        if(object instanceof String id) {

            return id;
        }

        return null;
    }
}
