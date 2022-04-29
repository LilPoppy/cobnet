package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.google.maps.model.AutocompletePrediction;

import java.util.List;

public record GoogleAutocompletePredicted(List<AutocompletePrediction> prediction) implements ApplicationJson {
}
