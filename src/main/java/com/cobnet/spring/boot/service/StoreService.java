package com.cobnet.spring.boot.service;

import com.cobnet.interfaces.spring.repository.StoreRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AddressForm;
import com.cobnet.spring.boot.dto.StoreRegisterForm;
import com.cobnet.spring.boot.dto.StoreRegisterResult;
import com.cobnet.spring.boot.dto.support.StoreRegisterResultStatus;
import com.cobnet.spring.boot.entity.Store;
import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StoreService {

    @Autowired
    private StoreRepository repository;

    public StoreRegisterResult register(StoreRegisterForm storeForm, AddressForm addressForm) {

        Store store = storeForm.getEntity();
        store.setLocation(addressForm.getEntity());
        System.out.println();
        PlacesSearchResult[] results;

        try {
            System.out.println(store.getLocation().address());
            results = ProjectBeanHolder.getGoogleMapService().search(store.getName(), store.getPhone(), store.getLocation().address());

        } catch (IOException | InterruptedException | ApiException ex) {

            ex.printStackTrace();

            return new StoreRegisterResult(StoreRegisterResultStatus.SERVICE_DOWN);
        }

        if(results == null) {

            return new StoreRegisterResult(StoreRegisterResultStatus.STORE_NONEXISTENT);
        }

        for(PlacesSearchResult result : results) {

            System.out.println("Output:" + result);
        }



        //实体验证store数据是否可被创建
        //判断store是否存在 且store是否可被新数据覆盖或更改
        //



        return new StoreRegisterResult(StoreRegisterResultStatus.SUCCESS);
    }

    //
}
