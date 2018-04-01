package com.charanajay.photoframer;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.charanajay.photoframer.utils.*;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAMERA_PIC_REQUEST = 102;

    public static String IMAGE_NAME = "";

    public static final int SELECT_GALLERY_IMAGE = 101;

    private Uri imageCapturedURI = null;

    @BindView(R.id.image_preview)
    ZoomableImageView imagePreview;

    @BindView(R.id.framer)
    ImageView framer;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    static Bitmap originalImage;
    // to backup image with filter applied
    static Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    static Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;
    FrameListFragment frameListFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    private RewardedVideoAd mRewardedVideoAd;
    InterstitialAd interstitialAd;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.activity_title_main));

        // loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //initialize ads
        MobileAds.initialize(this, "ca-app-pub-3894268392664867/5935308763");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3894268392664867/5935308763");
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

//        imageCapturedURI = getIntent().getData();
//        setCroppedImage(imageCapturedURI);

        Intent intent = getIntent();
        String selectionType = intent.getStringExtra("selectionType");
        if (selectionType.equals("gallery"))
            openImageFromGallery();
        else openCamera();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        frameListFragment = new FrameListFragment();

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));
        adapter.addFragment(frameListFragment, "FRAMES");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {
        sliderListener mySliderListener = new sliderListener();
        SeekBar brightnessBar = findViewById(R.id.seekbar_brightness);
        brightnessBar.setOnSeekBarChangeListener(mySliderListener);

        SeekBar contrastBar = findViewById(R.id.seekbar_contrast);
        contrastBar.incrementProgressBy(1);
        contrastBar.setOnSeekBarChangeListener(mySliderListener);

        SeekBar saturationBar = findViewById(R.id.seekbar_saturation);
        saturationBar.incrementProgressBy(1);
        saturationBar.setOnSeekBarChangeListener(mySliderListener);
    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        //final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        /*Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));*/
        //finalImage = myFilter.processFilter(bitmap);


    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if (id == R.id.action_save) {
            //show ads
//            if (interstitialAd.isLoaded())
//                interstitialAd.show();
//            interstitialAd.setAdListener(new AdListener() {
//                @Override
//                public void onAdClosed() {
                    saveImageToGallery();
//                }
//            });
            return true;
        }

        if (id == R.id.action_share) {
            if (interstitialAd.isLoaded())
                interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    shareImage();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                Log.d("destination URI :", destination_uri.toString());

                UCrop.of(source_uri, destination_uri)
                        .withAspectRatio(1, 1)
                        .start(this);
            }
            if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
                Log.d("x", "called camera pick");
                Uri source_uri = imageCapturedURI;
                Log.d("Sourceuri :", source_uri.toString());
                Uri destination_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                Log.d("destination URI :", destination_uri.toString());

                UCrop.of(source_uri, destination_uri)
                        .withAspectRatio(1, 1)
                        .start(this);
            } else if (requestCode == UCrop.REQUEST_CROP) {
                setCroppedImage(UCrop.getOutput(data));
            }
        }
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {

            Bitmap imageBitmap = null;
            String imagePath = "";
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

                IMAGE_NAME = imagePath;
                originalImage = imageBitmap.copy(imageBitmap.getConfig(), true);
                filteredImage = imageBitmap.copy(imageBitmap.getConfig(), true);
                finalImage = imageBitmap.copy(imageBitmap.getConfig(), true);
                imagePreview.setImageBitmap(imageBitmap);


                // render selected image thumbnails
                filtersListFragment.prepareThumbnail(originalImage);
            }
        }
    }

    private String getRealPathFromUri(Uri imageCapturedURI) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, imageCapturedURI, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void openImageFromGallery() {
        framer.setImageResource(0);
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
//                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            intent.setType("image/*");
//                            startActivityForResult(intent, SELECT_GALLERY_IMAGE);
                            callCroper();
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();    //works without crop. do not remove keeping it for record

    }

    public void openCamera() {
        Log.d("CAMERA", "called function");
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String dirName = String.valueOf(System.currentTimeMillis() + ".jpg");
                            File appTempDir = new File(Environment.getExternalStorageDirectory().getPath() + "/IPLFramer/Temp/temp");
                            if (!appTempDir.exists()) {
                                File appDir = new File("/sdcard/IPLFramer/Temp");
                                appDir.mkdirs();
                            }
                            File file = new File(appTempDir + dirName);
                            try {

                                imageCapturedURI = Uri.fromFile(file);
                                Log.d("CapturedURI", imageCapturedURI.toString());
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCapturedURI);
                                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                intent.putExtra("return data", true);

                                startActivityForResult(intent, CAMERA_PIC_REQUEST);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void callCroper() {
        Crop.pickImage(this);
    }

    /*
    * saves image to camera gallery
    * */
    private void saveImageToGallery() {
        Dexter.withActivity(this).withPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            View imageView = (ImageView) findViewById(R.id.image_preview);
                            imageView.setDrawingCacheEnabled(true);
                            Bitmap userImage = Bitmap.createBitmap(imageView.getDrawingCache());

                            BitmapDrawable frameImageDrawable = (BitmapDrawable) framer.getDrawable();
                            if (frameImageDrawable != null) {
                                Bitmap selectedFrame = frameImageDrawable.getBitmap();

                                finalImage = createSingleImageFromMultipleImages(userImage, selectedFrame);
                            } else {
                                finalImage = userImage;
                            }
                            //saves image to pictures folder. not using
                            //final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);


                            final String path = savebitmap(finalImage);
                            if (!TextUtils.isEmpty(path)) {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                        .setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                openImage(path);
                                            }
                                        });

                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Unable to save image!", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private String savebitmap(Bitmap finalImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        finalImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String appDirectoryName = "IPL Framer";
        String imageName = System.currentTimeMillis()+".jpg";
        final File imageRoot = new File(Environment.getExternalStorageDirectory(),appDirectoryName);
        imageRoot.mkdirs();
