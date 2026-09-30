package com.sai.smartpantry;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The strict-matching logic (brief section 2.3).
 *
 * A recipe is only suggested if EVERY ingredient it needs is in the pantry
 * in AT LEAST the required amount. To stop trivial differences breaking the match:
 *  - names are normalised: "Tomatoes", "tomato " and "TOMATO" all become "tomato"
 *  - units are converted to a base unit: kg -> g, l -> ml, "pieces" -> pcs
 *  - several pantry rows with the same name are added together
 */
public final class IngredientMatcher {

    private IngredientMatcher() { } // only static helper methods

    /** Lower-case, trim, and turn simple plurals into singular. */
    public static String normalizeName(String name) {
        if (name == null) return "";
        String n = name.trim().toLowerCase(Locale.US).replaceAll("\\s+", " ");

        if (n.length() > 4 && n.endsWith("ies")) {                 // berries -> berry
            n = n.substring(0, n.length() - 3) + "y";
        } else if (n.endsWith("oes") || n.endsWith("ches") || n.endsWith("shes")
                || n.endsWith("sses") || n.endsWith("xes")) {       // tomatoes -> tomato
            n = n.substring(0, n.length() - 2);
        } else if (n.length() > 3 && n.endsWith("s") && !n.endsWith("ss")) { // eggs -> egg
            n = n.substring(0, n.length() - 1);
        }
        return n;
    }

    /** The base unit a unit converts to: "g", "ml" or "pcs" (other units are kept as typed). */
    public static String baseUnit(String unit) {
        String u = cleanUnit(unit);
        switch (u) {
            case "g": case "gram": case "grams": case "gr":
            case "kg": case "kgs": case "kilogram": case "kilograms":
                return "g";
            case "ml": case "millilitre": case "millilitres": case "milliliter": case "milliliters":
            case "l": case "litre": case "litres": case "liter": case "liters":
                return "ml";
            case "": case "pc": case "pcs": case "piece": case "pieces": case "each": case "x":
                return "pcs";
            default:
                return u;
        }
    }

    /** How many base units one of this unit is worth, e.g. kg = 1000 (g), l = 1000 (ml). */
    public static double factor(String unit) {
        String u = cleanUnit(unit);
        switch (u) {
            case "kg": case "kgs": case "kilogram": case "kilograms":
            case "l": case "litre": case "litres": case "liter": case "liters":
                return 1000.0;
            default:
                return 1.0;
        }
    }

    private static String cleanUnit(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase(Locale.US).replace(".", "");
    }

    // Key used to group amounts: same ingredient AND same kind of unit
    private static String key(String name, String unit) {
        return normalizeName(name) + "|" + baseUnit(unit);
    }

    /** Adds up everything in the pantry, per ingredient, in base units. */
    public static Map<String, Double> buildPantryTotals(List<Ingredient> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (Ingredient item : pantry) {
            String k = key(item.getName(), item.getUnit());
            double amount = item.getQuantity() * factor(item.getUnit());
            Double existing = totals.get(k);
            totals.put(k, (existing == null ? 0 : existing) + amount);
        }
        return totals;
    }

    /** How much of this ingredient the pantry has, expressed in the recipe line's own unit. */
    public static float available(Map<String, Double> pantryTotals, String name, String unit) {
        Double base = pantryTotals.get(key(name, unit));
        if (base == null) return 0f; // missing, or only stored in an incompatible unit
        return (float) (base / factor(unit));
    }

    /**
     * THE STRICT RULE: fills in haveQty for every line, then returns true
     * only if every single line is satisfied. One missing or short ingredient = false.
     */
    public static boolean canMake(List<RecipeIngredient> required, Map<String, Double> pantryTotals) {
        if (required.isEmpty()) return false;
        boolean all = true;
        for (RecipeIngredient line : required) {
            line.setHaveQty(available(pantryTotals, line.getName(), line.getUnit()));
            if (!line.isSatisfied()) all = false;
        }
        return all;
    }
}