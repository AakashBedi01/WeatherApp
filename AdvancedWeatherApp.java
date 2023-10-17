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
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String AUTOCOMPLETE_API_KEY = "YOUR_AUTOCOMPLETE_API_KEY";
    private static final String AUTOCOMPLETE_URL = "https://api.weatherapi.com/v1/search.json";
    private static final String WEATHER_URL = "https://api.weatherapi.com/v1/current.json";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Advanced Weather App");

        Label locationLabel = new Label("Enter city:");
        ComboBox<String> locationInput = new ComboBox<>();
        locationInput.setEditable(true);
        locationInput.setPrefWidth(200);
        locationInput.setMaxWidth(Double.MAX_VALUE);
        locationInput.getItems().addAll(getAutoCompleteSuggestions(""));
        Button getWeatherButton = new Button("Get Weather");
        VBox weatherInfoBox = new VBox();
        weatherInfoBox.setAlignment(Pos.CENTER);
        ImageView weatherIcon = new ImageView();
        Label temperatureLabel = new Label();
        Label humidityLabel = new Label();
        Label windLabel = new Label();

        getWeatherButton.setOnAction(e -> {
            String city = locationInput.getEditor().getText();
            WeatherData weatherData = getWeatherData(city);
            if (weatherData != null) {
                temperatureLabel.setText("Temperature: " + weatherData.getTemperature() + "°C");
                humidityLabel.setText("Humidity: " + weatherData.getHumidity() + "%");
                windLabel.setText("Wind Speed: " + weatherData.getWindSpeed() + " km/h");
                weatherIcon.setImage(new Image(weatherData.getWeatherIconURL()));
            } else {
                weatherInfoBox.getChildren().clear();
                weatherInfoBox.getChildren().add(new Label("Could not fetch weather data."));
            }
        });

        // Update autocomplete suggestions every 5 seconds
        Timer autoCompleteTimer = new Timer();
        autoCompleteTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ObservableList<String> suggestions = getAutoCompleteSuggestions(locationInput.getEditor().getText());
                locationInput.setItems(suggestions);
            }
        }, 0, 5000);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(locationLabel, locationInput, getWeatherButton, weatherInfoBox);
        layout.getChildren().addAll(weatherIcon, temperatureLabel, humidityLabel, windLabel);
        layout.setMinWidth(300);
        layout.setMinHeight(300);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private class WeatherData {
        private double temperature;
        private double humidity;
        private double windSpeed;
        private String weatherIcon;

        WeatherData(double temperature, double humidity, double windSpeed, String weatherIcon) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.weatherIcon = weatherIcon;
        }

        double getTemperature() {
            return temperature;
        }

        double getHumidity() {
            return humidity;
        }

        double getWindSpeed() {
            return windSpeed;
        }

        String getWeatherIconURL() {
            return "https:" + weatherIcon;
        }
    }
}
