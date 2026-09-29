package com.sai.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PantryManager";

    // Tables
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Columns (ingredients)
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_UNIT = "unit";
    public static final String KEY_EXPIRY = "expiry_date";

    // Columns (recipes)
    public static final String KEY_TITLE = "title";
    public static final String KEY_INSTRUCTIONS = "instructions";

    // Columns (recipe_ingredients)
    // Recipes link to an ingredient by NAME, not by pantry row id,
    // so a recipe still works after the user deletes and re-adds "Flour".
    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_INGREDIENT_NAME = "ingredient_name";
    public static final String KEY_REQUIRED_QTY = "required_qty";

    // Alias used by the recipe-detail query (how much the pantry has)
    public static final String KEY_HAVE_QTY = "have_qty";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INGREDIENTS = "CREATE TABLE " + TABLE_INGREDIENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_QUANTITY + " REAL NOT NULL,"
                + KEY_UNIT + " TEXT NOT NULL,"
                + KEY_EXPIRY + " TEXT" + ")";

        String CREATE_RECIPES = "CREATE TABLE " + TABLE_RECIPES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT NOT NULL,"
                + KEY_INSTRUCTIONS + " TEXT" + ")";

        String CREATE_LINK = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + "("
                + KEY_RECIPE_ID + " INTEGER NOT NULL,"
                + KEY_INGREDIENT_NAME + " TEXT NOT NULL,"
                + KEY_REQUIRED_QTY + " REAL NOT NULL,"
                + KEY_UNIT + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + KEY_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + KEY_ID + ")" + ")";

        db.execSQL(CREATE_INGREDIENTS);
        db.execSQL(CREATE_RECIPES);
        db.execSQL(CREATE_LINK);

        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // --- Recipes ---
        long pancakes = insertRecipe(db, "Classic Pancakes",
                "1. Mix dry ingredients.\n2. Add wet ingredients.\n3. Cook on a hot griddle.");
        linkRecipeIngredient(db, pancakes, "Flour", 200f, "g");
        linkRecipeIngredient(db, pancakes, "Eggs", 2f, "pcs");
        linkRecipeIngredient(db, pancakes, "Milk", 300f, "ml");

        long oatmeal = insertRecipe(db, "Simple Oatmeal",
                "1. Boil water.\n2. Add oats.\n3. Simmer for 5 mins.\n4. Add toppings.");
        linkRecipeIngredient(db, oatmeal, "Oats", 100f, "g");
        linkRecipeIngredient(db, oatmeal, "Sugar", 10f, "g");

        // --- Starter pantry stock (so the demo shows strict matching) ---
        // Oatmeal is suggested straight away. Pancakes only appear once
        // the user adds at least 200 g of Flour.
        insertIngredient(db, "Oats", 500f, "g", "");
        insertIngredient(db, "Sugar", 1000f, "g", "");
        insertIngredient(db, "Eggs", 6f, "pcs", "");
        insertIngredient(db, "Milk", 1000f, "ml", "");
    }

    private long insertRecipe(SQLiteDatabase db, String title, String instructions) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_INSTRUCTIONS, instructions);
        return db.insert(TABLE_RECIPES, null, values);
    }

    private void linkRecipeIngredient(SQLiteDatabase db, long recipeId, String ingredientName, float qty, String unit) {
        ContentValues values = new ContentValues();
        values.put(KEY_RECIPE_ID, recipeId);
        values.put(KEY_INGREDIENT_NAME, ingredientName);
        values.put(KEY_REQUIRED_QTY, qty);
        values.put(KEY_UNIT, unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }

    private long insertIngredient(SQLiteDatabase db, String name, float qty, String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_QUANTITY, qty);
        values.put(KEY_UNIT, unit);
        values.put(KEY_EXPIRY, expiry);
        return db.insert(TABLE_INGREDIENTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // --- CRUD Methods ---

    public long addIngredient(String name, float qty, String unit, String expiry) {
        return insertIngredient(this.getWritableDatabase(), name, qty, unit, expiry);
    }

    public Cursor getAllIngredients() {
        return this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_INGREDIENTS + " ORDER BY " + KEY_NAME + " COLLATE NOCASE", null);
    }

    public Cursor getAllRecipes() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
    }

    public Cursor getRecipe(long recipeId) {
        return this.getReadableDatabase().rawQuery(
                "SELECT " + KEY_TITLE + ", " + KEY_INSTRUCTIONS
                        + " FROM " + TABLE_RECIPES
                        + " WHERE " + KEY_ID + " = ?",
                new String[]{String.valueOf(recipeId)});
    }

    // Each ingredient a recipe needs, plus how much of it the pantry has
    public Cursor getIngredientsForRecipe(long recipeId) {
        String query = "SELECT ri." + KEY_INGREDIENT_NAME + ", ri." + KEY_REQUIRED_QTY + ", ri." + KEY_UNIT + ", "
                + "COALESCE((SELECT SUM(i." + KEY_QUANTITY + ") FROM " + TABLE_INGREDIENTS + " i "
                + "WHERE i." + KEY_NAME + " = ri." + KEY_INGREDIENT_NAME + " COLLATE NOCASE), 0) AS " + KEY_HAVE_QTY + " "
                + "FROM " + TABLE_RECIPE_INGREDIENTS + " ri "
                + "WHERE ri." + KEY_RECIPE_ID + " = ?";
        return this.getReadableDatabase().rawQuery(query, new String[]{String.valueOf(recipeId)});
    }

    public void deleteIngredient(long id) {
        this.getWritableDatabase().delete(TABLE_INGREDIENTS, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Strict Matching Logic:
    // A recipe is suggested only if NONE of its required ingredients is missing or short.
    // Pantry entries with the same name (e.g. two "Flour" rows) are added together.
    public Cursor getSuggestedRecipes() {
        String query = "SELECT r." + KEY_ID + ", r." + KEY_TITLE + " FROM " + TABLE_RECIPES + " r "
                + "WHERE NOT EXISTS ("
                + "SELECT 1 FROM " + TABLE_RECIPE_INGREDIENTS + " ri "
                + "WHERE ri." + KEY_RECIPE_ID + " = r." + KEY_ID + " "
                + "AND COALESCE((SELECT SUM(i." + KEY_QUANTITY + ") FROM " + TABLE_INGREDIENTS + " i "
                + "WHERE i." + KEY_NAME + " = ri." + KEY_INGREDIENT_NAME + " COLLATE NOCASE), 0) < ri." + KEY_REQUIRED_QTY
                + ") "
                + "ORDER BY r." + KEY_TITLE;
        return this.getReadableDatabase().rawQuery(query, null);
    }
}