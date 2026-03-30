package com.stephenakanniolu.lab1011;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StephenFragment extends Fragment {

    private Spinner spinner;
    private Button btnDownload;
    private ProgressBar progressBar;
    private ImageView imageView;

    private final String[] imageNames = {"Ocean", "Forest", "City", "Desert", "Space"};
    private final String[] imageUrls = {
            "https://upload.wikimedia.org/wikipedia/commons/e/e0/Clouds_over_the_Atlantic_Ocean.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/b/b5/Bieler_B%C3%BCrgerwald_Winter.JPG",
            "https://upload.wikimedia.org/wikipedia/commons/4/47/New_york_times_square-terabass.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/5/58/Sahara_Desert_Algeria.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/c/c3/NGC_4414_%28NASA-Hubble%29.jpg"
    };

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stephen_akanniolu, container, false);

        spinner = view.findViewById(R.id.spinnerStephen);
        btnDownload = view.findViewById(R.id.buttonStephen);
        progressBar = view.findViewById(R.id.progressBarStephen);
        imageView = view.findViewById(R.id.imageViewStephen);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, imageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnDownload.setOnClickListener(v -> downloadImage());

        return view;
    }

    private void downloadImage() {
        int selectedPosition = spinner.getSelectedItemPosition();
        String urlString = imageUrls[selectedPosition];

        // Hide current image and show progress bar
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        btnDownload.setEnabled(false);

        executor.execute(() -> {
            Bitmap bitmap = null;
            String errorMessage = null;
            long startTime = System.currentTimeMillis();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }

            // Requirement: progress bar displayed for at least 5 seconds
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime < 5000) {
                try {
                    Thread.sleep(5000 - elapsedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            final Bitmap finalBitmap = bitmap;
            final String finalError = errorMessage;

            handler.post(() -> {
                progressBar.setVisibility(View.GONE);
                btnDownload.setEnabled(true);

                if (finalBitmap != null) {
                    imageView.setImageBitmap(finalBitmap);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    // Hide image and show error toast
                    imageView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + finalError, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
