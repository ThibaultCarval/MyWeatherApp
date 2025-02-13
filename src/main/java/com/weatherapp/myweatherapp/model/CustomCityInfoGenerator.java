package com.weatherapp.myweatherapp.model;

public class CustomCityInfoGenerator extends CityInfo{

    public CustomCityInfoGenerator(
            String address,
            String sunrise,
            String sunset,
            String solarenergy,
            String conditions
    ) {
        this.address = address;
        this.currentConditions = new CurrentConditions();
        this.currentConditions.sunrise = sunrise;
        this.currentConditions.sunset = sunset;
        this.currentConditions.solarenergy = solarenergy;
        this.currentConditions.conditions = conditions;
    }
}
