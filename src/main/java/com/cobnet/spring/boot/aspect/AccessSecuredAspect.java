package com.cobnet.spring.boot.aspect;

import com.cobnet.interfaces.connection.AuthenticatableChannel;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.security.Permissible;
import com.cobnet.interfaces.security.annotation.AccessSecured;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class AccessSecuredAspect {

    private static Logger LOG = LoggerFactory.getLogger(AccessSecuredAspect.class);

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Around("@annotation(com.cobnet.interfaces.security.annotation.AccessSecured)")
    public Object processMethodsAnnotatedWithAccessSecuredAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        AccessSecured annotation = method.getAnnotation(AccessSecured.class);

        String[] roles = annotation.roles();

        String[] permissions = annotation.permissions();

        Account account = Account.getCurrentAccount();

        if(joinPoint.getTarget() instanceof EventListener listener) {

            if(joinPoint.getArgs()[0] instanceof AuthenticatableChannel channel) {

                account = channel.getAccount();

            } else {

                throw new MethodArgumentTypeMismatchException(joinPoint.getArgs()[0], AuthenticatableChannel.class, "AuthenticatableChannel", new MethodParameter(method, 0), new AccessDeniedException("An exception occurred when getting access denied response."));
            }
        }

        if(account instanceof Permissible permissible) {

            if(roles.length > 0) {

                if (permissible instanceof User user && user.hasRole(roles)) {

                    return joinPoint.proceed();

                } else {

                    accessDenied();
                    return false;
                }
            }

            if(permissions.length > 0) {

                if (Arrays.stream(permissions).allMatch(permissible::isPermitted)) {

                    return joinPoint.proceed();

                } else {

                    accessDenied();
                    return false;
                }
            }

        } else if(roles.length > 0 || permissions.length > 0) {

            accessDenied();
            return false;
        }

        return joinPoint.proceed();
    }

    private void accessDenied() {

        HttpServletRequest request = ProjectBeanHolder.getCurrentHttpRequest();
        HttpServletResponse response =  ProjectBeanHolder.getCurrentHttpResponse();

        if(request != null && response != null) {

            try {

                accessDeniedHandler.handle(request, response, new AccessDeniedException("An exception occurred when getting access denied response."));

            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        } else {

            //TODO handle netty no permission here
        }
    }
}
