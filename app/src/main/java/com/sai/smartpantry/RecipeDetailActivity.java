package com.sai.smartpantry;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

/**
 * Shows one recipe: its ingredients (with how much the pantry has) and its method.
 * The recipe id arrives through the Intent from RecipeAdapter.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvIngredients, tvInstructions;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Toolbar with a back arrow
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Recipe");

        tvTitle = findViewById(R.id.tvRecipeTitle);
        tvIngredients = findViewById(R.id.tvIngredientsList);
        tvInstructions = findViewById(R.id.tvInstructions);
        db = new DatabaseHelper(this);

        long recipeId = getIntent().getLongExtra("RECIPE_ID", -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        Cursor recipeCursor = db.getRecipe(recipeId);
        if (recipeCursor.moveToFirst()) {
            tvTitle.setText(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TITLE)));
            tvInstructions.setText(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_INSTRUCTIONS)));
        }
        recipeCursor.close();

        // Each line shows what the recipe needs and how much the pantry has,
        // already converted to the recipe's unit (e.g. pantry "1 l" milk shows as 1000 ml)
        List<RecipeIngredient> lines = db.getIngredientsForRecipe(recipeId);
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient line : lines) {
            sb.append(line.isSatisfied() ? "✓ " : "✗ ")
                    .append(line.getName()).append(": ")
                    .append(PantryAdapter.formatQty(line.getRequiredQty())).append(" ").append(line.getUnit())
                    .append("  (you have ").append(PantryAdapter.formatQty(line.getHaveQty()))
                    .append(" ").append(line.getUnit()).append(")")
                    .append("\n");
        }
        tvIngredients.setText(sb.toString().trim());
    }

    // Back arrow in the toolbar closes this screen
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}