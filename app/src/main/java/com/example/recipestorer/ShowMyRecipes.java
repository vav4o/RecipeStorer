package com.example.recipestorer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowMyRecipes extends AppCompatActivity {

    private ArrayList<Recipe> query_list;
    static ListView recipesList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recipes_list);

        DatabaseHelper myDB = new DatabaseHelper(this);
        recipesList = findViewById(R.id.recipesList);

        query_list = myDB.getAllRecords();
        MyRecipes customAdapter = new MyRecipes(ShowMyRecipes.this, query_list);
        recipesList.setAdapter(customAdapter);

        recipesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMyRecipes.this);
                builder.setTitle("Title");
                builder.setItems(new CharSequence[]{"DELETE", "UPDATE"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new AlertDialog.Builder(ShowMyRecipes.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("DELETE AN ITEM")
                                        .setMessage("Are you sure you want to delete this item")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Recipe clicked_contact = (Recipe) parent.getItemAtPosition(position);
                                                myDB.deleteOne(clicked_contact);
                                                customAdapter.notifyDataSetChanged();
                                                Toast.makeText(ShowMyRecipes.this, "DELETED!", Toast.LENGTH_SHORT).show();
                                                query_list = myDB.getAllRecords();
                                                MyRecipes customAdapter = new MyRecipes(ShowMyRecipes.this, query_list);
                                                recipesList.setAdapter(customAdapter);
                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(ShowMyRecipes.this, "OK", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .show();
                                break;
                            case 1:
                                new AlertDialog.Builder(ShowMyRecipes.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("UPDATE AN ITEM")
                                        .setMessage("Are you sure you want to update the item?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Recipe clicked_contact = (Recipe) parent.getItemAtPosition(position);
                                                Integer send_id = clicked_contact.getId();
                                                String send_name = clicked_contact.getStrMeal();
                                                String send_category = clicked_contact.getStrCategory();
                                                String send_area = clicked_contact.getStrArea();
                                                String send_instructions = clicked_contact.getStrInstructions();
                                                String send_path = clicked_contact.getStrMealThumb();
                                                Intent intent_go = new Intent(ShowMyRecipes.this, EditRecipe.class);
                                                intent_go.putExtra("send_id", send_id);
                                                intent_go.putExtra("send_name", send_name);
                                                intent_go.putExtra("send_category", send_category);
                                                intent_go.putExtra("send_area", send_area);
                                                intent_go.putExtra("send_instructions", send_instructions);
                                                intent_go.putExtra("send_path", send_path);
                                                startActivity(intent_go);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(ShowMyRecipes.this, "OK", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .show();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }
}
