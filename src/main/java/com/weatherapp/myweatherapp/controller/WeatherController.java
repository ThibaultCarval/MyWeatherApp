package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WeatherController {

    @Autowired
    WeatherService weatherService;

    public WeatherController() {}
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast/{city}")
    public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) throws Exception {
        CityInfo ci = weatherService.forecastByCity(city);

        return ResponseEntity.ok(ci);
    }

    public int getSecondsFromTimestamp(String timestamp) throws Exception {
        /* Split the timestamp from format: "18:01:50"
         * into a single number of seconds
         */

        if (timestamp == null) throw new NullPointerException("Timestamp is null");
        if (!timestamp.matches("\\d\\d:\\d\\d:\\d\\d")) throw new Exception("Timestamp is incorrect format");

        // Instead of manually converting the time, use a library
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse(timestamp, timeFormatter);

        /*
         * Find the seconds within the day.
         * As the timestamps have resolutions down to the second, this is a valid measure.
         */
        return time.toSecondOfDay();

    }

    public long getDaylightHours(CityInfo cityInfo) throws Exception {
        /* Obtain the length in seconds of the daylight hours in a specific city.
         * It would make more sense to put it in CityInfo.java,
         * but I am only modifying WeatherController.java.
         */

        if (cityInfo == null) throw new NullPointerException("cityInfo is null");

        /* Extract specific variables needed to evaluate daylight hours for a city
         * It would make more sense to directly get the sunrise/sunset as EPOCHs, given then they are available in the API,
         * but it is not extractable in the current definition of CityInfo's currentConditions
         * Daylight savings only occurs during the night (impact minimisation). Therefore, we do need to consider it as an edge case.
         * Furthermore, the api does not specify the time type. Therefore, there is no need to consider 25- or 23- hour days.
         */
        String sunriseTimestamp = cityInfo.getSunrise(); // Could be null over the polar circles
        String sunsetTimestamp  = cityInfo.getSunset() ; // Could be null over the polar circles
        String solarEnergyS = cityInfo.getSolarenergy();
        float solarEnergy = (solarEnergyS == null || solarEnergyS.isEmpty()) ? 0f : Float.parseFloat(solarEnergyS);

        /*
         * Convert String timestamps to seconds within the day. Makes for easier manipulation
         *
         * Both sunrise and sunset timestamps can be non-null (day usual pattern) or null (polar day/night).
         * This gives us 4 cases for the (sunrise_time, sunset_time) couple:
         * (Non-null, Non-null): typical pattern where the sun rises and sets during the day;
         * (Null, Non-null): no sunrise but sunset: end of polar day.
         * (Non-Null, Null): sunrise but no sunset: beginning of polar day
         * (Null, Null): either established polar day or polar night;
         *      distinguishing between both is based on solar energy:
         *      if null or 0 then polar night, otherwise polar day.
         */

        long sunriseSeconds = (sunriseTimestamp != null && !sunriseTimestamp.equalsIgnoreCase("null")) ? getSecondsFromTimestamp(sunriseTimestamp) : 0;
        long sunsetSeconds = (sunsetTimestamp != null && !sunsetTimestamp.equalsIgnoreCase("null")) ? getSecondsFromTimestamp(sunsetTimestamp) : 24*3600;
        if (solarEnergy == 0f &&
                (sunsetTimestamp  == null || sunsetTimestamp .equalsIgnoreCase("null")) &&
                (sunriseTimestamp == null || sunriseTimestamp.equalsIgnoreCase("null")))
            sunsetSeconds = 0; // In full Polar night (Polar day is covered by default above)

        return sunsetSeconds - sunriseSeconds;
    }

    @GetMapping("/compare/daylight/{cityA}/{cityB}")
    public ResponseEntity<CityInfo> compareDaylightHours(@PathVariable("cityA") String cityA, @PathVariable("cityB") String cityB) throws Exception {

        /*
         * the function specification states:
         * "TODO: given two city names,
         *   compare the length of the daylight hours and return the city with the longest day".
         */

        if (cityA == null || cityA.isEmpty()) throw new NullPointerException("cityA is Null or Empty");
        if (cityB == null || cityB.isEmpty()) throw new NullPointerException("cityB is Null or Empty");

        CityInfo cityInfoA = weatherService.forecastByCity(cityA);
        CityInfo cityInfoB = weatherService.forecastByCity(cityB);

        if (cityInfoA == null) throw new NullPointerException("cityInfoA is Null, failed to find city");
        if (cityInfoB == null) throw new NullPointerException("cityInfoB is Null, failed to find city");

        // Get the daylight hours for each city, given current conditions
        long cityADaylightHours = getDaylightHours(cityInfoA);
        long cityBDaylightHours = getDaylightHours(cityInfoB);

        // Select which city has the longest day
        CityInfo longestDayCity = (cityADaylightHours >= cityBDaylightHours) ? cityInfoA : cityInfoB;

        return ResponseEntity.ok(longestDayCity);


    }

    @GetMapping("/compare/rain/{cityA}/{cityB}")
    public ResponseEntity<List<CityInfo>> compareRain(
            @PathVariable("cityA") String cityA, @PathVariable("cityB") String cityB) throws Exception {

        /*
         * the function specification states that "TODO: given two city names, check which city it is currently raining in".
         * The usage of the singular ("which city", and not "which cities") seems to imply that it is raining in
         * only one of the two cities passed as arguments.
         * However, from a physical perspective, we wouldn't rule out that it could rain in both at the same time,
         * or in neither of them. Hence the function should return 0 to 2 city descriptors.
         * I feel returning a list of city descriptors containing the cities where it is raining is an adequate target
         * for this function. Otherwise, the modifications needed for another target would be simple.
         */

        if (cityA == null || cityA.isEmpty()) throw new NullPointerException("cityA is Null or Empty");
        if (cityB == null || cityB.isEmpty()) throw new NullPointerException("cityB is Null or Empty");

        CityInfo cityInfoA = weatherService.forecastByCity(cityA);
        CityInfo cityInfoB = weatherService.forecastByCity(cityB);


        return compareRain(cityInfoA, cityInfoB);
    }

    public ResponseEntity<List<CityInfo>> compareRain(CityInfo cityInfoA, CityInfo cityInfoB) {
        // Function Separated for ease of testing

        // Null checks
        if (cityInfoA == null) throw new NullPointerException("cityInfoA is Null");
        if (cityInfoB == null) throw new NullPointerException("cityInfoB is Null");

        List<CityInfo> rainingCities = new ArrayList<>();

        /*
         * To check if it is currently raining, we look at the current conditions in the "conditions" field,
         * and check if it contains "rain".
         * Another possible method would be of checking the precipitation, but it seems to already have been done
         * within the "conditions".
         * Currently, snow will not be detected as rain, as they are separate conditions.
         */
        if (cityInfoA.getConditions().toLowerCase().contains("rain")) rainingCities.add(cityInfoA);
        if (cityInfoB.getConditions().toLowerCase().contains("rain")) rainingCities.add(cityInfoB);

        return ResponseEntity.ok(rainingCities);
    }


}
