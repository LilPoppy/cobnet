package com.cobnet.spring.boot.controller.restful;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.StoreRegisterForm;
import com.cobnet.spring.boot.dto.StoreRegisterResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class StoreController {

    public StoreRegisterResult createStore(HttpServletResponse response, StoreRegisterForm form) {

        StoreRegisterResult result = ProjectBeanHolder.getStoreService().register(form);

        response.setStatus(result.status().getCode());

        return result;
    }
}