package com.sai.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);
        setTitle("Add Ingredient");

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        db = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

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

        db.addIngredient(name, qty, unit, expiry);
        Toast.makeText(this, "Ingredient Added", Toast.LENGTH_SHORT).show();
        finish();
    }
}