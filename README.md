Smart Pantry Manager

A Java Android app that helps reduce food waste. You keep track of the ingredients you already have at home (your "pantry"), and the app suggests recipes you can cook using only those ingredients. There's no shopping trip, and no recipe is suggested unless you genuinely have everything it needs.

Built for Mobile App Development 700 (Richfield) by Saieshen Govender.

Features
Pantry management (full CRUD): add, view, edit and delete ingredients, each with a name, quantity, unit and an optional expiry date.
Pantry list: a RecyclerView with a custom adapter, loaded from the database.
Recipe collection: 20 recipes are seeded into the database on first run, each with ingredients, quantities and a method.
Suggested Recipes (strict matching): a recipe is shown only if every ingredient it needs is in the pantry in at least the required amount. A recipe with one missing or short ingredient is excluded.
Robust matching: ingredient names are normalised (Tomatoes = tomato, case and spaces ignored). Units are converted (1 kg = 1000 g, 1 l = 1000 ml, pieces = pcs). Several pantry entries with the same ingredient are added together.
Recipe detail: shows the full ingredient list, with how much of each you have, and the method.
Settings: a toggle to highlight ingredients that expire within 3 days, saved with SharedPreferences.
Empty state: shows a friendly message when no recipes match, instead of a blank screen.
Input validation: required fields, numbers greater than 0, and real dates in YYYY-MM-DD format.
Screens
Pantry tab: the list of ingredients (tap one to edit it, or use the bin icon to delete it)
Add / Edit Ingredient: opened with the + button, or by tapping an item
Suggested Recipes tab: only the recipes you can make right now
Recipe Detail: ingredients and method
Settings: opened from the ⋮ menu in the toolbar
Database: SQLite

The app uses SQLite through SQLiteOpenHelper, stored locally on the device.

Why SQLite:

The pantry is personal data that only one user needs, so it doesn't have to sync to a cloud or be shared.
It works fully offline and needs no account, server or internet connection.
It's built into Android, so there are no extra services to set up. It's also the approach covered in the module's persistent data chapter.
The data is relational (recipes → required ingredients), which suits SQL tables and queries.

Tables

Table	Columns
ingredients	id (PK), name, quantity, unit, expiry_date
recipes	id (PK), title, instructions
recipe_ingredients	recipe_id (FK → recipes.id), ingredient_name, required_qty, unit

Recipe ingredients are linked by name, not by pantry row id. That way a recipe still matches after the user deletes an ingredient and adds it again.

Project structure
File	Purpose
MainActivity	Host screen: toolbar and ⋮ menu, Pantry/Recipes tabs (ViewPager), + button
PantryFragment / PantryAdapter	Pantry list (RecyclerView + custom adapter)
AddIngredientActivity	Add and edit form with validation (the id is passed by Intent extra)
RecipesFragment / RecipeAdapter	Suggested recipes list
RecipeDetailActivity	One recipe's ingredients and method
SettingsActivity	Expiring-soon toggle (SharedPreferences)
DatabaseHelper	SQLite tables, seed data and CRUD
IngredientMatcher	The strict-matching logic (name normalisation, unit conversion)
How to run
Install Android Studio (a recent version).
Clone this repository:
git clone https://github.com/saieshen govender /Smart-Pantry-Manager.git
In Android Studio, choose File → Open and select the cloned folder. Wait for the Gradle sync to finish.
Create an emulator in Device Manager (for example Medium Phone, API 24 or later), or connect an Android phone with USB debugging turned on.
Click  Run.

The app opens with a starter pantry of 12 items, and 8 recipes are suggested straight away. Try adding Flour, 1, kg and Classic Pancakes will appear in the suggestions.

Requirements: minimum SDK 24 (Android 7.0), written in Java.