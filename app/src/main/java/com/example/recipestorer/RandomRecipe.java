package com.example.recipestorer;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomRecipe extends AppCompatActivity {
    private ApiInterface intf;
    private Button btnNewRandom;
    private Button btnSave;
    private Button btnBack;
    private TextView titleTextView;
    private TextView categoryTextView;
    private TextView areaTextView;
    private TextView instructionsTextView;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_recipe);
        intf = RecipeApi.getClient().create(ApiInterface.class);
        btnNewRandom = findViewById(R.id.btnNewRandom);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        imgView = findViewById(R.id.imageRecipe);
        getRandomRecipe();
        btnNewRandom.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(RandomRecipe.this, RandomRecipe.class));
                                                finish();
                                            }
                                        }
        );
        btnSave.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Recipe my_recipe = new Recipe();

                                           Drawable drawable = imgView.getDrawable();
                                           BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                                           Bitmap bitmap = bitmapDrawable.getBitmap();
                                           ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                           byte[] imageInByte = stream.toByteArray();
                                           ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);

                                           String path = saveToInternalStorage(bitmap);

                                           my_recipe = new Recipe(titleTextView.getText().toString(),
                                                   categoryTextView.getText().toString(),
                                                   areaTextView.getText().toString(),
                                                   instructionsTextView.getText().toString(),
                                                   path
                                           );
                                           Toast.makeText(RandomRecipe.this, my_recipe.toString(), Toast.LENGTH_SHORT).show();
                                           DatabaseHelper databaseHelper = new DatabaseHelper(RandomRecipe.this);
                                           boolean success = databaseHelper.addRecipe(my_recipe);
                                           Toast.makeText(RandomRecipe.this, "success", Toast.LENGTH_SHORT).show();
                                       }
                                   }
        );
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/recipestorer/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, titleTextView.getText().toString() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static class DownloadImageTask {
        private final ImageView bmImage;

        // ExecutorService to handle background thread tasks
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        public void execute(String url) {
            // Run the task in the background thread
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Bitmap result = downloadImage(url);
                    // Use a Handler to run UI updates on the main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            bmImage.setImageBitmap(result);
                        }
                    });
                }
            });
        }

        private Bitmap downloadImage(String urlString) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(urlString).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                Log.e("DownloadImageTask", "Error downloading image: " + e.getMessage());
            }
            return bitmap;
        }
    }

    private void getRandomRecipe() {
        Call<Meal> call = intf.getRandomRecipe();
        call.enqueue(new Callback<Meal>() {
            @Override
            public void onResponse(Call<Meal> call, Response<Meal> response) {
                titleTextView = findViewById(R.id.recipeName);
                titleTextView.setText(response.body().getMeals().get(0).getStrMeal());
                categoryTextView = findViewById(R.id.textCategory);
                categoryTextView.setText(response.body().getMeals().get(0).getStrCategory());
                areaTextView = findViewById(R.id.textArea);
                areaTextView.setText(response.body().getMeals().get(0).getStrArea());
                instructionsTextView = findViewById(R.id.textRecipe);
                instructionsTextView.setText(response.body().getMeals().get(0).getStrInstructions());

                new DownloadImageTask((ImageView) findViewById(R.id.imageRecipe))
                        .execute(response.body().getMeals().get(0).getStrMealThumb());
            }

            @Override
            public void onFailure(Call<Meal> call, Throwable t) {
            }
        });
    }
}

