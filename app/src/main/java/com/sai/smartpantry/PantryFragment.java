package com.sai.smartpantry;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PantryFragment extends Fragment {

    private RecyclerView recyclerView;
    private PantryAdapter adapter;
    private ArrayList<Ingredient> ingredientList;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ingredientList = new ArrayList<>();
        db = new DatabaseHelper(requireContext());

        adapter = new PantryAdapter(ingredientList, requireContext(), this::loadIngredients);
        recyclerView.setAdapter(adapter);
    }

    // Reload every time this tab becomes visible,
    // e.g. after coming back from the "Add Ingredient" screen.
    @Override
    public void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        Cursor cursor = db.getAllIngredients();
        ingredientList.clear();
        if (cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID);
            int nameCol = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_NAME);
            int qtyCol = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUANTITY);
            int unitCol = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_UNIT);
            int expiryCol = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXPIRY);
            do {
                long id = cursor.getLong(idCol);
                String name = cursor.getString(nameCol);
                float qty = cursor.getFloat(qtyCol);
                String unit = cursor.getString(unitCol);
                String expiry = cursor.getString(expiryCol);

                ingredientList.add(new Ingredient(id, name, qty, unit, expiry));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
}