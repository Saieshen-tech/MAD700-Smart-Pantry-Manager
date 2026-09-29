package com.sai.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Setup ViewPager with Fragments
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PantryFragment(), "Pantry");
        adapter.addFragment(new RecipesFragment(), "Recipes");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        // Setup FAB
        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });
    }
}