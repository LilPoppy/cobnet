package com.cobnet.spring.boot.aspect;

import com.cobnet.interfaces.spring.dto.annotation.ServiceOptionDefined;
import com.cobnet.interfaces.spring.dto.ServiceOptionGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class ServiceOptionDefinedAspect {

    @Around("@annotation(com.cobnet.interfaces.spring.dto.annotation.ServiceOptionDefined)")
    public Object processMethodsAnnotatedWithEventHandlerAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        if(joinPoint.getTarget() instanceof ServiceOptionGenerator generator) {

            ServiceOptionDefined annotation = method.getAnnotation(ServiceOptionDefined.class);

            if(joinPoint.getArgs()[0] instanceof String name) {

                if(Arrays.stream(annotation.names()).anyMatch(x -> x.equalsIgnoreCase(name))) {

                    return joinPoint.proceed(joinPoint.getArgs());
                }
            }
        }

        return null;
    }
}
