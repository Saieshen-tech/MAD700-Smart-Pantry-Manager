package com.sai.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Settings screen. The user's choice is saved with SharedPreferences
 * (a small key-value store on the device), so it is remembered after the app closes.
 */
public class SettingsActivity extends AppCompatActivity {

    // Shared names so other classes (e.g. PantryAdapter) read the same setting
    public static final String PREFS_NAME = "smart_pantry_settings";
    public static final String KEY_HIGHLIGHT_EXPIRING = "highlight_expiring";
    public static final int EXPIRY_WARNING_DAYS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show the back arrow
        }
        setTitle("Settings");

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SwitchCompat switchExpiring = findViewById(R.id.switchExpiring);

        // Show the saved value (on by default)
        switchExpiring.setChecked(prefs.getBoolean(KEY_HIGHLIGHT_EXPIRING, true));

        // Save immediately whenever the user flips the switch
        switchExpiring.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean(KEY_HIGHLIGHT_EXPIRING, isChecked).apply());
    }

    // Back arrow in the toolbar closes this screen
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}