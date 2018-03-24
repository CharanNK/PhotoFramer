package com.charanajay.photoframer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.charanajay.photoframer.utils.SpacesItemDecoration;

import java.util.ArrayList;

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

        frames.add(R.drawable.rcb_basic);
        frames.add(R.drawable.rcb_cupnamde);
        frames.add(R.drawable.rcb_playbold);
        frames.add(R.drawable.csk_dhoni);
        frames.add(R.drawable.csk_kingsback);
        //frames.add(R.drawable.csk_whistle);
        frames.add(R.drawable.csk_roarformore);
        frames.add(R.drawable.mi_basic);
        frames.add(R.drawable.mi_dilse);
        frames.add(R.drawable.mi_blue);
        frames.add(R.drawable.kkr_basic);
        frames.add(R.drawable.kkr_iamkkr);
        frames.add(R.drawable.kkr_korbo);
        frames.add(R.drawable.kkr_amikkr);
        frames.add(R.drawable.dd_basic);
        frames.add(R.drawable.kkr_reduced);
        frames.add(R.drawable.kingsxi_portrait);
        frames.add(R.drawable.kingsxi_panga);
        //frames.add(R.drawable.kingsxi_red);
        frames.add(R.drawable.rajastan_hallabol);
        frames.add(R.drawable.rajastan_gradient);
        frames.add(R.drawable.rajastan_portrait);
        frames.add(R.drawable.sunrisers_orange);
        frames.add(R.drawable.sunrisers_basic);

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
