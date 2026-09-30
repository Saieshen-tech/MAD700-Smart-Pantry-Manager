package com.sai.smartpantry;

/**
 * One ingredient line of a recipe, e.g. "Flour, 200 g".
 * haveQty is filled in when we compare against the pantry
 * (how much the user has, converted to this line's unit).
 */
public class RecipeIngredient {
    private final String name;
    private final float requiredQty;
    private final String unit;
    private float haveQty;

    public RecipeIngredient(String name, float requiredQty, String unit) {
        this.name = name;
        this.requiredQty = requiredQty;
        this.unit = unit;
    }

    public String getName() { return name; }
    public float getRequiredQty() { return requiredQty; }
    public String getUnit() { return unit; }
    public float getHaveQty() { return haveQty; }
    public void setHaveQty(float haveQty) { this.haveQty = haveQty; }

    // True if the pantry has at least the amount this line needs
    public boolean isSatisfied() { return haveQty >= requiredQty; }
}