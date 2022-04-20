package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AddressForm;
import com.cobnet.spring.boot.dto.AutocompleteResult;
import com.cobnet.spring.boot.dto.support.AutocompleteResultStatus;
import com.cobnet.spring.boot.service.support.AutocompleteRequestCache;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class GoogleMapService {

    public PlacesSearchResult[] search(String... params) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().textSearchRequest().query(String.join(" ", params)).await().results;
    }

    public AutocompletePrediction[] autocomplete(HttpSession session, String... params) throws IOException, InterruptedException, ApiException {

        return autocomplete(session.getId(), params);
    }

    public AutocompletePrediction[] autocomplete(Session session, String... params) throws IOException, InterruptedException, ApiException {

        return autocomplete(session.getId(), params);
    }

    public AutocompletePrediction[] autocomplete(String token, String... params) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(Arrays.toString(params), token).await();
    }

    public AutocompleteResult autocompleteRequest(HttpServletRequest request, AddressForm form) {

        HttpSession session = request.getSession(true);

        if(ProjectBeanHolder.getSecurityConfiguration().isHumanValidationEnable() && !ProjectBeanHolder.getHumanValidator().isValidated(session.getId())) {

            return new AutocompleteResult(AutocompleteResultStatus.HUMAN_VALIDATION_REQUEST);
        }

        if(!ProjectBeanHolder.getSecurityConfiguration().isSessionLimitEnable() || ProjectBeanHolder.getSecurityConfiguration().getSessionCreatedTimeRequire().compareTo(DateUtils.getInterval(new Date(session.getCreationTime()), DateUtils.now())) > 0) {

            return new AutocompleteResult(AutocompleteResultStatus.REJECTED);
        }

        AutocompleteRequestCache cache = this.getAutocompleteRequestCache(session.getId());

        ProjectBeanHolder.getCacheService().set(AutocompleteRequestCache.GoogleMapServiceKey, request.getSession().getId(), new AutocompleteRequestCache(cache != null ? cache.createdTime() : DateUtils.now(), cache != null ? cache.times() + 1 : 0), cache != null ? ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration().minus(DateUtils.getInterval(DateUtils.now(), cache.createdTime())) : ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration());

        List<AutocompletePrediction> result = null;

        if(cache != null) {

            if(cache.times() <= ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimit()) {

                try {

                    result = Arrays.stream(autocomplete(session.getId(), form.address())).toList();

                } catch (IOException | ApiException | InterruptedException e) {

                    e.printStackTrace();
                }

                if(result == null) {

                    return new AutocompleteResult(AutocompleteResultStatus.SERVICE_DOWN);
                }

                return new AutocompleteResult(AutocompleteResultStatus.SUCCESS, result);
            }

            return new AutocompleteResult(AutocompleteResultStatus.EXHAUSTED);
        }

        try {

            result = Arrays.stream(autocomplete(request.getSession(), form.address())).toList();

        } catch (IOException | ApiException | InterruptedException e) {

            e.printStackTrace();
        }

        if(result == null) {

            return new AutocompleteResult(AutocompleteResultStatus.SERVICE_DOWN);
        }

        return new AutocompleteResult(AutocompleteResultStatus.SUCCESS, result);
    }

    public AutocompleteRequestCache getAutocompleteRequestCache(String key) {

        return ProjectBeanHolder.getCacheService().get(AutocompleteRequestCache.GoogleMapServiceKey, key, AutocompleteRequestCache.class);
    }

    public PlaceDetails search(String placeId) throws IOException, InterruptedException, ApiException {

        return ProjectBeanHolder.getGoogleMap().placeDetailsRequest().placeId(placeId).await();
    }

}
