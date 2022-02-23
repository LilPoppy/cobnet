package com.storechain.spring.boot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.storechain.spring.boot.service.SessionRepositoryService;
import com.storechain.utils.SessionManager;

@Controller
public class TestController {

	@ResponseBody
	@CrossOrigin
    @GetMapping("test")
    public String test(HttpServletRequest request) {

        return "success";
    }
	
	@ResponseBody
	@CrossOrigin
    @RequestMapping(value = "login")
    public String login (HttpServletRequest request, String userName, String password){
        String msg="logon failure!";
        System.out.println("asdasd");
        if (userName!=null && "admin".equals(userName) && "123".equals(password)){
            request.getSession().setAttribute("admin", userName);
            msg = "login successful!";
            System.out.println(request.getSession().getId());
        }

        System.out.println(SessionManager.getRepository().findByPrincipalName(userName));
        
        return msg;
    }
}
