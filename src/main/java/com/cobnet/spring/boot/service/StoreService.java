package com.cobnet.spring.boot.service;

import com.cobnet.interfaces.spring.repository.StoreRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.StoreRegisterForm;
import com.cobnet.spring.boot.dto.StoreRegisterResult;
import com.cobnet.spring.boot.dto.support.StoreRegisterResultStatus;
import com.cobnet.spring.boot.entity.Store;
import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    @Autowired
    private StoreRepository repository;

    public StoreRegisterResult register(StoreRegisterForm form) {


        return new StoreRegisterResult(StoreRegisterResultStatus.SUCCESS);

    }

//    public StoreRegisterResult register(Store store) {
//
//
//        ProjectBeanHolder.getStoreRepository().saveAndFlush(store);
//    }
}
