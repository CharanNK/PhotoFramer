package com.charanajay.photoframer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.charanajay.photoframer.utils.SpacesItemDecoration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by charank on 13-03-2018.
 */

public class FrameListFragment extends Fragment {
    View view;
    String bitmap;
    InterstitialAd interstitialAd;
    SharedPreferences sharedPreferences;
    public Boolean isAdEnabled;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frames_list, container, false);
        initFrameRecycler();

        sharedPreferences = this.getActivity().getSharedPreferences("ipl_framer", MODE_PRIVATE);
        isAdEnabled = sharedPreferences.getBoolean("isadenabled", false);

        if (Math.random() < 0.5 && isAdEnabled)
            showAd();
        return view;
    }

    public void initFrameRecycler() {
        Log.d("FrameRecycler", "initFrameRecycler:called");

        ArrayList<Integer> frames = new ArrayList<Integer>();

        frames.add(R.drawable.rcb_1);
        frames.add(R.drawable.rcb_2);
        frames.add(R.drawable.rcb_3);
        frames.add(R.drawable.rcb_4);
        frames.add(R.drawable.rcb_5);
        frames.add(R.drawable.rcb_6);
        frames.add(R.drawable.rcb_7);
        frames.add(R.drawable.rcb_8);
        frames.add(R.drawable.rcb_9);
        frames.add(R.drawable.rcb_10);

        frames.add(R.drawable.csk_1);
        frames.add(R.drawable.csk_2);
        frames.add(R.drawable.csk_3);
        frames.add(R.drawable.csk_4);
        frames.add(R.drawable.csk_6);
        frames.add(R.drawable.csk_7);
        frames.add(R.drawable.csk_8);

        frames.add(R.drawable.mi_1);
        frames.add(R.drawable.mi_2);
        frames.add(R.drawable.mi_3);
        frames.add(R.drawable.mi_4);
        frames.add(R.drawable.mi_5);
        frames.add(R.drawable.mi_6);
        frames.add(R.drawable.mi_7);

        frames.add(R.drawable.kkr_1);
        frames.add(R.drawable.kkr_2);
        frames.add(R.drawable.kkr_3);
        frames.add(R.drawable.kkr_4);
        frames.add(R.drawable.kkr_5);
        frames.add(R.drawable.kkr_6);

        frames.add(R.drawable.dd_1);
        frames.add(R.drawable.dd_2);
        frames.add(R.drawable.dd_3);
        frames.add(R.drawable.dd_4);
        frames.add(R.drawable.dd_5);
        frames.add(R.drawable.dd_6);

        frames.add(R.drawable.kingsxi_1);
        frames.add(R.drawable.kingsxi_2);
        frames.add(R.drawable.kingsxi_3);
        frames.add(R.drawable.kingsxi_4);
        frames.add(R.drawable.kingsxi_5);

        frames.add(R.drawable.rajastan_1);
        frames.add(R.drawable.rajastan_2);
        frames.add(R.drawable.rajastan_3);
        frames.add(R.drawable.rajastan_4);
        frames.add(R.drawable.rajastan_5);

        frames.add(R.drawable.sunrisers_1);
        frames.add(R.drawable.sunrisers_2);
        frames.add(R.drawable.sunrisers_3);
        frames.add(R.drawable.sunrisers_4);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.frames_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        FramesAdapter framesAdapter = new FramesAdapter(frames, getContext());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(framesAdapter);

    }

    public void showAd() {
        interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(getString(R.string.non_vide_addid));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (interstitialAd.isLoaded())
                    interstitialAd.show();
            }
        }, 10000);
    }
}
