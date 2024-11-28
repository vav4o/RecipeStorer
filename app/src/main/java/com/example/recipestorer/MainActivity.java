package com.example.recipestorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //private ActivityMainBinding binding;
    private Button btnRandomRecipe;
    private Button btnCreateRecipe;
    private Button btnViewRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnRandomRecipe = findViewById(R.id.btnRandomRecipe);
        btnCreateRecipe = findViewById(R.id.btnCreateRecipe);
        btnViewRecipes = findViewById(R.id.btnViewRecepies);
        btnRandomRecipe.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   startActivity(new Intent(MainActivity.this, RandomRecipe.class));
                                               }
                                           }
        );

        btnCreateRecipe.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   startActivity(new Intent(MainActivity.this, CreateRecipe.class));
                                               }
                                           }
        );

        btnViewRecipes.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  startActivity(new Intent(MainActivity.this, ShowMyRecipes.class));
                                              }
                                          }
        );
    }
}