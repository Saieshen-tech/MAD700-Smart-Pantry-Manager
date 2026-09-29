package com.sai.smartpantry;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * One screen used for BOTH adding and editing an ingredient.
 * - Opened from the + button with no extra  -> Add mode
 * - Opened by tapping a pantry item with its id passed in the Intent -> Edit mode
 */
public class AddIngredientActivity extends AppCompatActivity {

    // Key used to pass the ingredient's id between screens through the Intent
    public static final String EXTRA_INGREDIENT_ID = "INGREDIENT_ID";

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private DatabaseHelper db;

    // -1 means "no id was passed in", so we are adding a new ingredient
    private long ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        db = new DatabaseHelper(this);

        // Read the id sent by PantryAdapter (if any) to decide Add vs Edit mode
        ingredientId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, -1);

        if (ingredientId != -1) {
            setTitle("Edit Ingredient");
            btnSave.setText("Update");
            loadIngredient();
        } else {
            setTitle("Add Ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    // Edit mode: fill the form with the ingredient's current values from the database
    private void loadIngredient() {
        Cursor cursor = db.getIngredient(ingredientId);
        if (cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_NAME)));
            etQuantity.setText(PantryAdapter.formatQty(
                    cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUANTITY))));
            etUnit.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_UNIT)));
            etExpiry.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXPIRY)));
        }
        cursor.close();
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        // --- Input validation ---
        if (name.isEmpty()) {
            etName.setError("Enter a name");
            return;
        }
        if (qtyStr.isEmpty()) {
            etQuantity.setError("Enter a quantity");
            return;
        }

        float qty;
        try {
            qty = Float.parseFloat(qtyStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            return;
        }
        if (qty <= 0) {
            etQuantity.setError("Quantity must be more than 0");
            return;
        }

        if (unit.isEmpty()) unit = "pcs";

        if (!expiry.isEmpty() && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            etExpiry.setError("Use the format YYYY-MM-DD");
            return;
        }

        // --- Save: insert a new row, or update the existing one ---
        if (ingredientId == -1) {
            db.addIngredient(name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updateIngredient(ingredientId, name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish(); // close this screen and go back to the pantry list
    }
}