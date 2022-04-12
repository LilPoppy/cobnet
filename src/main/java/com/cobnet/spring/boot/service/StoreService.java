package com.cobnet.spring.boot.service;

import com.cobnet.spring.boot.dto.StoreRegisterForm;
import com.cobnet.spring.boot.dto.StoreRegisterResult;
import com.cobnet.spring.boot.dto.support.StoreRegisterResultStatus;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    public StoreRegisterResult register(StoreRegisterForm form) {

        return new StoreRegisterResult(StoreRegisterResultStatus.SUCCESS);

    }
}
