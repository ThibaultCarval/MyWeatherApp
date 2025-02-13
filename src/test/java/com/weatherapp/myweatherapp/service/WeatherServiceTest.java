package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.controller.WeatherController;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.model.CustomCityInfoGenerator;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherServiceTest {

  // TODO: 12/05/2023 write unit tests
    @Autowired MockMvc mvc;

    @MockBean
    WeatherService ws;

        private WeatherService    testWeatherService;
    private VisualcrossingRepository testWeatherRepo;
    @BeforeAll
    public void load_WeatherController() {
        testWeatherRepo = new VisualcrossingRepository(
                "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/",
                "NVPMMQN5ZFM8UF22TVR2MTXZT");
        testWeatherService = new WeatherService(testWeatherRepo);
    }

    @Test
    void verify_visualcrossingRepository_AccessForecast() throws Exception {
        System.out.println("Tests | verify_visualcrossingRepository_AccessForecast");
        CityInfo a = testWeatherRepo.getByCity("Paris");

        assert a != null;
        assert a.getAddress().contains("Paris");
    }

    @Test
    void verify_weatherService_AccessForecast() throws Exception {
        System.out.println("Tests | verify_weatherService_AccessForecast");
        CityInfo a = testWeatherService.forecastByCity("Paris");

        assert a != null;
        assert a.getAddress().contains("Paris");
    }

    @Test
    void verify_EmptyTest() throws Exception {
        System.out.println("Tests | verify_EmptyTest");

        CityInfo cityInfo = new CustomCityInfoGenerator("Paris", "00:00:00", "23:59:59", "0.7", "Rain");

        assert cityInfo != null && cityInfo.getAddress().equalsIgnoreCase("paris");
    }

    @Test
    void verify_CustomCityInfoGenerator() throws Exception {
        System.out.println("Tests | CustomCityInfoGenerator -> data copied correctly");
        // CustomCityInfoGenerator -> data copied correctly

        CityInfo cityInfo = new CustomCityInfoGenerator("Paris", "00:00:00", "23:59:59", "0.7", "Rain");

        assert cityInfo.getAddress().equalsIgnoreCase("Paris");
        assert cityInfo.getSunrise().equalsIgnoreCase("00:00:00");
        assert cityInfo.getSunset().equalsIgnoreCase("23:59:59");
        assert cityInfo.getSolarenergy().equalsIgnoreCase("0.7");
        assert cityInfo.getConditions().toLowerCase().contains("rain");

    }








}