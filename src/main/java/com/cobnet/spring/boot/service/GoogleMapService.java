package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.spring.repository.AutocompleteRequestCacheRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AddressForm;
import com.cobnet.spring.boot.dto.GoogleAutocompletePredicted;
import com.cobnet.spring.boot.dto.support.GoogleApiRequestResultStatus;
import com.cobnet.spring.boot.cache.AutocompleteRequestCache;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceAutocompleteType;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GoogleMapService {

    @Autowired
    private AutocompleteRequestCacheRepository repository;

    public PlacesSearchResult[] search(String... params) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().textSearchRequest().query(Arrays.toString(params)).await().results;
    }


    public GoogleAutocompletePredicted autocompleteRequest(HttpServletRequest request, PlaceAutocompleteType type, AddressForm form, String... params) throws ResponseFailureStatusException {

        HttpSession session = request.getSession(true);

        AutocompleteRequestCache cache = this.getAutocompleteRequestCache(session.getId());

        this.setAutocompleteRequestCache(new AutocompleteRequestCache(request.getSession().getId(), cache != null ? cache.getCreationTime() : DateUtils.now(), cache != null ? cache.getCount() + 1 : 0), cache != null ? ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration().minus(DateUtils.getInterval(DateUtils.now(), cache.getCreationTime())) : ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration());

        if(cache != null) {

            if(cache.getCount() <= ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimit()) {

                return getGoogleAutocompletePredicted(type, form, session, Stream.of(params), params);
            }

            throw new ResponseFailureStatusException(GoogleApiRequestResultStatus.EXHAUSTED);
        }

        return getGoogleAutocompletePredicted(type, form, session, Stream.of(params), params);
    }

    private GoogleAutocompletePredicted getGoogleAutocompletePredicted(PlaceAutocompleteType type, AddressForm form, HttpSession session, Stream<String> params2, String[] params) throws ResponseFailureStatusException {

        try {

            if(type == null) {

                return new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(Stream.of(params), Stream.of(form.address())).toArray(String[]::new)), session.getId()).await()).toList());

            } else {

                return new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(params2, Stream.of(form.address())).toArray(String[]::new)), session.getId()).types(type).await()).toList());
            }

        } catch (IOException | ApiException | InterruptedException e) {

            throw new ResponseFailureStatusException(GoogleApiRequestResultStatus.SERVICE_DOWN);
        }
    }

    public AutocompleteRequestCache getAutocompleteRequestCache(String key) {

        Optional<AutocompleteRequestCache> optional = repository.findById(key);

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public AutocompleteRequestCache setAutocompleteRequestCache(AutocompleteRequestCache cache) {

        return setAutocompleteRequestCache(cache, null);
    }

    public AutocompleteRequestCache setAutocompleteRequestCache(AutocompleteRequestCache cache, Duration expiration) {

        if(expiration != null && expiration.isZero()) {

            return repository.save(cache, expiration);
        }

        return repository.save(cache);
    }

    public PlaceDetails search(String placeId) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().placeDetailsRequest().placeId(placeId).await();
    }

}
