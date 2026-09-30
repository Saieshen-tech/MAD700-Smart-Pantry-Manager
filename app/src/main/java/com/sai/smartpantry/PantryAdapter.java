package com.sai.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Custom RecyclerView Adapter: turns each Ingredient in the list
 * into one row on screen (layout: item_ingredient.xml).
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private static final int COLOR_NORMAL = Color.parseColor("#666666");
    private static final int COLOR_WARNING = Color.parseColor("#C62828");

    private final List<Ingredient> ingredientList;
    private final Context context;
    private final OnRefreshListener listener;

    // Lets the adapter ask the Fragment to reload the list after a delete
    public interface OnRefreshListener {
        void onRefresh();
    }

    public PantryAdapter(List<Ingredient> list, Context context, OnRefreshListener listener) {
        this.ingredientList = list;
        this.context = context;
        this.listener = listener;
    }

    // Creates a new empty row view
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new PantryViewHolder(view);
    }

    // Fills a row with the data of the ingredient at this position
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        Ingredient current = ingredientList.get(position);
        holder.tvName.setText(current.getName());

        String details = formatQty(current.getQuantity()) + " " + current.getUnit();
        boolean warn = false;

        if (current.getExpiry() != null && !current.getExpiry().isEmpty()) {
            details += "  •  Expires " + current.getExpiry();

            // Expiring-soon highlight (can be switched off in Settings)
            SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
            boolean highlightOn = prefs.getBoolean(SettingsActivity.KEY_HIGHLIGHT_EXPIRING, true);
            Long days = daysUntil(current.getExpiry());

            if (highlightOn && days != null && days <= SettingsActivity.EXPIRY_WARNING_DAYS) {
                details += (days < 0) ? "  •  EXPIRED" : "  •  Expires soon";
                warn = true;
            }
        }
        holder.tvDetails.setText(details);
        holder.tvDetails.setTextColor(warn ? COLOR_WARNING : COLOR_NORMAL);

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
            if (listener != null) listener.onRefresh();
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

    /**
     * Days from today until the given YYYY-MM-DD date.
     * Negative = already expired. Returns null if the date is empty or not a real date.
     */
    static Long daysUntil(String expiry) {
        if (expiry == null || expiry.isEmpty()) return null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            fmt.setLenient(false); // rejects impossible dates like 2026-02-31
            Date expiryDate = fmt.parse(expiry);
            Date today = fmt.parse(fmt.format(new Date())); // today at midnight
            return Math.round((expiryDate.getTime() - today.getTime()) / 86400000.0);
        } catch (ParseException e) {
            return null;
        }
    }

    // Holds the views of one row so they don't have to be looked up every time
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