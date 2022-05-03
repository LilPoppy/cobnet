package com.cobnet.spring.boot.service;

import com.cobnet.common.Delegate;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.exception.ServiceDownException;
import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.interfaces.spring.repository.StoreRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.*;
import com.cobnet.spring.boot.entity.Store;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceAutocompleteType;
import com.google.maps.model.PlaceDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
public class StoreService {

    @Autowired
    private StoreRepository repository;

    public ResponseResult<GoogleApiRequestResultStatus> find(HttpServletRequest request, String name, String postalCode) {

        AddressForm form = new AddressForm();
        form.setPostalCode(postalCode);

        return ProjectBeanHolder.getGoogleMapService().autocompleteRequest(request, PlaceAutocompleteType.ESTABLISHMENT, form, name);
    }

    public ResponseResult<GoogleApiRequestResultStatus> details(String storeId) {

        Optional<Store> store = repository.findById(storeId);

        if(store.isPresent()) {

            return new ResponseResult<>(GoogleApiRequestResultStatus.SUCCESS,
                    new ObjectWrapper<>("name", store.get().getName()),
                    new AddressForm(store.get().getLocation().getStreet(), store.get().getLocation().getUnit(), store.get().getLocation().getCity(), store.get().getLocation().getState(), store.get().getLocation().getCountry(), store.get().getLocation().getPostalCode()),
                    new ObjectWrapper<>("is-permanently-closed", store.get().isPermanentlyClosed()),
                    new ObjectWrapper<>("phone-number", store.get().getPhone()),
                    new ObjectWrapper<>("rating", store.get().getRating()),
                    new ObjectWrapper<>("created", true));
        }

        return googleDetails(storeId);
    }

    public Store fetch(Store store) throws ResponseFailureStatusException {

        ResponseResult<GoogleApiRequestResultStatus> result = googleDetails(store.getId());

        if(result.status() == GoogleApiRequestResultStatus.SUCCESS && result.contents().length > 0) {

            store.setName((String) result.get(ObjectWrapper.class, "name").getValue());
            store.setLocation(result.get(AddressForm.class).getEntity());
            store.setPhone((String) result.get(ObjectWrapper.class, "phone-number").getValue());
            store.setPermanentlyClosed((Boolean) result.get(ObjectWrapper.class, "is-permanently-closed").getValue());
            store.setRating((Float) result.get(ObjectWrapper.class, "rating").getValue());

            return store;
        }

        throw new ResponseFailureStatusException(result.status());
    }

    public ResponseResult<GoogleApiRequestResultStatus> googleDetails(String storeId) {

        try {

            PlaceDetails details = ProjectBeanHolder.getGoogleMapService().search(storeId);

            if(details == null) {

                return new ResponseResult<>(GoogleApiRequestResultStatus.FAILED);
            }

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

            return new ResponseResult<>(GoogleApiRequestResultStatus.SUCCESS,
                    new ObjectWrapper<>("name", details.name),
                    new AddressForm(street.toString(), unit.toString(), city.toString(), state.toString(), country.toString(), postalCode.toString()),
                    new ObjectWrapper<>("is-permanently-closed", details.permanentlyClosed),
                    new ObjectWrapper<>("phone-number", details.internationalPhoneNumber),
                    new ObjectWrapper<>("rating", details.rating));

        } catch (IOException | InterruptedException | ApiException e) {

            if(e.fillInStackTrace() instanceof InvalidRequestException) {

                return new ResponseResult<>(GoogleApiRequestResultStatus.BAD_REQUEST);
            }

            e.printStackTrace();

            return new ResponseResult<>(GoogleApiRequestResultStatus.SERVICE_DOWN);
        }
    }

//    public ResponseResult<StoreServiceModifyResultStatus> save(String storeId, Set<ServiceOption> options) {
//
//    }

