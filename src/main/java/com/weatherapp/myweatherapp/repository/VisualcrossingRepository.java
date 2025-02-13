package com.weatherapp.myweatherapp.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.myweatherapp.model.CityInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.stream.Collectors;

@Repository
public class VisualcrossingRepository {

  @Value("${weather.visualcrossing.url}")
  String url;
  @Value("${weather.visualcrossing.key}")
  String key;

  public VisualcrossingRepository() {}
  public VisualcrossingRepository(String url, String key) {this.url = url; this.key = key;}

  public CityInfo getByCity(String city) throws Exception {
    if (url == null || url.isEmpty() || key == null || key.isEmpty()) throw new Exception("URL or KEY not loaded");

    String uri = url + "timeline/" +city + "?key=" + key;

    RestTemplate restTemplate = new RestTemplate();
    InputStream stream = restTemplate.getRequestFactory().createRequest(URI.create(uri), HttpMethod.GET).execute().getBody();
    String result = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));

    ObjectMapper objectMapper = new ObjectMapper();
    CityInfo cityInfo = objectMapper.readValue(result, CityInfo.class);
    if (cityInfo == null) throw new Exception("Failed to custom-access API");

    return cityInfo;

  }
}
