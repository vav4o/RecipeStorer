package com.example.recipestorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "RecipeStorer";
    private static final String TABLE_NAME = "recipes";
    private static final String KEY_ID = "id";
    private static final String RECIPE_NAME = "name";
    private static final String CATEGORY = "category";
    private static final String AREA = "area";
    private static final String INSTRUCTIONS = "instructions";
    private static final String IMAGE = "image";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + KEY_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + RECIPE_NAME + " TEXT, " + CATEGORY + " TEXT, " + AREA + " TEXT, " + INSTRUCTIONS + " TEXT, " + IMAGE + " TEXT " + " )";
        db.execSQL(CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECIPE_NAME, recipe.getStrMeal());
        values.put(CATEGORY, recipe.getStrCategory());
        values.put(AREA, recipe.getStrArea());
        values.put(INSTRUCTIONS, recipe.getStrInstructions());
        values.put(IMAGE, recipe.getStrMealThumb());
        long insert = db.insert(TABLE_NAME, null, values);
        if (insert == -1) return false;
        else return true;
    }

    public ArrayList<Recipe> getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        Recipe recipeModel = new Recipe();
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                recipeModel = new Recipe();
                recipeModel.setId(cursor.getInt(0));
                recipeModel.setStrMeal(cursor.getString(1));
                recipeModel.setStrCategory(cursor.getString(2));
                recipeModel.setStrArea(cursor.getString(3));
                recipeModel.setStrInstructions(cursor.getString(4));
                recipeModel.setStrMealThumb(cursor.getString(5));
                recipes.add(recipeModel);
            }
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public boolean deleteOne(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = " DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + recipe.getId();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst())
            return true;
        else return false;
    }

    public boolean updateOne(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECIPE_NAME, recipe.getStrMeal());
        contentValues.put(CATEGORY, recipe.getStrCategory());
        contentValues.put(AREA, recipe.getStrArea());
        contentValues.put(INSTRUCTIONS, recipe.getStrInstructions());
        contentValues.put(IMAGE, recipe.getStrMealThumb());
        db.update(TABLE_NAME, contentValues, " ID = ?", new String[]{String.valueOf(recipe.getId())});
        return true;
    }
}
