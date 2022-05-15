package com.cobnet.spring.boot.entity.support;

import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.spring.repository.JPABaseRepository;
import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class JpaExtensionRepository <T, ID extends Serializable>  extends SimpleJpaRepository<T,ID> implements JPABaseRepository<T,ID>  {

    private final EntityManager manager;
    private final JpaEntityInformation<T, ?> information;


    public JpaExtensionRepository(Class<T> domainClass, EntityManager manager) {

        super(domainClass, manager);
        this.manager = manager;
        this.information = JpaEntityInformationSupport.getEntityInformation(domainClass, manager);
    }

    public JpaExtensionRepository(JpaEntityInformation<T, ?> information, EntityManager manager) {

        super(information, manager);
        this.information = information;
        this.manager = manager;
    }

    @Transactional
    @Override
    public <S extends T> S save(S entity) {

        if(entity instanceof Account account) {

            account = (Account) super.save(entity);

            super.flush();

            this.manager.refresh(account);

            for(String key : ProjectBeanHolder.getRedisIndexedSessionRepository().findByPrincipalName(account.getUsername()).keySet()) {

                Session session = ProjectBeanHolder.getRedisIndexedSessionRepository().findById(key);

                SecurityContext context = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

                if(context.getAuthentication() instanceof AccountAuthenticationToken token) {

                    context.setAuthentication(token.updatePrincipal(account));
                }

                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
            }

            SecurityContext context = SecurityContextHolder.getContext();

            if(context != null && context.getAuthentication() instanceof AccountAuthenticationToken token) {

                if(token.getAccount() != null && token.getAccount().getUsername() != null && token.getAccount().getUsername().equalsIgnoreCase(account.getUsername())) {

                    context.setAuthentication(token.updatePrincipal(account));
                }
            }

            return (S) account;

        } else {

            return super.save(entity);
        }
    }


}