    public ResponseResult<StoreRegisterResultStatus> register(String storeId) {

        if(repository.findById(storeId).isPresent()) {

            return new ResponseResult<>(StoreRegisterResultStatus.STORE_ALREADY_REGISTERED);
        }

        try {

            Store store = this.fetch(new Store.Builder().setPlaceId(storeId).build());

            if(store.isPermanentlyClosed()) {

                return new ResponseResult<>(StoreRegisterResultStatus.STORE_PERMANENTLY_CLOSED);
            }

            repository.save(store);

            return new ResponseResult<>(StoreRegisterResultStatus.SUCCESS);

        } catch (ResponseFailureStatusException ex) {

            GoogleApiRequestResultStatus status = (GoogleApiRequestResultStatus) ex.getStatus();

            return switch (status) {

                case SUCCESS, BAD_REQUEST -> new ResponseResult<>(StoreRegisterResultStatus.STORE_NONEXISTENT);
                case FAILED, SERVICE_DOWN -> new ResponseResult<>(StoreRegisterResultStatus.SERVICE_DOWN);
                case EXHAUSTED -> new ResponseResult<>(StoreRegisterResultStatus.EXHAUSTED);
                case HUMAN_VALIDATION_REQUEST -> new ResponseResult<>(StoreRegisterResultStatus.HUMAN_VALIDATION_REQUEST);
                case REJECTED -> new ResponseResult<>(StoreRegisterResultStatus.SECURITY_CHECK);
            };
        }
    }

    public ResponseResult<StoreCheckInPageDetailResultStatus> getStoreCheckInPageDetail(String storeId, Locale locale) throws IOException {

        Optional<Store> store = repository.findById(storeId);

        if(store.isEmpty()) {

            return new ResponseResult<>(StoreCheckInPageDetailResultStatus.NO_EXIST);
        }

        try {

            final String textMale = ProjectBeanHolder.getTranslatorMessageSource().getMessage("gender.male", locale);
            final String textFemale = ProjectBeanHolder.getTranslatorMessageSource().getMessage("gender.female", locale);

            List<String> referralOptions = new ArrayList<>();

            for(SurveyReferralOption option : SurveyReferralOption.values()) {

                referralOptions.add(ProjectBeanHolder.getTranslatorMessageSource().getMessage(option.getKey(), locale));
            }

            List<com.cobnet.spring.boot.entity.Service> services = new ArrayList<>();

            for(com.cobnet.spring.boot.entity.Service service : store.get().getServices()) {

                service.setName(ProjectBeanHolder.getTranslatorMessageSource().getMessage(service.getName(), locale));
            }

            //显示所有可执行的服务项目
//            store.get().getServices().stream().forEach(service -> {
//
//            });

            //再创建一个方法

            var aaa = new StaffRequiredServiceOption(null, 1, new HashMap<>());

            return new ResponseResult<>(StoreCheckInPageDetailResultStatus.SUCCESS, new DynamicPage(new Properties(),
                new StepContainerPageField(0, "firstName", ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.first-name", locale), PageFieldType.INPUT, new Properties()),
                new StepContainerPageField(0, "lastName", ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.last-name", locale), PageFieldType.INPUT, new Properties()),
                new StepContainerPageField(1, "gender", ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.gender", locale), new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

                    delegator.put("data-score", "gender");
                    delegator.put("label", textMale);

                    return delegator;

                })), new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

                    delegator.put("data-score", "gender");
                    delegator.put("label", textFemale);

                    return delegator;

                }))),
                new StepContainerPageField(2, "phoneNumber", ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.phone-number", locale), PageFieldType.INPUT, new Properties()),
                new StepContainerPageField(3, "referral", ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.referral", locale), referralOptions.stream().map(option -> new DynamicPageField(PageFieldType.RADIO, new Delegate<>(new Properties()).invoke(delegator -> {

                    delegator.put("label", option);

                    return  delegator;

                }))).toArray(DynamicPageField[]::new)),
                new StepContainerPageField(4, "services", ProjectBeanHolder.getTranslatorMessageSource().getMessage("label.service-select", locale, services.stream().map(service -> new DynamicPageField(PageFieldType.CHECK_BOX, new Delegate<>(new Properties()).invoke(delegator -> {

                    delegator.put("label", service.getName());

                    return delegator;

                }))))),
                new StepContainerPageField(5, aaa.name(), aaa.getDisplayDescription(locale), aaa.getFields(locale))
            ));

        } catch (ServiceDownException ex) {

            return new ResponseResult<>(StoreCheckInPageDetailResultStatus.SERVICE_DOWN);
        }

    }
}
