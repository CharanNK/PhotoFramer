package com.charanajay.photoframer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by charank on 14-03-2018.
 */

public class FramesAdapter extends RecyclerView.Adapter<FramesAdapter.ViewHolder> {

    private static final String TAG = "FramesAdapterRecycler";

    private ArrayList<Integer> frames = new ArrayList<Integer>();
    private Context mContext;

    ImageView imagePreview,framer;

    public FramesAdapter( ArrayList<Integer> frames, Context mContext) {
        this.frames = frames;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder:called");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frames_list_item,parent,false);
        imagePreview = ((Activity)mContext).findViewById(R.id.image_preview);
        framer = ((Activity)mContext).findViewById(R.id.framer);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder:called");

        Bitmap imgscale = decodeSampledBitmapFromResource(frames.get(position),100,100,mContext);
        holder.frameThumbnail.setImageBitmap(imgscale);

        holder.frameThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onclick:clicked on an image"+frames.get(position));

                Bitmap frameBitmap = decodeSampledBitmapFromResource(frames.get(position),800,800,mContext);

                framer.setImageBitmap(frameBitmap);

            }
        });
    }

    @Override
    public int getItemCount() {
        return frames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView frameThumbnail;
        public ViewHolder(View itemView) {
            super(itemView);

            frameThumbnail = itemView.findViewById(R.id.frame_thumbnail);

        }
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
//        canvas.drawBitmap(firstImage, 0f, 0f, null);
//        int x = canvas.getWidth()-secondImage.getWidth();
//        int y = canvas.getHeight()-secondImage.getHeight();
//        canvas.drawBitmap(secondImage, x, y, null);
        canvas.drawBitmap(firstImage, null, new Rect(0,0,firstImage.getWidth(),firstImage.getHeight()), new Paint());
        canvas.drawBitmap(secondImage, null, new Rect(0,0,firstImage.getWidth(),firstImage.getHeight()), new Paint());
        return result;
    }

    public static Bitmap decodeSampledBitmapFromResource(int resId, int reqWidth, int reqHeight,Context context) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeResource(context.getResources(),resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
// Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 3;
            final int halfWidth = width / 3;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
