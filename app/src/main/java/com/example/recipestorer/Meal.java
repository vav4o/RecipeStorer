package com.example.recipestorer;

import java.util.List;

public class Meal {
    private List<Recipe> meals;

    public Meal(List<Recipe> meals) {
        this.meals = meals;
    }

    public List<Recipe> getMeals() {
        return meals;
    }

    public void setMeals(List<Recipe> meals) {
        this.meals = meals;
    }
}
