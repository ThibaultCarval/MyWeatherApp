package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

  @Autowired
  VisualcrossingRepository weatherRepo;

  public WeatherService() {}
  public WeatherService(VisualcrossingRepository weatherRepo) {this.weatherRepo = weatherRepo;}

  public CityInfo forecastByCity(String city) throws Exception {
      return weatherRepo.getByCity(city);
  }
}
