# Advanced Weather App

The **Advanced Weather App** is a JavaFX application that allows users to retrieve weather information and forecasts for specific locations. It utilizes the WeatherAPI to fetch weather data, providing real-time weather updates and 3-day forecasts. 

## Features

- **Location Search**: Users can enter the name of a city or location to obtain current weather and a 3-day weather forecast.
- **Autocomplete Suggestions**: As you type the city's name, the application provides autocomplete suggestions, making location entry easier.
- **Display Weather Data**: The app displays essential weather information, including temperature, humidity, wind speed, and weather condition.
- **Weather Icons**: Users can view weather conditions using icons for a more visual representation.
- **3-Day Weather Forecast**: Access a 3-day weather forecast for the selected location.

## Prerequisites

- **Java Development Kit (JDK)**: Ensure that you have the Java Development Kit (JDK) installed on your system to run this application.
- **JavaFX SDK**: Make sure to add the JavaFX SDK to your project for JavaFX application development.

## Getting Started

1. **Launch the Application**: Start the application.
2. **Enter Location**: Input the name of the city or location you want to get weather data for.
3. **Autocomplete Suggestions**: As you type the city name, you will receive autocomplete suggestions to assist with location entry.
4. **Retrieve Weather Data**: Click the "Get Weather" button to initiate the data retrieval process.
5. **View Weather Information**: The app will display the current weather data for the specified location.
6. **3-Day Forecast**: Additionally, you can access a 3-day weather forecast for the selected city.
7. **Weather Information**: Details such as temperature, humidity, wind speed, and weather conditions will be shown.

## Configuration

- The application utilizes the WeatherAPI to retrieve weather data. You must have an API key to use the application. Replace the placeholders for API keys in the code with your own keys.

```java
private static final String API_KEY = "Your_WeatherAPI_Key";
private static final String AUTOCOMPLETE_API_KEY = "Your_AutocompleteAPI_Key";

##Additional Information
Autocomplete Suggestions: Suggestions are updated every 2 seconds to provide the latest city suggestions.
Data Fetching: If the app cannot retrieve data, it will display an error message.
3-Day Forecast: The forecast provides weather information for the next 3 days.
This app is created using Java and JavaFX, making it a versatile and reliable solution for obtaining weather information.
Feedback
We appreciate your feedback and suggestions for improving the app's functionality and user experience. Feel free to reach out with your comments and ideas.
