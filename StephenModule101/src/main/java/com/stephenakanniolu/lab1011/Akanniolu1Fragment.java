package com.stephenakanniolu.lab1011;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Akanniolu1Fragment extends Fragment {

    private static final String PREFS_NAME = "WeatherPrefs";
    private static final String KEY_UNIT = "temp_unit";
    private static final String API_KEY = "06b3c302ab9a325c55c20c260ef941d3";

    private TextView tvLon, tvLat, tvCountry, tvHumidity, tvName, tvTemp, tvDescription;
    private RadioGroup rgUnit;
    private RadioButton rbCelsius, rbFahrenheit;
    private Spinner spinnerCity;
    private SharedPreferences sharedPreferences;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // FIX: Pointing to fragment_akanniolu (the weather XML you provided)
        View view = inflater.inflate(R.layout.fragment_akanniolu, container, false);

        // Initializing UI components
        tvLon = view.findViewById(R.id.tv_lon);
        tvLat = view.findViewById(R.id.tv_lat);
        tvCountry = view.findViewById(R.id.tv_country);
        tvHumidity = view.findViewById(R.id.tv_humidity);
        tvName = view.findViewById(R.id.tv_weather_name);
        tvTemp = view.findViewById(R.id.tv_temp);
        tvDescription = view.findViewById(R.id.tv_description);
        rgUnit = view.findViewById(R.id.rg_unit);
        rbCelsius = view.findViewById(R.id.rb_celsius);
        rbFahrenheit = view.findViewById(R.id.rb_fahrenheit);
        spinnerCity = view.findViewById(R.id.spinner_city);

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Retrieve saved temperature unit preference
        String savedUnit = sharedPreferences.getString(KEY_UNIT, "C");
        if ("F".equals(savedUnit)) {
            rbFahrenheit.setChecked(true);
        } else {
            rbCelsius.setChecked(true);
        }

        rgUnit.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (checkedId == R.id.rb_celsius) {
                editor.putString(KEY_UNIT, "C");
            } else {
                editor.putString(KEY_UNIT, "F");
            }
            editor.apply();
            fetchWeather(); // Refresh data when unit changes
        });

        // Setup Spinner with city array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchWeather();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void fetchWeather() {
        if (spinnerCity.getSelectedItem() == null) return;

        String city = spinnerCity.getSelectedItem().toString();
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;

        executor.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    handler.post(() -> updateUI(json));
                } else {
                    showError("Error: " + conn.getResponseMessage());
                }
            } catch (Exception e) {
                showError("Connection failed: " + e.getMessage());
            }
        });
    }

    private void updateUI(JSONObject json) {
        try {
            JSONObject coord = json.getJSONObject("coord");
            tvLon.setText(String.valueOf(coord.getDouble("lon")));
            tvLat.setText(String.valueOf(coord.getDouble("lat")));

            JSONObject sys = json.getJSONObject("sys");
            tvCountry.setText(sys.getString("country"));

            JSONObject main = json.getJSONObject("main");
            tvHumidity.setText("Humidity: " + main.getInt("humidity") + "%");
            tvName.setText(json.getString("name"));

            double kelvin = main.getDouble("temp");
            String unit = sharedPreferences.getString(KEY_UNIT, "C");
            double displayTemp;
            String unitSymbol;

            if ("F".equals(unit)) {
                displayTemp = (kelvin - 273.15) * 9 / 5 + 32;
                unitSymbol = "°F";
            } else {
                displayTemp = kelvin - 273.15;
                unitSymbol = "°C";
            }

            tvTemp.setText(String.format("%.1f %s", displayTemp, unitSymbol));

            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            tvDescription.setText(weather.getString("description"));

        } catch (Exception e) {
            showError("Data Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        handler.post(() -> {
            if (getView() != null) {
                Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", v -> snackbar.dismiss());
                snackbar.show();
            }
        });
    }
}