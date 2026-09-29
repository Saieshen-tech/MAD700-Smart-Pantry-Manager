package com.sai.smartpantry;

public class Ingredient {
    private long id;
    private String name;
    private float quantity;
    private String unit;
    private String expiry;

    public Ingredient(long id, String name, float quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public float getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiry() { return expiry; }
}