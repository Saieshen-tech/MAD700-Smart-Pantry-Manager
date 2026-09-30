package com.sai.smartpantry;

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

/**
 * "Suggested Recipes" tab: lists ONLY the recipes the user can make
 * with what is in the pantry right now (strict matching).
 */
public class RecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvTitle, tvEmpty;
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
        tvTitle = view.findViewById(R.id.tvTitle);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recipeList = new ArrayList<>();
        db = new DatabaseHelper(requireContext());

        adapter = new RecipeAdapter(recipeList, requireContext());
        recyclerView.setAdapter(adapter);
    }

    // Lifecycle: onResume runs every time this tab comes back into view,
    // so the suggestions are re-checked after any pantry change.
    @Override
    public void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        recipeList.clear();
        recipeList.addAll(db.getSuggestedRecipes()); // strict matching happens here
        adapter.notifyDataSetChanged();

        tvTitle.setText("Suggested Recipes (" + recipeList.size() + ")");
        // Friendly message instead of a blank screen when nothing matches
        tvEmpty.setVisibility(recipeList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}