package com.example.recipestorer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EditRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_recipe);

        DatabaseHelper myDb = new DatabaseHelper(this);
        EditText update_name = findViewById(R.id.update_name);
        EditText update_category = findViewById(R.id.update_category);
        EditText update_area = findViewById(R.id.update_area);
        EditText update_instructions = findViewById(R.id.update_instructions);
        Button btn_edit = findViewById(R.id.btnEdit);
        Button btnBack = findViewById(R.id.btnBack);
        ImageView imgView = findViewById(R.id.imageEdit);

        Integer sent_main_id = getIntent().getExtras().getInt("send_id");
        String sent_main_name = getIntent().getExtras().getString("send_name");
        String sent_main_category = getIntent().getExtras().getString("send_category");
        String sent_main_area = getIntent().getExtras().getString("send_area");
        String sent_main_instructions = getIntent().getExtras().getString("send_instructions");
        String sent_main_path = getIntent().getExtras().getString("send_path");

        update_name.setText(sent_main_name);
        update_category.setText(sent_main_category);
        update_area.setText(sent_main_area);
        update_instructions.setText(sent_main_instructions);

        loadImageFromStorage(sent_main_path, sent_main_name);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_name = update_name.getText().toString();
                String new_category = update_category.getText().toString();
                String new_area = update_area.getText().toString();
                String new_instructions = update_instructions.getText().toString();
                Recipe new_recipe = new Recipe(new_name, new_category, new_area, new_instructions, sent_main_path);
                new_recipe.setId(sent_main_id);
                myDb.updateOne(new_recipe);
                boolean update_success = myDb.updateOne(new_recipe);
                if (update_success)
                    Toast.makeText(EditRecipe.this, "Updated" + new_recipe.toString(),
                            Toast.LENGTH_SHORT).show();
                else Toast.makeText(EditRecipe.this, "Not updated", Toast.LENGTH_SHORT).show();
                ArrayList<Recipe> query_list = myDb.getAllRecords();
                MyRecipes myAdapter = new MyRecipes(EditRecipe.this, query_list);
                ShowMyRecipes.recipesList.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
                Intent intent_go_back = new Intent(EditRecipe.this, MainActivity.class);
                startActivity(intent_go_back);
                finish();
            }
        });
    }

    private void loadImageFromStorage(String path, String name) {
        try {
            File f = new File(path, name + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.imageEdit);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
