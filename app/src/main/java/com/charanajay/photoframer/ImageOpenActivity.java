package com.charanajay.photoframer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by charank on 19-03-2018.
 */

public class ImageOpenActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.first_page);

        ButterKnife.bind(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getString(R.string.activity_title_main));
    }
    public void openImageFromGallery(View view){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("selectionType","gallery");
        startActivity(intent);
    }

    public void openImageFromCamera(View view){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("selectionType","camera");
        startActivity(intent);
    }
    public void openSavedPhotos(View view){
        String appDirectoryName = "IPL Framer";
        final File imageRoot = new File(Environment.getExternalStorageDirectory(),appDirectoryName);

        if(imageRoot.exists()) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Pictures");
            intent.setDataAndType(Uri.parse("file://" +imageRoot.toString()), "image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(Intent.createChooser(intent, "Open images"));
            startActivity(intent);
        }
        else Toast.makeText(getApplicationContext(),"You have not saved any images from the app!",Toast.LENGTH_LONG).show();
    }
}
