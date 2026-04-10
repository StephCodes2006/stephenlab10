//Stephen Akanniolu n01725208
package com.stephenakanniolu.lab1011;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

            if (itemId == R.id.navigation_stephen) {
                selectedFragment = new StephenAkannioluFragment();
            } else if (itemId == R.id.navigation_akanniolu) {
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
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new StephenAkannioluFragment())
                    .commit();
            navView.setSelectedItemId(R.id.navigation_stephen);
        }
    }

    // --- NEW CONTACTS STUFF START ---

    // Requirement 21: Menu item on ActionBar (Toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ste_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Requirement 21: Launch device contacts when clicked
        if (item.getItemId() == R.id.steMenuContacts) {
            steLaunchContactsAndToastCount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void steLaunchContactsAndToastCount() {
        // Launch the device contacts activity
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        startActivity(intent);

        // Requirement 22: Count contacts programmatically
        int contactCount = 0;
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null) {
            contactCount = cur.getCount();
            cur.close();
        }

        // Requirement 22: Display count in a long toast
        // Ensure you have "ste_contact_count" in your strings.xml to avoid hardcoding
        String message = getString(R.string.ste_contact_count_prefix) + " " + contactCount;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // --- NEW CONTACTS STUFF END ---
}