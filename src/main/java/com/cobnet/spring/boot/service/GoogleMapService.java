package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AddressForm;
import com.cobnet.spring.boot.dto.GoogleAutocompletePredicted;
import com.cobnet.spring.boot.dto.ResponseResult;
import com.cobnet.spring.boot.dto.support.GoogleApiRequestResultStatus;
import com.cobnet.spring.boot.service.support.AutocompleteRequestCache;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceAutocompleteType;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class GoogleMapService {

    public PlacesSearchResult[] search(String... params) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().textSearchRequest().query(Arrays.toString(params)).await().results;
    }


    public ResponseResult<GoogleApiRequestResultStatus> autocompleteRequest(HttpServletRequest request, PlaceAutocompleteType type, AddressForm form, String... params) {

        HttpSession session = request.getSession(true);

        if(ProjectBeanHolder.getSecurityConfiguration().isHumanValidationEnable() && !ProjectBeanHolder.getHumanValidator().isValidated(session.getId())) {

            return new ResponseResult<>(GoogleApiRequestResultStatus.HUMAN_VALIDATION_REQUEST);
        }

        if(ProjectBeanHolder.getSecurityConfiguration().isSessionLimitEnable() && ProjectBeanHolder.getSecurityConfiguration().getSessionCreatedTimeRequire().compareTo(DateUtils.getInterval(new Date(session.getCreationTime()), DateUtils.now())) > 0) {

            return new ResponseResult<>(GoogleApiRequestResultStatus.REJECTED);
        }

        AutocompleteRequestCache cache = this.getAutocompleteRequestCache(session.getId());

        ProjectBeanHolder.getCacheService().set(AutocompleteRequestCache.GoogleMapServiceKey, request.getSession().getId(), new AutocompleteRequestCache(cache != null ? cache.createdTime() : DateUtils.now(), cache != null ? cache.times() + 1 : 0), cache != null ? ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration().minus(DateUtils.getInterval(DateUtils.now(), cache.createdTime())) : ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration());

        GoogleAutocompletePredicted result = null;

        if(cache != null) {

            if(cache.times() <= ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimit()) {

                try {

                    if(type == null) {

                        result = new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(Stream.of(params), Stream.of(form.address())).toArray(String[]::new)), session.getId()).await()).toList());

                    } else {

                        result = new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(Stream.of(params), Stream.of(form.address())).toArray(String[]::new)), session.getId()).types(type).await()).toList());
                    }

                } catch (IOException | ApiException | InterruptedException e) {

                    e.printStackTrace();
                }

                if(result == null) {

                    return new ResponseResult<>(GoogleApiRequestResultStatus.SERVICE_DOWN);
                }

                return new ResponseResult(GoogleApiRequestResultStatus.SUCCESS, result);
            }

            return new ResponseResult<>(GoogleApiRequestResultStatus.EXHAUSTED);
        }

        try {

            if(type == null) {

                result = new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(Stream.of(params), Stream.of(form.address())).toArray(String[]::new)), session.getId()).await()).toList());

            } else {

                result = new GoogleAutocompletePredicted(Arrays.stream(ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(Stream.concat(Stream.of(params), Stream.of(form.address())).toArray(String[]::new)), session.getId()).types(type).await()).toList());
            }
        } catch (IOException | ApiException | InterruptedException e) {

            e.printStackTrace();
        }

        if(result == null) {

            return new ResponseResult<>(GoogleApiRequestResultStatus.SERVICE_DOWN);
        }

        return new ResponseResult<>(GoogleApiRequestResultStatus.SUCCESS, result);
    }

    public AutocompleteRequestCache getAutocompleteRequestCache(String key) {

        return ProjectBeanHolder.getCacheService().get(AutocompleteRequestCache.GoogleMapServiceKey, key, AutocompleteRequestCache.class);
    }

    public PlaceDetails search(String placeId) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().placeDetailsRequest().placeId(placeId).await();
    }

}
