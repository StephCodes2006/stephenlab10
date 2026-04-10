// Stephen Akanniolu - n01725208
package com.stephenakanniolu.lab1011;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InitialsFragment extends Fragment {

    private FusedLocationProviderClient steLocationClient;
    private int stePermissionDeniedCount = 0;
    private static final String STE_CHANNEL_ID = "stephen_notification_channel";

    // Timer/Clock variables
    private TextView steTxtTimer;
    private final Handler steHandler = new Handler(Looper.getMainLooper());
    private Runnable steRunnable;

    // Requirement 52: Notification permission for Android 13+ (API 33)
    private final ActivityResultLauncher<String> steNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    // Requirement 44: Location permission launcher
    private final ActivityResultLauncher<String> steRequestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    steGetLastLocation();
                } else {
                    stePermissionDeniedCount++;
                    if (stePermissionDeniedCount >= 2) {
                        steShowSnackbar("Please enable location in Settings", true);
                    } else {
                        steShowSnackbar("Permission Denied - Stephen Akanniolu", false);
                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initials, container, false);

        // --- AdMob Initialization (Requirement 54) ---
        MobileAds.initialize(requireContext(), status -> {});
        AdView steAdView = view.findViewById(R.id.steAdView);
        if (steAdView != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            steAdView.loadAd(adRequest);
        }

        // --- Clock Timer Initialization ---
        steTxtTimer = view.findViewById(R.id.steTxtTimer);
        steStartClock();

        steLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        steCreateNotificationChannel();

        // Check for Notification Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            steNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Requirement 41: Handle button click (Fixed ID reference)
        view.findViewById(R.id.steBtnLocation).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                steGetLastLocation();
            } else {
                steRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });

        return view;
    }

    private void steStartClock() {
        steRunnable = new Runnable() {
            @Override
            public void run() {
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                if (steTxtTimer != null) steTxtTimer.setText(currentTime);
                steHandler.postDelayed(this, 1000);
            }
        };
        steHandler.post(steRunnable);
    }

    @SuppressWarnings("MissingPermission")
    private void steGetLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            steLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    String coords = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                    // Requirement 41: Indefinite Snackbar
                    steShowSnackbar(coords, false);
                    // Requirement 51: Notification
                    steSendNotification("Location Determined: " + coords);
                } else {
                    steShowSnackbar("Error: Location not determined", false);
                }
            });
        }
    }

    private void steSendNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), STE_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("Stephen Akanniolu - Location")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true);

        // --- FIXED: Context.NOTIFICATION_SERVICE to avoid red error ---
        NotificationManager manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1725208, builder.build());
        }
    }

    private void steCreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    STE_CHANNEL_ID, "Stephen Lab Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void steShowSnackbar(String message, boolean openSettings) {
        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE);
        if (openSettings) {
            snackbar.setAction("SETTINGS", v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            });
        } else {
            snackbar.setAction("DISMISS", v -> snackbar.dismiss());
        }
        snackbar.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up the handler to prevent memory leaks
        if (steHandler != null && steRunnable != null) {
            steHandler.removeCallbacks(steRunnable);
        }
    }
}