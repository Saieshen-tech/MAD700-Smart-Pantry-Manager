package com.sai.smartpantry;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recipeList = new ArrayList<>();
        db = new DatabaseHelper(requireContext());

        adapter = new RecipeAdapter(recipeList, requireContext());
        recyclerView.setAdapter(adapter);
    }

    // Re-run the matching every time this tab is shown,
    // so suggestions follow pantry changes.
    @Override
    public void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        Cursor cursor = db.getSuggestedRecipes(); // Strict Matching
        recipeList.clear();
        if (cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID);
            int titleCol = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TITLE);
            do {
                long id = cursor.getLong(idCol);
                String title = cursor.getString(titleCol);
                recipeList.add(new Recipe(id, title));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(recipeList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}