package com.storechain.spring.boot.controller;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TestController {

	@ResponseBody
	@CrossOrigin
    @GetMapping("test")
    public String test(HttpServletRequest request) {

        return "success";
    }
	
	@GetMapping("/index")
	public ModelAndView index(){
		ModelAndView mav= new ModelAndView("socket");
		mav.addObject("uid", new Random());
		return mav;
	}

}
