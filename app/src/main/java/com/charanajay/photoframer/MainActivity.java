package com.charanajay.photoframer;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 10;
    private ImageView imageView;
    private Button select_image;
    final String[] items = new String[]{"From Camera", "From SD Card"};
    private Uri imageCapturedURI = null;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        select_image = findViewById(R.id.select_image);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                & ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                & ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) == PackageManager.PERMISSION_GRANTED) {
            select_image.setOnClickListener(this);
        } else {
            String[] permissionsRequested = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionsRequested, REQUEST_CODE);
        }

        imageView.setOnClickListener(this);
        select_image.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    imageView.setOnClickListener(this);
                } else {
                    Toast.makeText(this, "Permissions not enabled", Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_image:
                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setTitle("Choose option");
                dialogBuilder.setAdapter(stringArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int option) {
                        if (option == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(Environment.getExternalStorageDirectory(), "tmp_images" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                            imageCapturedURI = Uri.fromFile(file);
                            try {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCapturedURI);
                                intent.putExtra("return data", true);

                                startActivityForResult(intent, PICK_FROM_CAMERA);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), PICK_FROM_FILE);
                        }
                    }
                });

                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        Bitmap imageBitmap = null;
        String imagePath = "";
        if (requestCode == PICK_FROM_FILE) {
            imageCapturedURI = data.getData();
            imagePath = getRealPathFromUri(imageCapturedURI);
            if (imagePath == null)
                imagePath = imageCapturedURI.getPath();
            if (imagePath != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                imageBitmap = BitmapFactory.decodeFile(imagePath, options);
            }
        } else {
            imagePath = imageCapturedURI.getPath();
            imageBitmap = BitmapFactory.decodeFile(imagePath);
        }
        imageView.setImageBitmap(imageBitmap);
    }

    private String getRealPathFromUri(Uri imageCapturedURI) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, imageCapturedURI, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
