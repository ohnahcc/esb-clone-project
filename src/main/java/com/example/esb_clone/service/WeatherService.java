package com.example.esb_clone.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1️⃣ 도시명으로 위도(lat), 경도(lon) 가져오기
    private Map<String, Object> getCityCoordinates(String city) {
        String geoUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + apiKey;
        List<Map<String, Object>> response = restTemplate.getForObject(geoUrl, List.class);

        if (response == null || response.isEmpty()) {
            throw new RuntimeException("해당 도시의 위치 정보를 찾을 수 없습니다.");
        }

        return response.get(0);
    }

    // 2️⃣ 가져온 위도/경도를 사용하여 날씨 정보 가져오기
    public Map<String, Object> getWeather(String city) {
        Map<String, Object> location = getCityCoordinates(city);
        double lat = (double) location.get("lat");
        double lon = (double) location.get("lon");

        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        return restTemplate.getForObject(weatherUrl, Map.class);
    }
}

