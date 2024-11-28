package com.example.recipestorer;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.VideoCapture;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CreateRecipe extends AppCompatActivity implements View.OnClickListener {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private Button btnTakePictures;
    private Button btnSaveNew;
    private Button btnBack;
    private Bitmap recipePhoto;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private TextView setRecipeName;
    private TextView setRecipeCategory;
    private TextView setRecipeArea;
    private TextView setRecipeInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_recipe);

        btnTakePictures = findViewById(R.id.btnTakePicture);
        btnSaveNew = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        //previewView = findViewById(R.id.textureView);
        previewView = findViewById(R.id.previewView);
        setRecipeName = findViewById(R.id.update_name);
        setRecipeCategory = findViewById(R.id.update_category);
        setRecipeArea = findViewById(R.id.update_area);
        setRecipeInstructions = findViewById(R.id.update_instructions);

        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build();

                // Set up the capture use case to allow users to take photos
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build();

                // Choose the camera by requiring a lens facing
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Attach use cases to the camera with the same lifecycle owner
                Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        preview,
                        imageCapture);

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());
            } catch (InterruptedException | ExecutionException e) {
            }
        }, ContextCompat.getMainExecutor(this));

        btnSaveNew.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              Recipe my_recipe = new Recipe();

                                              String path = saveToInternalStorage(recipePhoto);

                                              my_recipe = new Recipe(setRecipeName.getText().toString(),
                                                      setRecipeCategory.getText().toString(),
                                                      setRecipeArea.getText().toString(),
                                                      setRecipeInstructions.getText().toString(),
                                                      path
                                              );
                                              Toast.makeText(CreateRecipe.this, my_recipe.toString(), Toast.LENGTH_SHORT).show();
                                              DatabaseHelper databaseHelper = new DatabaseHelper(CreateRecipe.this);
                                              boolean success = databaseHelper.addRecipe(my_recipe);
                                              Toast.makeText(CreateRecipe.this, "success", Toast.LENGTH_SHORT).show();
                                          }
                                      }
        );

        btnTakePictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
                btnTakePictures.setVisibility(View.INVISIBLE);
                btnSaveNew.setVisibility(View.VISIBLE);
            }
        });

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
        File mypath = new File(directory, setRecipeName.getText().toString() + ".jpg");

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

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    //@SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        //videoCapture = new VideoCapture.Builder()
        //        .setVideoFrameRate(30)
        //        .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    //@SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btnTakePicture: {
//                if (bRecording.getText() == "RECORD") {
//                    bRecording.setText("STOP");
//                    recordVideo();
//                } else {
//                    bRecording.setText("RECORD");
//                    videoCapture.stopRecording();
//                }
//                break;
//            }
//            case R.id.btnTakePicture: {
//                capturePhoto();
//                break;
//            }
//
//        }


    }

    //@SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {
            long timeStamp = System.currentTimeMillis();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");


            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
            //}
//            videoCapture.startRecording(
//                    new VideoCapture.OutputFileOptions.Builder(
//                            getContentResolver(),
//                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                            contentValues
//                    ).build(),
//                    getExecutor(),
//                    new VideoCapture.OnVideoSavedCallback() {
//                        @Override
//                        public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
//                            Toast.makeText(MainActivity.this,"Saving...",Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
//                            Toast.makeText(MainActivity.this,"Error: "+ message ,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//            );


        }
    }

    private void capturePhoto() {
        long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        ImageView img = (ImageView) findViewById(R.id.imageRecipe);

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CreateRecipe.this, "Saving...", Toast.LENGTH_SHORT).show();

                        //Show photo from gallery
                        String[] projection = new String[]{
                                MediaStore.Images.ImageColumns._ID,
                                MediaStore.Images.ImageColumns.DATA,
                                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                MediaStore.Images.ImageColumns.DATE_TAKEN,
                                MediaStore.Images.ImageColumns.MIME_TYPE
                        };
                        final Cursor cursor = CreateRecipe.this.getContentResolver()
                                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

                        // Put it in the image view
                        if (cursor.moveToFirst()) {
                            final ImageView imageView = (ImageView) findViewById(R.id.imageEdit);
                            String imageLocation = cursor.getString(1);
                            File imageFile = new File(imageLocation);
                            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                                Bitmap bm = BitmapFactory.decodeFile(imageLocation);


                                Matrix matrix = new Matrix();

                                matrix.postRotate(90);

                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, 300, 400, true);

                                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                                recipePhoto = rotatedBitmap;
                                imageView.setImageBitmap(rotatedBitmap);
                                imageView.setVisibility(View.VISIBLE);
                                //cameraProviderFuture.cancel(true);
                                previewView.setVisibility(View.INVISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CreateRecipe.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
