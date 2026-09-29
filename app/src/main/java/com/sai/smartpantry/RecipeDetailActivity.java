package com.sai.smartpantry;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

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

        Cursor ingredientsCursor = db.getIngredientsForRecipe(recipeId);
        StringBuilder sb = new StringBuilder();
        if (ingredientsCursor.moveToFirst()) {
            int nameCol = ingredientsCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_INGREDIENT_NAME);
            int reqCol = ingredientsCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_REQUIRED_QTY);
            int unitCol = ingredientsCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_UNIT);
            int haveCol = ingredientsCursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HAVE_QTY);
            do {
                String name = ingredientsCursor.getString(nameCol);
                float required = ingredientsCursor.getFloat(reqCol);
                String unit = ingredientsCursor.getString(unitCol);
                float have = ingredientsCursor.getFloat(haveCol);

                sb.append("• ").append(name).append(": ")
                        .append(PantryAdapter.formatQty(required)).append(" ").append(unit)
                        .append("  (you have ").append(PantryAdapter.formatQty(have)).append(")")
                        .append("\n");
            } while (ingredientsCursor.moveToNext());
        }
        ingredientsCursor.close();
        tvIngredients.setText(sb.toString().trim());
    }

    // Back arrow in the toolbar closes this screen
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}