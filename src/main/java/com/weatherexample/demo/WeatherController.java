package com.weatherexample.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
public class WeatherController {

  private WebClient webClient = WebClient.builder().baseUrl("https://api.weather.gov").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
  
  @GetMapping("/weather")
  public Mono<WeatherData> getWeather(@RequestParam("lat") String latitude, @RequestParam("lng") String longitude) {
    // Fetch live weather data from national weather service


    return webClient.get()
      .uri("/points/{lat},{lng}", latitude, longitude)
      .retrieve()
      .bodyToMono(String.class)
      .map(this::parseWeatherData)
      .flatMap(this::getForecast);
  }

  private Map<String, String> parseWeatherData(String jsonData) {
  ObjectMapper mapper = new ObjectMapper();

  try {
    JsonNode root = mapper.readTree(jsonData);
    JsonNode forecastNode = root.path("properties").path("forecast");
    JsonNode cityNode = root.path("properties").path("relativeLocation").path("properties").path("city");
    JsonNode stateNode = root.path("properties").path("relativeLocation").path("properties").path("state");

    if (forecastNode.isMissingNode()) {
      throw new RuntimeException("Forecast not found in JSON data");
    }

    Map<String, String> resultMap = new HashMap<>();
    resultMap.put("forecastUrl", forecastNode.asText());
    resultMap.put("city", cityNode.asText());
    resultMap.put("state", stateNode.asText());

    return resultMap;

  } catch (IOException e) {
    throw new RuntimeException("Error parsing JSON data", e);
  }
}

  private Mono<WeatherData> getForecast(Map<String, String> weatherData) {
    String forecastUrl = weatherData.get("forecastUrl");

    return webClient.get()
      .uri(forecastUrl)
      .retrieve()
      .bodyToMono(String.class)
      .map(this::parseForecastData)
      .map(forecast -> new WeatherData(weatherData.get("city"), weatherData.get("state"), forecast.get("updated").asText(), forecast.get("forecast")));
  }

  private Map<String, JsonNode> parseForecastData(String jsonData) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      JsonNode root = mapper.readTree(jsonData);
      JsonNode updatedNode = root.path("properties").path("updated");
      JsonNode periodsNode = root.path("properties").path("periods");

      Map<String, JsonNode> resultMap = new HashMap<>();
      resultMap.put("updated", updatedNode);
      resultMap.put("forecast", periodsNode);

      return resultMap;

    } catch (IOException e) {
      throw new RuntimeException("Error parsing JSON data", e);
    }
  }

}
