package com.charanajay.photoframer;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.charanajay.photoframer.utils.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by charank on 13-03-2018.
 */

public class FrameListFragment extends Fragment {
    View view;
    String bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frames_list, container, false);
        initFrameRecycler();
        return view;
    }

    public void initFrameRecycler() {
        Log.d("FrameRecycler", "initFrameRecycler:called");

        ArrayList<Integer> frames = new ArrayList<Integer>();

        frames.add(R.drawable.rcbbasic);
        frames.add(R.drawable.cupnamde);
        frames.add(R.drawable.rcbplaybold);
        frames.add(R.drawable.cskdhoni);
        frames.add(R.drawable.cskback);
        frames.add(R.drawable.mumbai_indians);
        frames.add(R.drawable.cskwhistle);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.frames_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        FramesAdapter framesAdapter = new FramesAdapter(frames, getContext());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(framesAdapter);

    }
}
