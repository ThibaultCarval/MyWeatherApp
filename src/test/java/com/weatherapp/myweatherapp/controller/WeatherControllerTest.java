package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.model.CustomCityInfoGenerator;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeatherControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    WeatherController wc;

    private WeatherController testWeatherController;
    private WeatherService    testWeatherService;
    private VisualcrossingRepository testWeatherRepo;
    @BeforeAll
    public void load_WeatherController() {
        testWeatherRepo = new VisualcrossingRepository(
                "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/",
                "removed");
        testWeatherService = new WeatherService(testWeatherRepo);
        testWeatherController = new WeatherController(testWeatherService);
    }

    @Test
    void verify_AccessForecast() throws Exception {
        System.out.println("Tests | verify_AccessForecast");

//        MockHttpServletResponse r = mvc.perform(get("/forecast/" + "Paris")).andExpect(status().isOk()).andReturn().getResponse();
//        assert r.getContentAsString().contains("Paris");

        CityInfo cityInfo = testWeatherController.forecastByCity("Paris").getBody();

        assert cityInfo.getAddress().equalsIgnoreCase("Paris");
    }

    @Test
    void verify_correct_getSecondsFromTimestamp() throws Exception {
        System.out.println("Tests | WeatherController.getSecondsFromTimestamp -> correct conversion of timestamp");
        // WeatherController.getSecondsFromTimestamp -> correct conversion of timestamp


        assert testWeatherController.getSecondsFromTimestamp("00:00:00") == 0;
        assert testWeatherController.getSecondsFromTimestamp("00:00:10") == 10;
        assert testWeatherController.getSecondsFromTimestamp("23:58:59") == 23*60*60+58*60+59;

    }

    @Test
    void verify_incorrect_getSecondsFromTimestamp() throws Exception {
        System.out.println("Tests | WeatherController.getSecondsFromTimestamp -> invalid timestamp raises exception");
        // WeatherController.getSecondsFromTimestamp -> invalid timestamp raises exception

        try {
            testWeatherController.getSecondsFromTimestamp("00:00:00:00");}
        catch (Exception ignored) {return;}

        throw new Exception("Timestamp conversion should have failed");

    }

    @Test
    void verify_getDaylightHours_notNull() throws Exception {
        System.out.println("Tests | WeatherController.getDaylightHours -> Both timestamps not null");
        // WeatherController.getDaylightHours -> Both timestamps not null

        CityInfo cityInfo = new CustomCityInfoGenerator(
                "Testing",
                "10:22:37", "18:45:31",
                "5", "Rain");
        long daylight = testWeatherController.getDaylightHours(cityInfo);

        assert daylight == (18*3600+45*60+31)-(10*3600+22*60+37);
    }

    @Test
    void verify_getDaylightHours_bothNullPolarDay() throws Exception {
        System.out.println("Tests | WeatherController.getDaylightHours -> Both timestamps null, polar day");
        // WeatherController.getDaylightHours -> Both timestamps null, polar day

        CityInfo cityInfo = new CustomCityInfoGenerator(
                "Testing",
                "null", null,
                "5", "Rain");
        long daylight = testWeatherController.getDaylightHours(cityInfo);

        assert daylight == 24*3600;
    }

    @Test
    void verify_getDaylightHours_bothNullPolarNight() throws Exception {
        System.out.println("Tests | WeatherController.getDaylightHours -> Both timestamps null, polar night");
        // WeatherController.getDaylightHours -> Both timestamps null, polar night

        CityInfo cityInfo = new CustomCityInfoGenerator(
                "Testing",
                "null", null,
                "0", "Rain");
        long daylight = testWeatherController.getDaylightHours(cityInfo);

        assert daylight == 0;
    }

    @Test
    void verify_getDaylightHours_riseNull() throws Exception {
        System.out.println("Tests | WeatherController.getDaylightHours -> sunrise null, sunset not null");
        // WeatherController.getDaylightHours -> sunrise null, sunset not null

        CityInfo cityInfo = new CustomCityInfoGenerator(
                "Testing",
                "null", "18:45:31",
                "5", "Rain");
        long daylight = testWeatherController.getDaylightHours(cityInfo);

        assert daylight == 18*3600+45*60+31;
    }
    @Test
    void verify_getDaylightHours_setNull() throws Exception {
        System.out.println("Tests | WeatherController.getDaylightHours -> sunrise not null, sunset null");
        // WeatherController.getDaylightHours -> sunrise not null, sunset null

        CityInfo cityInfo = new CustomCityInfoGenerator(
                "Testing",
                "10:22:37", "null",
                "5", "Rain");
        long daylight = testWeatherController.getDaylightHours(cityInfo);

        assert daylight == 24*3600-(10*3600+22*60+37);
    }
    @Test
    void verify_getDaylightHours_checkNull() throws Exception {
        System.out.println("Tests | WeatherController.getDaylightHours -> cityinfo null");
        // WeatherController.getDaylightHours -> cityinfo null

        try {
            testWeatherController.getDaylightHours(null);
        }
        catch (Exception ignored) {return;}

        throw new Exception("WeatherController.getDaylightHours -> null checks should have failed");
    }
    @Test
    void verify_compareDaylightHours_repeatCity() throws Exception {
        System.out.println("Tests | WeatherController.compareDaylightHours -> Same city twice");
        // WeatherController.compareDaylightHours -> Same city twice

        ResponseEntity<CityInfo> responseEntity = testWeatherController.compareDaylightHours("Paris", "Paris");
        CityInfo cityInfo = Objects.requireNonNull(responseEntity.getBody());
        assert cityInfo.getAddress().equalsIgnoreCase("Paris");
    }
    @Test
    void verify_compareDaylightHours_permutations() throws Exception {
        System.out.println("Tests | WeatherController.compareDaylightHours -> two cities, both permutations, same return");
        // WeatherController.compareDaylightHours -> two cities, both permutations, same return

        CityInfo responseEntityA = Objects.requireNonNull(testWeatherController.compareDaylightHours("Paris", "Koln").getBody());
        CityInfo responseEntityB = Objects.requireNonNull(testWeatherController.compareDaylightHours("Koln", "Paris").getBody());


        assert responseEntityA.getAddress().equalsIgnoreCase(responseEntityB.getAddress());

    }
    @Test
    void verify_compareDaylightHours_checkNull() throws Exception {
        System.out.println("Tests | WeatherController.compareDaylightHours -> null checks");
        // WeatherController.compareDaylightHours -> null checks

        try {Objects.requireNonNull(testWeatherController.compareDaylightHours(null, "Koln").getBody());}
        catch (Exception ignored) {return;}

        throw new Exception("WeatherController.compareDaylightHours -> null checks should have failed");

    }

    @Test
    void verify_compareRain() throws Exception {
        System.out.println("Tests | WeatherController.compareRain -> Works properly");

        CityInfo cityInfoA = new CustomCityInfoGenerator(
                "TestingA", "null", "null", "null",
                "Rain");
        CityInfo cityInfoB = new CustomCityInfoGenerator(
                "TestingB", "null", "null", "null",
                "Overcast");

        ResponseEntity<List<CityInfo>> responseEntity = testWeatherController.compareRain(cityInfoA, cityInfoB);
        List<CityInfo> cityInfos = Objects.requireNonNull(responseEntity.getBody());

        assert cityInfos.size() == 1;
        assert cityInfos.get(0).getAddress().equalsIgnoreCase("TestingA");
    }
    @Test
    void verify_compareRain_nullCheck() throws Exception {
        System.out.println("Tests | WeatherController.compareRain -> nullCheck");

        CityInfo cityInfoA = null;
        CityInfo cityInfoB = null;

        try {
            testWeatherController.compareRain(cityInfoA, cityInfoB);
        }
        catch (Exception ignored) {return;}

        throw new Exception("WeatherController.getDaylightHours -> null checks should have triggered");
    }
}
