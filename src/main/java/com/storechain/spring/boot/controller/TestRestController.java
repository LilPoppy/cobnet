package com.storechain.spring.boot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {
	
	@PostMapping("/doLogin")
	public String login(String username, String password) {
		System.out.println("@asdasd@");
		return username.length() > 0 && password.length() > 0 ? "登陆成功" : "登陆失败";
	}
	
    @PostMapping("/doRegister")
    public String register(String username, String password) {
    	System.out.println("@asdasdE@");
        return username.length() > 0 && password.length() > 0 ? "注册成功" : "注册失败";
    }
}
