package com.charanajay.photoframer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by charank on 19-03-2018.
 */

public class ImageOpenActivity extends AppCompatActivity{
    @BindView(R.id.open_photo_image)
    ImageView openPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.first_page);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.activity_title_main));

        openPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageOpenActivity.this,MainActivity.class);
                startActivity(intent);
//                openGallery(view);
            }
        });
    }

    private void openGallery(View view) {
        Crop.pickImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==Crop.REQUEST_PICK){
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(getCacheDir(),"cropped"));

                Log.d("destination URI :",destination_uri.toString());

                Crop.of(source_uri,destination_uri).asSquare().start(this);
                openPhoto.setImageURI(Crop.getOutput(data));
            }
            else if(requestCode==Crop.REQUEST_CROP){
                handleCrop(resultCode,data);
            }
        }
    }

    private void handleCrop(int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Intent intent = new Intent(ImageOpenActivity.this,MainActivity.class);
            intent.putExtra("imageUri",Crop.getOutput(data));
            intent.setData(Crop.getOutput(data));
            startActivity(intent);
//            openPhoto.setImageURI(Crop.getOutput(data));
        }else if(resultCode == Crop.RESULT_ERROR){
            Toast.makeText(this.getApplicationContext(),"Error while cropping",Toast.LENGTH_SHORT).show();
        }
    }
}
