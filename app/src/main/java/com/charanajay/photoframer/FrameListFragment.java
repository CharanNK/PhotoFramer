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

        frames.add(R.drawable.rcb_support);
        frames.add(R.drawable.rcb_basic);
        frames.add(R.drawable.rcb_cupnamde);
        frames.add(R.drawable.rcb_playbold);
        frames.add(R.drawable.rcb_namma_rcb);
        frames.add(R.drawable.rcb_sidekannada);
        frames.add(R.drawable.rcb_sideenglish);
        frames.add(R.drawable.rcb_playbold_test);
        frames.add(R.drawable.rcb_bangalore_kan);
        frames.add(R.drawable.rcb_kohli);
        frames.add(R.drawable.csk_isupport);
        frames.add(R.drawable.csk_dhoni);
        frames.add(R.drawable.csk_kingsback);
        frames.add(R.drawable.csk_whistlepodu);
        frames.add(R.drawable.csk_roarformore);
        frames.add(R.drawable.csk_squad);
        frames.add(R.drawable.csk_one);
        frames.add(R.drawable.csk_two);
        frames.add(R.drawable.mi_isupport);
        frames.add(R.drawable.mi_basic);
        frames.add(R.drawable.mi_dilse);
        frames.add(R.drawable.mi_blue);
        frames.add(R.drawable.mi_squad);
        frames.add(R.drawable.mi_one);
        frames.add(R.drawable.mi_two);
        frames.add(R.drawable.kkr_isupport);
        frames.add(R.drawable.kkr_basic);
        frames.add(R.drawable.kkr_iamkkr);
        frames.add(R.drawable.kkr_korbo);
        frames.add(R.drawable.kkr_amikkr);
        frames.add(R.drawable.kkr_taiyaar);
        frames.add(R.drawable.delhi_isupport);
        frames.add(R.drawable.dd_basic);
        frames.add(R.drawable.dd_players);
        frames.add(R.drawable.dd_dildelhi);
        frames.add(R.drawable.dd_playerstwo);
        frames.add(R.drawable.dd_one);
        frames.add(R.drawable.kingsxi_isupport);
        frames.add(R.drawable.kingsxi_portrait);
        frames.add(R.drawable.kingsxi_panga);
        frames.add(R.drawable.kingsxi_saddi);
        frames.add(R.drawable.kingsxi_zinta);
        frames.add(R.drawable.rajastan_isupport);
        frames.add(R.drawable.rajastan_hallabol);
        frames.add(R.drawable.rajastan_gradient);
        frames.add(R.drawable.rajastan_portrait);
        frames.add(R.drawable.rajastan_shilpa);
        frames.add(R.drawable.srh_isupport);
        frames.add(R.drawable.sunrisers_orange);
        frames.add(R.drawable.sunrisers_basic);
        frames.add(R.drawable.srh_orange_army);

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