//        File fileToSave = new File(Environment.getExternalStorageDirectory()
//                + File.separator +"IPL Framer"+File.separator+"Saved"+File.separator+ System.currentTimeMillis()+".jpg");
        final File fileToSave = new File(imageRoot,imageName);
        try {
            fileToSave.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(fileToSave);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            fo.write(bytes.toByteArray());
            fo.close();
            url = MediaStore.Images.Media.insertImage(getContentResolver()
                    ,fileToSave.getAbsolutePath(),fileToSave.getName(),fileToSave.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    private void shareImage() {
        {
            Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                BitmapDrawable userImagedrawable = (BitmapDrawable) imagePreview.getDrawable();
                                Bitmap userImage = userImagedrawable.getBitmap();


                                if (framer.getDrawable() == null) {
                                    finalImage = userImage;

                                } else {
                                    BitmapDrawable frameImageDrawable = (BitmapDrawable) framer.getDrawable();
                                    Bitmap selectedFrame = frameImageDrawable.getBitmap();
                                    finalImage = createSingleImageFromMultipleImages(userImage, selectedFrame);
                                }

                                File file = null;
                                if (finalImage != null) {
                                    final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
                                    File dir = new File(dirPath);
                                    if (!dir.exists())
                                        dir.mkdirs();
                                    file = new File(dirPath, finalImage + ".jpg");
                                    try {
                                        FileOutputStream fOut = new FileOutputStream(file);
                                        finalImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                        fOut.flush();
                                        fOut.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                Uri uri = Uri.fromFile(file);
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.setType("image/jpeg");
                                String str = "https://play.google.com/store/apps/details?id=" + getPackageName();
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Find more cool frames of your favorite teams\n\nDownload IPL Photo Framer:\n" + str);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                try {
                                    startActivity(Intent.createChooser(intent, "Share Image"));
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getBaseContext(), "No App Available", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        }
    }

    // opening image in default image viewer app
    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    private class sliderListener implements SeekBar.OnSeekBarChangeListener {
        private int smoothnessFactor = 10;

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progress = Math.round(progress / smoothnessFactor);
            switch (seekBar.getId()) {
                case R.id.seekbar_brightness:
                    imagePreview.setColorFilter(setBrightness(progress));
                    break;
                case R.id.seekbar_contrast:
                    ColorMatrixColorFilter filter = setContrast((float) progress);
                    imagePreview.setColorFilter(filter);
                    break;
                case R.id.seekbar_saturation:
                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(progress);
                    ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
                    imagePreview.setColorFilter(colorMatrixColorFilter);
                    break;
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
//            seekBar.setProgress(Math.round((seekBar.getProgress() + (smoothnessFactor / 2)) / smoothnessFactor) * smoothnessFactor);
        }
    }

    private ColorMatrixColorFilter setContrast(float progress) {
        float scale = progress + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        float[] array = new float[]{
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0};
        ColorMatrix matrix = new ColorMatrix(array);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        return filter;
    }

    public static PorterDuffColorFilter setBrightness(int progress) {
        if (progress > 0) {
            int value = (int) progress * 255 / 100;
            return new PorterDuffColorFilter(Color.argb(value, 255, 255, 255), PorterDuff.Mode.SRC_OVER);
        } else {
            int value = (int) (progress * -1) * 255 / 100;
            return new PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, null, new Rect(0, 0, firstImage.getWidth(), firstImage.getHeight()), new Paint());
        canvas.drawBitmap(secondImage, null, new Rect(0, 0, firstImage.getWidth(), firstImage.getHeight()), new Paint());
        return result;
    }

    private void setCroppedImage(Uri uri) {
        Bitmap imageBitmap = null;
        String imagePath = "";
        imageCapturedURI = uri;
        imagePath = imageCapturedURI.getPath();
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

            IMAGE_NAME = imagePath;
            originalImage = imageBitmap.copy(imageBitmap.getConfig(), true);
            filteredImage = imageBitmap.copy(imageBitmap.getConfig(), true);
            finalImage = imageBitmap.copy(imageBitmap.getConfig(), true);
            imagePreview.setImageBitmap(imageBitmap);


            // render selected image thumbnails
            filtersListFragment.prepareThumbnail(originalImage);
        }
    }
}
