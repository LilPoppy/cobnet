package com.cobnet.spring.boot.core;

import com.cobnet.spring.boot.configuration.GoogleConsoleConfiguration;
import com.google.maps.*;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class GoogleMap {

    private final GeoApiContext context;

    GoogleMap(GoogleConsoleConfiguration configuration) {

        GeoApiContext.Builder builder = new GeoApiContext.Builder().apiKey(configuration.getApiKey());

        if(configuration.getMap() != null) {

            if (configuration.getMap().getChannel() != null) {

                builder = builder.channel(configuration.getMap().getChannel());
            }

            if (configuration.getMap().getErrorTimeout() != null) {

                builder = builder.connectTimeout(configuration.getMap().getErrorTimeout().toMillis(), TimeUnit.MILLISECONDS);
            }

            if (configuration.getMap().getMaxRetries() != null) {

                builder = builder.maxRetries(configuration.getMap().getMaxRetries());
            }

            if (configuration.getMap().getClientId() != null && configuration.getMap().getCryptographicSecret() != null) {

                builder = builder.enterpriseCredentials(configuration.getMap().getClientId(), configuration.getMap().getCryptographicSecret());
            }
        }

        this.context = builder.build();
    }

    public TextSearchRequest textSearchRequest() {

        return new TextSearchRequest(this.context);
    }

    public NearbySearchRequest nearbySearchRequest() {

        return new NearbySearchRequest(this.context);
    }

    public PlaceDetailsRequest placeDetailsRequest() {

        return new PlaceDetailsRequest(this.context);
    }

    public PhotoRequest photoRequest() {

        return new PhotoRequest(this.context);
    }

    public PlaceAutocompleteRequest placeAutocompleteRequest(String input, PlaceAutocompleteRequest.SessionToken token) {

        return PlacesApi.placeAutocomplete(this.context, input, token);
    }

    public PlaceAutocompleteRequest placeAutocompleteRequest(String input, String token) {

        return this.placeAutocompleteRequest(input, new PlaceAutocompleteRequest.SessionToken(token));
    }

    public QueryAutocompleteRequest queryAutocompleteRequest(String input) {

        return  PlacesApi.queryAutocomplete(this.context, input);
    }

    public FindPlaceFromTextRequest findPlaceFromTextRequest() {

        return new FindPlaceFromTextRequest(this.context);
    }

    public GeocodingApiRequest geocodingApiRequest() {

        return new GeocodingApiRequest(this.context);
    }

    public GeolocationApiRequest geolocationApiRequest() {

        return GeolocationApi.newRequest(this.context);
    }

    public DirectionsApiRequest directionsApiRequest() {

        return new DirectionsApiRequest(this.context);
    }

    public DistanceMatrixApiRequest distanceMatrixApiRequest() {

        return new DistanceMatrixApiRequest(this.context);
    }

    public PendingResult<ElevationResult[]> getElevationByPath(int samples, LatLng... path) {

        return ElevationApi.getByPath(this.context, samples, path);
    }

    public PendingResult<ElevationResult[]> getElevationByPath(GeoApiContext context, int samples, EncodedPolyline encodedPolyline) {

        return ElevationApi.getByPath(this.context, samples, encodedPolyline);
    }

    public PendingResult<ElevationResult> getByPoint(LatLng location) {

        return ElevationApi.getByPoint(this.context, location);
    }

    public PendingResult<ElevationResult[]> getByPoints(LatLng... points) {

        return ElevationApi.getByPoints(this.context, points);
    }

    public PendingResult<ElevationResult[]> getByPoints(EncodedPolyline encodedPolyline) {
        return ElevationApi.getByPoints(this.context, encodedPolyline);
    }

    public StaticMapsRequest staticMapsRequest() {

        return new StaticMapsRequest(this.context);
    }

    public PendingResult<TimeZone> getTimeZone(LatLng location) {

        return TimeZoneApi.getTimeZone(this.context, location);
    }

    public NearestRoadsApiRequest nearestRoadsApiRequest() {

        return new NearestRoadsApiRequest(this.context);
    }

    public SpeedLimitsApiRequest speedLimitsApiRequest() {

        return new SpeedLimitsApiRequest(this.context);
    }

    public SnapToRoadsApiRequest snapToRoadsApiRequest() {

        return new SnapToRoadsApiRequest(this.context);
    }
}
