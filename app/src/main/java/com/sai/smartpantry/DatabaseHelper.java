package com.sai.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All database work for the app (SQLite via SQLiteOpenHelper).
 *
 * Tables:
 *  - ingredients         : the user's pantry (id, name, quantity, unit, expiry_date)
 *  - recipes             : the recipe collection (id, title, instructions)
 *  - recipe_ingredients  : which ingredients each recipe needs (recipe_id, ingredient_name, required_qty, unit)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Version 2 = the full 20-recipe collection. Raising the number makes
    // Android call onUpgrade() once on phones that already have version 1.
    private static final int DATABASE_VERSION = 2;
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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Runs once, the first time the app opens the database: create tables + seed data
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

        seedRecipes(db);
        seedPantry(db);
    }

    // Runs when DATABASE_VERSION goes up: rebuild the tables with the new seed data
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // ------------------------------------------------------------------
    // Seed data (pre-loaded on first run)
    // ------------------------------------------------------------------

    private void seedRecipes(SQLiteDatabase db) {
        seedRecipe(db, "Classic Pancakes",
                "1. Whisk the flour and sugar together.\n2. Beat in the eggs and milk until smooth.\n3. Melt a little butter in a pan and cook ladlefuls until golden on both sides.",
                ing("Flour", 200, "g"), ing("Eggs", 2, "pcs"), ing("Milk", 300, "ml"), ing("Sugar", 20, "g"), ing("Butter", 20, "g"));

        seedRecipe(db, "Simple Oatmeal",
                "1. Bring the milk to a gentle boil.\n2. Stir in the oats.\n3. Simmer for 5 minutes, stirring.\n4. Sweeten with sugar and serve.",
                ing("Oats", 100, "g"), ing("Milk", 250, "ml"), ing("Sugar", 10, "g"));

        seedRecipe(db, "Scrambled Eggs",
                "1. Beat the eggs with the milk and salt.\n2. Melt the butter in a pan over low heat.\n3. Stir gently until just set.",
                ing("Eggs", 3, "pcs"), ing("Milk", 30, "ml"), ing("Butter", 10, "g"), ing("Salt", 2, "g"));

        seedRecipe(db, "Cheese Omelette",
                "1. Beat the eggs with the salt.\n2. Melt the butter in a pan and pour in the eggs.\n3. When almost set, sprinkle over the cheese, fold and serve.",
                ing("Eggs", 3, "pcs"), ing("Cheese", 50, "g"), ing("Butter", 10, "g"), ing("Salt", 2, "g"));

        seedRecipe(db, "Tomato & Onion Omelette",
                "1. Chop the tomato and onion and fry in the oil for 3 minutes.\n2. Beat the eggs with the salt and pour over.\n3. Cook until set, fold and serve.",
                ing("Eggs", 3, "pcs"), ing("Tomato", 1, "pcs"), ing("Onion", 1, "pcs"), ing("Cooking Oil", 15, "ml"), ing("Salt", 2, "g"));

        seedRecipe(db, "Cheese Toastie",
                "1. Butter the outside of both slices of bread.\n2. Put the cheese between them.\n3. Toast in a pan until golden and the cheese melts.",
                ing("Bread", 2, "pcs"), ing("Cheese", 40, "g"), ing("Butter", 10, "g"));

        seedRecipe(db, "French Toast",
                "1. Whisk the eggs, milk and sugar.\n2. Soak the bread slices in the mixture.\n3. Fry in butter until golden on both sides.",
                ing("Bread", 4, "pcs"), ing("Eggs", 2, "pcs"), ing("Milk", 100, "ml"), ing("Sugar", 10, "g"), ing("Butter", 20, "g"));

        seedRecipe(db, "Plain Buttered Rice",
                "1. Rinse the rice.\n2. Boil in salted water for 15 minutes.\n3. Drain and stir through the butter.",
                ing("Rice", 200, "g"), ing("Salt", 2, "g"), ing("Butter", 10, "g"));

        seedRecipe(db, "Rice Pudding",
                "1. Put the rice, milk and sugar in a pot.\n2. Simmer gently for 30 minutes, stirring often.\n3. Stir in the butter and serve warm.",
                ing("Rice", 100, "g"), ing("Milk", 600, "ml"), ing("Sugar", 50, "g"), ing("Butter", 10, "g"));

        seedRecipe(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry the chopped onion in the oil.\n3. Add the rice, then push aside and scramble the eggs.\n4. Mix together with the soy sauce.",
                ing("Rice", 200, "g"), ing("Eggs", 2, "pcs"), ing("Onion", 1, "pcs"), ing("Cooking Oil", 30, "ml"), ing("Soy Sauce", 20, "ml"));

        seedRecipe(db, "Tomato Soup",
                "1. Fry the chopped onion in the butter until soft.\n2. Add the chopped tomatoes, salt and a cup of water.\n3. Simmer for 20 minutes, then blend until smooth.",
                ing("Tomato", 6, "pcs"), ing("Onion", 1, "pcs"), ing("Butter", 20, "g"), ing("Salt", 5, "g"));

        seedRecipe(db, "Chakalaka",
                "1. Fry the onion in the oil.\n2. Add the curry powder, grated carrots and chopped tomatoes.\n3. Cook for 10 minutes, then stir in the baked beans and heat through.",
                ing("Onion", 1, "pcs"), ing("Tomato", 2, "pcs"), ing("Carrot", 2, "pcs"), ing("Baked Beans", 410, "g"), ing("Cooking Oil", 30, "ml"), ing("Curry Powder", 10, "g"));

        seedRecipe(db, "Mielie Pap",
                "1. Bring 750 ml of salted water to the boil.\n2. Slowly stir in the maize meal.\n3. Cover and cook on low for 20 minutes, stirring now and then.\n4. Stir in the butter.",
                ing("Maize Meal", 250, "g"), ing("Salt", 5, "g"), ing("Butter", 10, "g"));

        seedRecipe(db, "Spaghetti Bolognese",
                "1. Fry the onion in the oil, then brown the mince.\n2. Add the chopped tomatoes and simmer for 20 minutes.\n3. Serve over the cooked spaghetti.",
                ing("Spaghetti", 250, "g"), ing("Beef Mince", 500, "g"), ing("Tomato", 3, "pcs"), ing("Onion", 1, "pcs"), ing("Cooking Oil", 15, "ml"));

        seedRecipe(db, "Macaroni and Cheese",
                "1. Cook the macaroni.\n2. Melt the butter, stir in the flour, then slowly add the milk to make a sauce.\n3. Stir in the cheese and mix with the macaroni.",
                ing("Macaroni", 250, "g"), ing("Cheese", 150, "g"), ing("Milk", 500, "ml"), ing("Butter", 30, "g"), ing("Flour", 30, "g"));

        seedRecipe(db, "Banana Smoothie",
                "1. Peel and slice the bananas.\n2. Blend with the milk and sugar until smooth.",
                ing("Banana", 2, "pcs"), ing("Milk", 250, "ml"), ing("Sugar", 10, "g"));

        seedRecipe(db, "Potato Wedges",
                "1. Cut the potatoes into wedges.\n2. Toss with the oil and salt.\n3. Bake at 200°C for 35 minutes, turning once.",
                ing("Potato", 4, "pcs"), ing("Cooking Oil", 30, "ml"), ing("Salt", 5, "g"));

        seedRecipe(db, "Vetkoek",
                "1. Mix the flour, yeast, sugar and salt with warm water into a soft dough.\n2. Leave to rise for 1 hour.\n3. Shape into balls and deep-fry in the oil until golden.",
                ing("Flour", 500, "g"), ing("Yeast", 10, "g"), ing("Sugar", 15, "g"), ing("Salt", 5, "g"), ing("Cooking Oil", 500, "ml"));

        seedRecipe(db, "Tuna Mayo Sandwich",
                "1. Mix the drained tuna with the mayonnaise.\n2. Spread between the slices of bread.",
                ing("Bread", 2, "pcs"), ing("Tuna", 170, "g"), ing("Mayonnaise", 30, "ml"));

        seedRecipe(db, "Garlic Bread",
                "1. Mash the crushed garlic into the butter.\n2. Spread on the bread slices.\n3. Grill until golden.",
                ing("Bread", 4, "pcs"), ing("Butter", 40, "g"), ing("Garlic", 2, "pcs"));
    }

    // Starter pantry, so the demo shows strict matching straight away.
    // Some items use kg / l and plural names on purpose, to show the matching copes with that.
    private void seedPantry(SQLiteDatabase db) {
        insertIngredient(db, "Eggs", 6f, "pcs", "");
        insertIngredient(db, "Milk", 1f, "l", "");
        insertIngredient(db, "Oats", 500f, "g", "");
        insertIngredient(db, "Sugar", 1f, "kg", "");
        insertIngredient(db, "Butter", 250f, "g", "");
        insertIngredient(db, "Bread", 10f, "pcs", "");
        insertIngredient(db, "Cheese", 200f, "g", "");
        insertIngredient(db, "Tomatoes", 4f, "pcs", "");
        insertIngredient(db, "Onions", 2f, "pcs", "");
        insertIngredient(db, "Rice", 1f, "kg", "");
        insertIngredient(db, "Salt", 500f, "g", "");
        insertIngredient(db, "Cooking Oil", 750f, "ml", "");
    }

    // Small helper so the recipe list above stays readable
    private static RecipeIngredient ing(String name, float qty, String unit) {
        return new RecipeIngredient(name, qty, unit);
    }

    private void seedRecipe(SQLiteDatabase db, String title, String instructions, RecipeIngredient... items) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_INSTRUCTIONS, instructions);
        long recipeId = db.insert(TABLE_RECIPES, null, values);

        for (RecipeIngredient item : items) {
            ContentValues link = new ContentValues();
            link.put(KEY_RECIPE_ID, recipeId);
            link.put(KEY_INGREDIENT_NAME, item.getName());
            link.put(KEY_REQUIRED_QTY, item.getRequiredQty());
            link.put(KEY_UNIT, item.getUnit());
            db.insert(TABLE_RECIPE_INGREDIENTS, null, link);
        }
    }

    private long insertIngredient(SQLiteDatabase db, String name, float qty, String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_QUANTITY, qty);
        values.put(KEY_UNIT, unit);
        values.put(KEY_EXPIRY, expiry);
        return db.insert(TABLE_INGREDIENTS, null, values);
    }

    // ------------------------------------------------------------------
    // Pantry CRUD
    // ------------------------------------------------------------------

    // CREATE
    public long addIngredient(String name, float qty, String unit, String expiry) {
        return insertIngredient(this.getWritableDatabase(), name, qty, unit, expiry);
    }

    // READ all (for the pantry list)
    public Cursor getAllIngredients() {
        return this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_INGREDIENTS + " ORDER BY " + KEY_NAME + " COLLATE NOCASE", null);
    }

    // READ one (to fill in the Edit screen)
    public Cursor getIngredient(long id) {
        return this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // UPDATE
    public int updateIngredient(long id, String name, float qty, String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_QUANTITY, qty);
        values.put(KEY_UNIT, unit);
        values.put(KEY_EXPIRY, expiry);
        return this.getWritableDatabase().update(TABLE_INGREDIENTS, values,
                KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // DELETE
    public void deleteIngredient(long id) {
        this.getWritableDatabase().delete(TABLE_INGREDIENTS, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ------------------------------------------------------------------
    // Recipes + strict matching
    // ------------------------------------------------------------------

    public Cursor getRecipe(long recipeId) {
        return this.getReadableDatabase().rawQuery(
                "SELECT " + KEY_TITLE + ", " + KEY_INSTRUCTIONS
                        + " FROM " + TABLE_RECIPES
                        + " WHERE " + KEY_ID + " = ?",
                new String[]{String.valueOf(recipeId)});
    }

    // The whole pantry as a list of Ingredient objects (used by the matcher)
    private List<Ingredient> loadPantry() {
        List<Ingredient> pantry = new ArrayList<>();
        Cursor c = getAllIngredients();
        if (c.moveToFirst()) {
            do {
                pantry.add(new Ingredient(
                        c.getLong(c.getColumnIndexOrThrow(KEY_ID)),
                        c.getString(c.getColumnIndexOrThrow(KEY_NAME)),
                        c.getFloat(c.getColumnIndexOrThrow(KEY_QUANTITY)),
                        c.getString(c.getColumnIndexOrThrow(KEY_UNIT)),
                        c.getString(c.getColumnIndexOrThrow(KEY_EXPIRY))));
            } while (c.moveToNext());
        }
        c.close();
        return pantry;
    }

    // Every recipe's ingredient lines, grouped by recipe id, in one query
    private Map<Long, List<RecipeIngredient>> loadAllRecipeIngredients() {
        Map<Long, List<RecipeIngredient>> map = new HashMap<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS, null);
        if (c.moveToFirst()) {
            do {
                long recipeId = c.getLong(c.getColumnIndexOrThrow(KEY_RECIPE_ID));
                List<RecipeIngredient> lines = map.get(recipeId);
                if (lines == null) {
                    lines = new ArrayList<>();
                    map.put(recipeId, lines);
                }
                lines.add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow(KEY_INGREDIENT_NAME)),
                        c.getFloat(c.getColumnIndexOrThrow(KEY_REQUIRED_QTY)),
                        c.getString(c.getColumnIndexOrThrow(KEY_UNIT))));
            } while (c.moveToNext());
        }
        c.close();
        return map;
    }

    /**
     * STRICT MATCHING: returns only the recipes the user can make right now,
     * i.e. every required ingredient is in the pantry in at least the required amount.
     * The actual rule lives in IngredientMatcher.canMake().
     */
    public List<Recipe> getSuggestedRecipes() {
        Map<String, Double> pantryTotals = IngredientMatcher.buildPantryTotals(loadPantry());
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe = loadAllRecipeIngredients();

        List<Recipe> suggested = new ArrayList<>();
        Cursor c = this.getReadableDatabase().rawQuery(
                "SELECT " + KEY_ID + ", " + KEY_TITLE + " FROM " + TABLE_RECIPES
                        + " ORDER BY " + KEY_TITLE + " COLLATE NOCASE", null);
        if (c.moveToFirst()) {
            do {
                long id = c.getLong(c.getColumnIndexOrThrow(KEY_ID));
                List<RecipeIngredient> required = ingredientsByRecipe.get(id);
                if (required != null && IngredientMatcher.canMake(required, pantryTotals)) {
                    suggested.add(new Recipe(id, c.getString(c.getColumnIndexOrThrow(KEY_TITLE))));
                }
            } while (c.moveToNext());
        }
        c.close();
        return suggested;
    }

    // One recipe's ingredient lines, each with how much the pantry has (for the detail screen)
    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> lines = loadAllRecipeIngredients().get(recipeId);
        if (lines == null) return new ArrayList<>();
        IngredientMatcher.canMake(lines, IngredientMatcher.buildPantryTotals(loadPantry())); // fills in haveQty
        return lines;
    }
}