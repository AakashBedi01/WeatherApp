import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class AdvancedWeatherApp extends Application {
     // Your API keys and URLs
    private static final String API_KEY = "86f881fd49404551839215850231710";
    private static final String AUTOCOMPLETE_API_KEY = "86f881fd49404551839215850231710";
    private static final String AUTOCOMPLETE_URL = "https://api.weatherapi.com/v1/search.json";
    private static final String WEATHER_URL = "https://api.weatherapi.com/v1/current.json";
    private static final String FORECAST_URL = "https://api.weatherapi.com/v1/forecast.json";

    public static void main(String[] args) {
        launch(args);
    }

    public static class WeatherData {
        private double temperature;
        private double humidity;
        private double windSpeed;
        private String weatherIcon;
        private String date;

        public WeatherData(double temperature, double humidity, double windSpeed, String weatherIcon) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.weatherIcon = weatherIcon;
        }

        public String toString() {
            return "Date: " + date + " - Temperature: " + temperature + "°C";
        }

        public double getTemperature() {
            return temperature;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public String getWeatherIconURL() {
            return "https:" + weatherIcon;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Advanced Weather App");

        // Create UI elements
        Label locationLabel = new Label("Enter city:");
        ComboBox<String> locationInput = new ComboBox<>();
        locationInput.setEditable(true);
        locationInput.setPrefWidth(200);
        locationInput.setMaxWidth(Double.MAX_VALUE);
        locationInput.setCellFactory(param -> new CityListCell());

        Button getWeatherButton = new Button("Get Weather");
        VBox weatherInfoBox = new VBox();
        weatherInfoBox.setAlignment(Pos.CENTER);
        ImageView weatherIcon = new ImageView();
        Label temperatureLabel = new Label();
        Label humidityLabel = new Label();
        Label windLabel = new Label();
        ListView<String> forecastListView = new ListView<>();
        forecastListView.setPrefWidth(300); 
        VBox forecastBox = new VBox();
        forecastBox.setAlignment(Pos.CENTER);
        forecastBox.setSpacing(10);
        forecastListView.setPrefWidth(300);
        locationInput.setEditable(true);
        locationInput.setPrefWidth(200);
        locationInput.setMaxWidth(Double.MAX_VALUE);
        locationInput.getItems().addAll(getAutoCompleteSuggestions(""));
        weatherInfoBox.setAlignment(Pos.CENTER);
        getWeatherButton.setAlignment(Pos.CENTER);

         // Handle button click
        getWeatherButton.setOnAction(e -> {
            String city = locationInput.getEditor().getText();
            WeatherData weatherData = getWeatherData(city);
            if (weatherData != null) {
                temperatureLabel.setText("Temperature: " + weatherData.getTemperature() + "°C");
                humidityLabel.setText("Humidity: " + weatherData.getHumidity() + "%");
                windLabel.setText("Wind Speed: " + weatherData.getWindSpeed() + " km/h");
                weatherIcon.setImage(new Image(weatherData.getWeatherIconURL()));

                ObservableList<WeatherData> forecast = get3DayForecast(city);

                if (!forecast.isEmpty()) {
                    forecastListView.getItems().clear();
                    for (WeatherData forecastData : forecast) {
                        forecastListView.getItems().add(forecastData.toString());
                    }
                } else {
                    forecastListView.getItems().clear();
                    forecastListView.getItems().add("Could not fetch forecast data.");
                }
            } else {
                weatherInfoBox.getChildren().clear();
                weatherInfoBox.getChildren().add(new Label("Could not fetch weather data."));
            }
        });

        // Update autocomplete suggestions every 2 seconds
        Timer autoCompleteTimer = new Timer();
        autoCompleteTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ObservableList<String> suggestions = getAutoCompleteSuggestions(locationInput.getEditor().getText());
                locationInput.setItems(suggestions);
            }
        }, 0, 2000);

        // Create the main layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(locationLabel, locationInput, getWeatherButton, weatherInfoBox, weatherIcon, temperatureLabel, humidityLabel, windLabel, forecastListView);
        layout.setAlignment(Pos.CENTER);
        layout.setMinWidth(400);
        layout.setMinHeight(400);

        // Create and set the scene
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();}

    private ObservableList<WeatherData> get3DayForecast(String city) {
        // Retrieve a 3-day weather forecast for the specified city
        try {
            String apiUrl = FORECAST_URL + "?key=" + API_KEY + "&q=" + city + "&days=3";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray forecastArray = jsonObject.getJSONObject("forecast").getJSONArray("forecastday");

                ObservableList<WeatherData> forecast = FXCollections.observableArrayList();

                for (int i = 0; i < forecastArray.length(); i++) {
                    JSONObject dayData = forecastArray.getJSONObject(i);
                    JSONObject day = dayData.getJSONObject("day");
                    String date = dayData.getString("date");
                    double temperature = day.getDouble("avgtemp_c");
                    WeatherData forecastData = new WeatherData(temperature, 0.0, 0.0, "");
                    forecastData.setDate(date);
                    forecast.add(forecastData);
                }

                return forecast;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FXCollections.emptyObservableList();
    }

    
    private ObservableList<String> getAutoCompleteSuggestions(String query) {
        try {
            String apiUrl = AUTOCOMPLETE_URL + "?key=" + AUTOCOMPLETE_API_KEY + "&q=" + query;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray suggestionsArray = new JSONArray(response.toString());
                ObservableList<String> suggestions = FXCollections.observableArrayList();

                for (int i = 0; i < suggestionsArray.length(); i++) {
                    JSONObject suggestionObject = suggestionsArray.getJSONObject(i);
                    suggestions.add(suggestionObject.getString("name"));
                }

                return suggestions;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FXCollections.emptyObservableList();
    }
    
    
    
    private WeatherData getWeatherData(String city) {
        try {
            String apiUrl = WEATHER_URL + "?key=" + API_KEY + "&q=" + city;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject current = jsonObject.getJSONObject("current");

                WeatherData weatherData = new WeatherData(
                        current.getDouble("temp_c"),
                        current.getDouble("humidity"),
                        current.getDouble("wind_kph"),
                        current.getJSONObject("condition").getString("icon"));

                return weatherData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class CityListCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item);
            } else {
                setText(null);
            }
        }
    }
}
