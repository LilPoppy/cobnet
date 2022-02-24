package com.storechain.spring.boot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.storechain.utils.SessionManager;

@Controller
public class TestController {

	@ResponseBody
	@CrossOrigin
    @RequestMapping(value = "login")
    public String login (HttpServletRequest request, String userName, String password){
        String msg="logon failure!";
        System.out.println("asdasd");
        if (userName!=null && "admin".equals(userName) && "123".equals(password)){
            request.getSession().setAttribute("user", userName);
            msg = "login successful!";
            System.out.println(request.getSession().getId());
        }

        System.out.println(SessionManager.getRepository().findByPrincipalName(userName));
        
        return msg;
    }
	
    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "loginPage";
    }

    @GetMapping("/register")
    public String register() {
        return "registerPage";
    }
}
