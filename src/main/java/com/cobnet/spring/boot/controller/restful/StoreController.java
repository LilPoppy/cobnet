package com.cobnet.spring.boot.controller.restful;

import com.cobnet.exception.ServiceDownException;
import com.cobnet.interfaces.security.annotation.AccessSecured;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.StoreCheckInPageDetailResultStatus;
import com.cobnet.spring.boot.dto.support.StoreCheckInResultStatus;
import com.cobnet.spring.boot.dto.support.StoreRegisterResultStatus;
import com.google.maps.errors.ApiException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Tag(name = "Store")
@RestController
public class StoreController {

    @AccessSecured(roles = "USER")
    @PostMapping("/store/create")
    public ResponseResult<StoreRegisterResultStatus> create(HttpServletResponse response, StoreRegisterForm store) throws IOException, InterruptedException, ApiException {

        ResponseResult<StoreRegisterResultStatus> result = ProjectBeanHolder.getStoreService().register(store);

        response.setStatus(result.status().getCode());

        return result;
    }

    @PostMapping("/store/{storeId}/check-in")
    public ResponseResult<StoreCheckInResultStatus> checkIn(@PathVariable String storeId, CustomerInfoForm info, List<WorkInfoForm> services) {

        return new ResponseResult(StoreCheckInResultStatus.SUCCESS);
    }

    @GetMapping("/store/{storeId}/check-in-page-details")
    public ResponseResult<StoreCheckInPageDetailResultStatus> checkInPageDetail(HttpServletResponse response, @PathVariable String storeId, String country, String language) throws IOException, ServiceDownException {

        ResponseResult<StoreCheckInPageDetailResultStatus> result = ProjectBeanHolder.getStoreService().getStoreCheckInPageDetail(storeId, new Locale(country, language));

        response.setStatus(result.status().getCode());

        return result;
    }


}
