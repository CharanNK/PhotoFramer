package com.charanajay.photoframer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

    private ArrayList<String> frameNames = new ArrayList<String>();
    private ArrayList<Integer> frames = new ArrayList<Integer>();
    private Context mContext;

    ImageView imagePreview;

    public FramesAdapter(ArrayList<String> frameNames, ArrayList<Integer> frames, Context mContext) {
        this.frameNames = frameNames;
        this.frames = frames;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder:called");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_list_item,parent,false);
        imagePreview = ((Activity)mContext).findViewById(R.id.image_preview);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder:called");

        holder.frameThumbnail.setImageResource(frames.get(position));

        holder.frameName.setText(frameNames.get(position));
        holder.frameThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onclick:clicked on an image"+frameNames.get(position));
                Toast.makeText(mContext,frameNames.get(position),Toast.LENGTH_LONG).show();



                Bitmap bigImage = ((BitmapDrawable)imagePreview.getDrawable()).getBitmap();
                Bitmap smallImage = BitmapFactory.decodeResource(mContext.getResources(), frames.get(position));
                Bitmap mergedImages = createSingleImageFromMultipleImages(bigImage, smallImage);

                imagePreview.setImageBitmap(mergedImages);
            }
        });
    }

    @Override
    public int getItemCount() {
        return frameNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView frameName;
        ImageView frameThumbnail;
        public ViewHolder(View itemView) {
            super(itemView);

            frameName = itemView.findViewById(R.id.filter_name);
            frameThumbnail = itemView.findViewById(R.id.thumbnail);

        }
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){

        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        int x = canvas.getWidth()-secondImage.getWidth();
        int y = canvas.getWidth()-secondImage.getHeight();
        canvas.drawBitmap(secondImage, x, y, null);
        return result;
    }
}
