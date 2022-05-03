package com.cobnet.spring.boot.controller.restful;

import com.cobnet.exception.ServiceDownException;
import com.cobnet.interfaces.security.annotation.AccessSecured;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.GoogleApiRequestResultStatus;
import com.cobnet.spring.boot.dto.support.StoreCheckInPageDetailResultStatus;
import com.cobnet.spring.boot.dto.support.StoreCheckInResultStatus;
import com.cobnet.spring.boot.dto.support.StoreRegisterResultStatus;
import com.google.maps.errors.ApiException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Tag(name = "Store")
@RestController
public class StoreController {

    @AccessSecured(roles = "USER")
    @PostMapping("/store/create")
    public ResponseResult<StoreRegisterResultStatus> create(HttpServletResponse response, @RequestParam("store-id") String storeId) throws IOException, InterruptedException, ApiException {

        ResponseResult<StoreRegisterResultStatus> result = ProjectBeanHolder.getStoreService().register(storeId);

        response.setStatus(result.status().getCode());

        return result;
    }

    @AccessSecured(roles = "USER")
    @PostMapping("/store/find-place")
    public ResponseResult<GoogleApiRequestResultStatus> findPlace(HttpServletRequest request, HttpServletResponse response, String name, @RequestParam(name = "postal-code") String postalCode) {

        ResponseResult<GoogleApiRequestResultStatus> result = ProjectBeanHolder.getStoreService().find(request, name, postalCode);

        response.setStatus(result.status().getCode());

        return result;
    }

    @PostMapping("/store/find-address")
    public ResponseResult<GoogleApiRequestResultStatus> address(HttpServletResponse response, @RequestParam(name = "store-id") String storeId) {

        ResponseResult<GoogleApiRequestResultStatus> result = ProjectBeanHolder.getStoreService().details(storeId);

        response.setStatus(result.status().getCode());

        if(result.contents().length > 1) {

            return new ResponseResult<>(result.status(), result.get(AddressForm.class));
        }

        return new ResponseResult<>(result.status());
    }

    @PostMapping("/store/{store-id}/check-in")
    public ResponseResult<StoreCheckInResultStatus> checkIn(@PathVariable(name = "store-id") String storeId, CustomerInfoForm info, List<WorkInfoForm> services) {

        return new ResponseResult(StoreCheckInResultStatus.SUCCESS);
    }

    @GetMapping("/store/{store-id}/check-in-page-details")
    public ResponseResult<StoreCheckInPageDetailResultStatus> checkInPageDetail(HttpServletResponse response, @PathVariable(name = "store-id") String storeId, String country, String language) throws IOException, ServiceDownException {

        ResponseResult<StoreCheckInPageDetailResultStatus> result = ProjectBeanHolder.getStoreService().getStoreCheckInPageDetail(storeId, new Locale(language, country));

        response.setStatus(result.status().getCode());

        return result;
    }


}
