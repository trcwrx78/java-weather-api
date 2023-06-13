package com.weatherexample.demo;

import com.fasterxml.jackson.databind.JsonNode;

public class WeatherData {
  
  private String city;
  private String state;
  private String updated;
  private JsonNode forecast;

  public WeatherData(String city, String state, String updated, JsonNode forecast) {
    this.city = city;
    this.state = state;
    this.updated = updated;
    this.forecast = forecast;
  } 

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getUpdated() {
    return updated;
  }

  public JsonNode getForecast() {
    return forecast;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setUpdated(String updated) {
    this.updated = updated;
  }

  public void setForecast(JsonNode forecast) {
    this.forecast = forecast;
  }
  
}
