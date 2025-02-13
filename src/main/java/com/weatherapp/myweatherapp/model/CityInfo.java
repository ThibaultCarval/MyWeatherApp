package com.weatherapp.myweatherapp.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityInfo {

  public CityInfo() {}
  @JsonProperty("address")
  String address;

  @JsonProperty("description")
  String description;

  @JsonProperty("currentConditions")
  CurrentConditions currentConditions;

  @JsonProperty("days")
  List<Days> days;

  static class CurrentConditions {
    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("sunrise")
    String sunrise;

    @JsonProperty("sunset")
    String sunset;

    @JsonProperty("feelslike")
    String feelslike;

    @JsonProperty("humidity")
    String humidity;

    @JsonProperty("conditions")
    String conditions;

    // Added to check polar day/night
    @JsonProperty("solarenergy")
    String solarenergy;

    private Map<String, Object> optional = new HashMap<>();
    @JsonAnySetter
    private void addOptional(String name, Object value) {
      optional.put(name, value);
    }
    @JsonAnyGetter
    private Object getOptional(String name) {
      return optional.get(name);
    }

  }

  static class Days {

    @JsonProperty("datetime")
    String date;

    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("tempmax")
    String maxTemperature;

    @JsonProperty("tempmin")
    String minTemperature;

    @JsonProperty("conditions")
    String conditions;

    @JsonProperty("description")
    String description;

    private Map<String, Object> optional = new HashMap<>();
    @JsonAnySetter
    private void addOptional(String name, Object value) {
      optional.put(name, value);
    }
    @JsonAnyGetter
    private Object getOptional(String name) {
      return optional.get(name);
    }
  }

  private Map<String, Object> optional = new HashMap<>();
  @JsonAnySetter
  private void addOptional(String name, Object value) {
    optional.put(name, value);
  }
  @JsonAnyGetter
  private Object getOptional(String name) {
    return optional.get(name);
  }

  public CurrentConditions getCurrentConditions() {
    return currentConditions;
  }

  public String getSunrise() {
    return currentConditions.sunrise;
  }

  public String getSunset() {
    return currentConditions.sunset;
  }

  public String getSolarenergy() {
    return currentConditions.solarenergy;
  }

  public String getConditions() {
    return currentConditions.conditions;
  }

  public String getAddress() {
    return address;
  }

}
