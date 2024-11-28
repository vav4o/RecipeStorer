package com.example.recipestorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MyRecipes extends ArrayAdapter<Recipe> {
    private final LayoutInflater inflater;
    private final ArrayList<Recipe> objects;

    private static class ViewHolder {
        TextView textMeal;
        TextView textMealCategory;
        TextView textMealArea;
    }

    public MyRecipes(@NonNull Context context, ArrayList<Recipe> objects) {
        super(context, R.layout.my_recipes_view, objects);
        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public int getCount() {
        return objects.size();
    }

    public Recipe getItem(int position) {
        return objects.get(position);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.my_recipes_view, null);
            holder.textMeal = convertView.findViewById(R.id.textMeal);
            holder.textMealCategory = convertView.findViewById(R.id.textMealCategory);
            holder.textMealArea = convertView.findViewById(R.id.textMealArea);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textMeal.setText(objects.get(position).getStrMeal());
        holder.textMealCategory.setText(objects.get(position).getStrCategory());
        holder.textMealArea.setText(objects.get(position).getStrArea());
        return convertView;
    }
}

