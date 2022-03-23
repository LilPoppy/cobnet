package com.cobnet.spring.boot.controller;

import com.cobnet.interfaces.connection.annotation.EventHandler;
import com.cobnet.interfaces.security.annotation.AccessSecured;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AuthenticationResult;
import com.cobnet.spring.boot.dto.ConnectionToken;
import com.cobnet.spring.boot.dto.support.HttpMapTransmission;
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

    @EventHandler
    public void test() {

    }

    @GetMapping("/connectionToken")
    public Map<String, Object> connectionToken(HttpServletRequest request) throws IOException {

        HttpMapTransmission result = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.isAuthenticated()) {

            HttpSession session = request.getSession();

            if(session != null) {

                Object attribute = session.getAttribute(SecurityConfiguration.CONNECTION_TOKEN);

                result = attribute instanceof ConnectionToken ? (ConnectionToken)attribute : null;

                if(result == null) {

                    //TODO: Load balancer then return a good ip
                    result = new ConnectionToken(new Date(System.currentTimeMillis()), UUID.randomUUID().toString(), "localhost", 8090);

                    session.setAttribute(SecurityConfiguration.CONNECTION_TOKEN, result);
                }
            }
        } else {

            result = new AuthenticationResult(false);
        }

        assert result != null;

        return result.getData();
    }
}
