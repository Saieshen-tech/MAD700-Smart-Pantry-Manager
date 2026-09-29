package com.sai.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<Ingredient> ingredientList;
    private final Context context;
    private final OnRefreshListener listener;

    public interface OnRefreshListener {
        void onRefresh();
    }

    public PantryAdapter(List<Ingredient> list, Context context, OnRefreshListener listener) {
        this.ingredientList = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        Ingredient current = ingredientList.get(position);
        holder.tvName.setText(current.getName());

        String details = formatQty(current.getQuantity()) + " " + current.getUnit();
        if (current.getExpiry() != null && !current.getExpiry().isEmpty()) {
            details += "  •  Expires " + current.getExpiry();
        }
        holder.tvDetails.setText(details);

        // Tap a row -> open the Edit screen, passing this ingredient's id through the Intent
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddIngredientActivity.class);
            intent.putExtra(AddIngredientActivity.EXTRA_INGREDIENT_ID, current.getId());
            context.startActivity(intent);
        });

        // Tap the bin icon -> delete from the database, then reload the list
        holder.btnDelete.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(context);
            db.deleteIngredient(current.getId());
            Toast.makeText(context, current.getName() + " removed", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onRefresh(); // reloads the list from the database
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    // Shows 200 instead of 200.0, but keeps 1.5 as 1.5
    static String formatQty(float qty) {
        if (qty == (long) qty) return String.valueOf((long) qty);
        return String.valueOf(qty);
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;
        ImageButton btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}