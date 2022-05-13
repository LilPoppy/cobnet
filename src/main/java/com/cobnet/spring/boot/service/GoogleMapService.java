package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.spring.repository.AutocompleteRequestCacheRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AddressForm;
import com.cobnet.spring.boot.dto.GoogleAutocompletePredicted;
import com.cobnet.spring.boot.dto.support.GoogleApiRequestResultStatus;
import com.cobnet.spring.boot.cache.GoogleMapRequestCache;
import com.cobnet.spring.boot.dto.support.ServiceRequestStatus;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class GoogleMapService {

    @Autowired
    private AutocompleteRequestCacheRepository repository;

    public PlacesSearchResult[] search(String... params) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().textSearchRequest().query(Arrays.toString(params)).await().results;
    }

    public PlaceDetails search(String placeId) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().placeDetailsRequest().placeId(placeId).await();
    }

    //TODO: Place entity to store the search cache and reuse it again.
    public AddressForm findPlaceAddressRequest(HttpServletRequest request, String placeId) {

        return this.invoke(request, cache -> {

            try {

                PlaceDetails details = this.search(placeId);

                if(details == null) {

                    throw new ResponseFailureStatusException(GoogleApiRequestResultStatus.EMPTY);
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

                return new AddressForm(street.toString(), unit.toString(), city.toString(), state.toString(), country.toString(), postalCode.toString());

            } catch (IOException | ApiException | InterruptedException e) {

                throw new ResponseFailureStatusException(ServiceRequestStatus.SERVICE_DOWN);
            }
        });
    }


    public GoogleAutocompletePredicted autocompleteRequest(HttpServletRequest request, PlaceAutocompleteType type, AddressForm form, String... params) throws ResponseFailureStatusException {

        return this.invoke(request, cache -> {

            try {

                if(type == null) {

                    return new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(Stream.of(params), Stream.of(form.address())).toArray(String[]::new)), cache.getId()).await()).toList());

                } else {

                    return new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(Stream.of(params), Stream.of(form.address())).toArray(String[]::new)), cache.getId()).types(type).await()).toList());
                }

            } catch (IOException | ApiException | InterruptedException e) {

                throw new ResponseFailureStatusException(ServiceRequestStatus.SERVICE_DOWN);
            }
        });
    }

    public <T> T invoke(HttpServletRequest request, Function<GoogleMapRequestCache, T> predicated) {

        HttpSession session = request.getSession(true);

        GoogleMapRequestCache cache = this.getGoogleMapRequestCache(session.getId());

        this.setGoogleMapRequestCache(new GoogleMapRequestCache(request.getSession().getId(), cache != null ? cache.getCreationTime() : DateUtils.now(), cache != null ? cache.getCount() + 1 : 0), cache != null ? ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration().minus(DateUtils.getInterval(DateUtils.now(), cache.getCreationTime())) : ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration());

        if(cache != null) {

            if(cache.getCount() > ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimit()) {

                throw new ResponseFailureStatusException(ServiceRequestStatus.EXHAUSTED);
            }
        }

        return predicated.apply(cache);
    }

    public GoogleMapRequestCache getGoogleMapRequestCache(String key) {

        Optional<GoogleMapRequestCache> optional = repository.findById(key);

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public GoogleMapRequestCache setGoogleMapRequestCache(GoogleMapRequestCache cache) {

        return setGoogleMapRequestCache(cache, null);
    }

    public GoogleMapRequestCache setGoogleMapRequestCache(GoogleMapRequestCache cache, Duration expiration) {

        if(expiration != null && expiration.isZero()) {

            return repository.save(cache, expiration);
        }

        return repository.save(cache);
    }

}
