package com.cobnet.spring.boot.controller;

import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.dto.AuthenticationResult;
import com.cobnet.spring.boot.dto.ConnectionToken;
import com.cobnet.spring.boot.dto.MappedPacket;
import com.cobnet.spring.boot.dto.MappedPacket;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserInfoController {


    @GetMapping("/connection")
    public Map<String, Object> authenticate(HttpServletRequest request) throws IOException {

        MappedPacket result = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.isAuthenticated()) {

            HttpSession session = request.getSession();

            if(session != null) {

                Object attribute = session.getAttribute(SecurityConfiguration.CONNECTION_TOKEN);
                System.out.println("@@@@a");
                result = attribute instanceof ConnectionToken ? (ConnectionToken)attribute : null;
                System.out.println("@@@@b");
                if(result == null) {
                    System.out.println("@@@@c");
                    //TODO: Load balancer then return a good ip
                    result = new ConnectionToken(new Date(System.currentTimeMillis()), UUID.randomUUID().toString(), "localhost", 8090);
                    System.out.println("@@@@d");
                    session.setAttribute(SecurityConfiguration.CONNECTION_TOKEN, result);
                    System.out.println("@@@@dd");
                }
            }
        } else {
            System.out.println("@@@@e");
            result = new AuthenticationResult(false);
        }
        System.out.println("@@@@f");
        return result.getRaw();
    }
}
