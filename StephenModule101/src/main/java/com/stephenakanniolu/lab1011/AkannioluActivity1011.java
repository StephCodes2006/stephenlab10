//Stephen Akanniolu n01725208
package com.stephenakanniolu.lab1011;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AkannioluActivity1011 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Initialize the ViewModel
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 2. Install the splash screen BEFORE super.onCreate
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // 3. Set the condition to keep the splash screen visible
        splashScreen.setKeepOnScreenCondition(() -> {
            Boolean ready = viewModel.isReady().getValue();
            return ready == null || !ready;
        });

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Match these IDs exactly with your res/menu/bottom_nav_menu.xml
            if (itemId == R.id.navigation_stephen) {
                selectedFragment = new StephenAkannioluFragment();
            } else if (itemId == R.id.navigation_akanniolu) {
                // FIXED: Using Akanniolu1Fragment to match your weather class name
                selectedFragment = new Akanniolu1Fragment();
            } else if (itemId == R.id.navigation_id) {
                selectedFragment = new N01725208Fragment();
            } else if (itemId == R.id.navigation_initial) {
                selectedFragment = new InitialsFragment();
            } else if (itemId == R.id.navigation_asna) {
                selectedFragment = new AsnaFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                return true; // Click handled successfully
            }
            return false;
        });

        // Set default selection to StephenAkannioluFragment (1st screen from left)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new StephenAkannioluFragment())
                    .commit();
            navView.setSelectedItemId(R.id.navigation_stephen);
        }
    }
}