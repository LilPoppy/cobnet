package com.cobnet.spring.boot.aspect;

import com.cobnet.connection.InboundPacket;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import io.netty.channel.Channel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@EnableAspectJAutoProxy
@Aspect
@Component
public class EventHandlerAspect {


    @Around("@annotation(com.cobnet.interfaces.connection.annotation.EventHandler)")
    public Object processMethodsAnnotatedWithEventHandlerAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        if(joinPoint.getTarget() instanceof EventListener<?> listener) {

            EventHandler annotation = method.getAnnotation(EventHandler.class);

            if(joinPoint.getArgs()[0] instanceof Channel && joinPoint.getArgs()[1] instanceof InboundPacket packet) {

                if(Arrays.stream(annotation.value()).anyMatch(opertaion -> opertaion == packet.getOperation())) {

                    return joinPoint.proceed(joinPoint.getArgs());
                }
            }
        }

        return false;
    }
}
