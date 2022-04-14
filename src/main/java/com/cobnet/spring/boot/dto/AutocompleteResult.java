package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.AutocompleteResultStatus;
import com.google.maps.model.AutocompletePrediction;

import java.util.List;

public record AutocompleteResult(AutocompleteResultStatus status, List<AutocompletePrediction> result) implements ApplicationJson {

    public AutocompleteResult(AutocompleteResultStatus status) {

        this(status, null);
    }

    @Override
    public AutocompleteResultStatus status() {
        return status;
    }

    @Override
    public List<AutocompletePrediction> result() {
        return result;
    }
}
