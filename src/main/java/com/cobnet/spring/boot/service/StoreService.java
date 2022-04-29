package com.cobnet.spring.boot.service;

import com.cobnet.exception.ServiceDownException;
import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.interfaces.spring.repository.StoreRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.CheckInFormField;
import com.cobnet.spring.boot.dto.DynamicPage;
import com.cobnet.spring.boot.dto.ResponseResult;
import com.cobnet.spring.boot.dto.StoreRegisterForm;
import com.cobnet.spring.boot.dto.support.StoreCheckInPageDeatilResultStatus;
import com.cobnet.spring.boot.dto.support.StoreRegisterResultStatus;
import com.cobnet.spring.boot.dto.support.UIType;
import com.cobnet.spring.boot.entity.Address;
import com.cobnet.spring.boot.entity.Store;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class StoreService {

    @Autowired
    private StoreRepository repository;

    public ResponseResult<StoreRegisterResultStatus> register(StoreRegisterForm storeForm) {

        //ChIJKZHAALQtMYYRnE0zrKPsS8I
        Store store = storeForm.getEntity();

        try {

            PlaceDetails details = ProjectBeanHolder.getGoogleMapService().search(store.getId());

            if(details == null) {

                return new ResponseResult<>(StoreRegisterResultStatus.STORE_NONEXISTENT);
            }

            if(details.permanentlyClosed) {

                return new ResponseResult<>(StoreRegisterResultStatus.STORE_PERMANENTLY_CLOSED);
            }

            store.setName(details.name);

            store.setPhone(details.internationalPhoneNumber);

            List<AddressComponent> components = Arrays.stream(ProjectBeanHolder.getGoogleMap().geocodingApiRequest().address(details.formattedAddress).await()).map(result -> result.addressComponents).flatMap(Arrays::stream).toList();

            StringBuilder street = new StringBuilder(), unit = new StringBuilder(), city = new StringBuilder(), state = new StringBuilder(), postalCode = new StringBuilder(), country = new StringBuilder();

            for(var component : components) {

                if(Arrays.stream(component.types).toList().contains(AddressComponentType.STREET_NUMBER)) {

                    street.insert(0, component.shortName);
                }

                if(Arrays.stream(component.types).toList().contains(AddressComponentType.ROUTE)) {

                    street.insert(street.length(), " ").insert(street.length(), component.shortName);
                }

                if(Arrays.stream(component.types).toList().contains(AddressComponentType.SUBPREMISE)) {

                    unit.append("Ste #").append(component.shortName.toUpperCase());
                }

                if(Arrays.stream(component.types).toList().containsAll(List.of(AddressComponentType.LOCALITY, AddressComponentType.POLITICAL))) {

                    city.append(component.shortName);
                }

                if(Arrays.stream(component.types).toList().containsAll(List.of(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1, AddressComponentType.POLITICAL))) {

                    state.append(component.shortName);
                }

                if(Arrays.stream(component.types).toList().containsAll(List.of(AddressComponentType.COUNTRY, AddressComponentType.POLITICAL))) {

                    country.append(component.longName);
                }

                if(Arrays.stream(component.types).toList().contains(AddressComponentType.POSTAL_CODE)) {

                    postalCode.append(component.shortName);
                }
            }

            store.setLocation(new Address.Builder().setStreet(street.toString()).setUnit(unit.toString()).setCity(city.toString()).setState(state.toString()).setCountry(country.toString()).setZipCode(Integer.parseInt(postalCode.toString())).build());

        } catch (IOException | InterruptedException | ApiException ex) {

            ex.printStackTrace();

            return new ResponseResult<>(StoreRegisterResultStatus.SERVICE_DOWN);
        }

        Optional<Store> existent = repository.findById(store.getId());

        if(existent.isPresent()) {

            return new ResponseResult<>(StoreRegisterResultStatus.STORE_EXISTED);
        }

        repository.save(store);

        return new ResponseResult<>(StoreRegisterResultStatus.SUCCESS);
    }

    public ResponseResult<StoreCheckInPageDeatilResultStatus> getStoreCheckInPageFields(String storeId, Locale locale) throws IOException, ServiceDownException {

        List<PageField> fields = new ArrayList<>();



        DynamicPage page = new DynamicPage();

        fields.add(new CheckInFormField(0, "firstName", null, ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.first-name", locale), UIType.INPUT, null));
        fields.add(new CheckInFormField(1, "lastName", null, ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.last-name", locale), UIType.INPUT, null));
        fields.add(new CheckInFormField(2,"gender", null, ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.gender", locale), UIType.SELECT, null));
        fields.add(new CheckInFormField(3,"phoneNumber", null, ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.phone-number", locale), UIType.PHONE_INPUT, null));
        fields.add(new CheckInFormField(4,"referral", null, ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.referral", locale), UIType.SELECT, null));

        return fields;
    }
}
