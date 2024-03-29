package com.sudoajay.statusonline.Fragments.Download;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sudoajay.statusonline.HelperClass.GrabData;
import com.sudoajay.statusonline.MainActivity;
import com.sudoajay.statusonline.R;
import com.sudoajay.statusonline.sharedPreferences.PrefManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private MainActivity main_Activity;
    private View view;
    private RecyclerView recyclerView;
    private Recyclerview_Adapter recyclerview_adapter;
    private List<Long> lastModiArry = new ArrayList<>();
    private GrabData grabData;
    private SwipeRefreshLayout swipeToRefresh;
    private ConstraintLayout nothingToShow_ConstraintsLayout;
    private Set<String> fileRemove;

    public DownloadFragment(MainActivity main_Activity) {
        this.main_Activity=main_Activity;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        nothingToShow_ConstraintsLayout = view.findViewById(R.id.nothingToShow_ConstraintsLayout);
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        swipeToRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        fileRemove = new PrefManager(main_Activity.getApplicationContext()).getFilePath();


        swipeToRefresh.setOnRefreshListener(this);

        OnRefresh();
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), "getreso", Toast.LENGTH_SHORT).show();
    }


    public void LastModiSort() {
        long temp;
        String tem;
        for (int i = 0; i < lastModiArry.size(); i++) {
            for (int j = i + 1; j < lastModiArry.size(); j++) {
                if (lastModiArry.get(i) < lastModiArry.get(j)) {
                    temp = lastModiArry.get(i);
                    lastModiArry.set(i, lastModiArry.get(j));
                    lastModiArry.set(j, temp);

                    tem = grabData.getArrayPath().get(i);
                    grabData.getArrayPath().set(i, grabData.getArrayPath().get(j));
                    grabData.getArrayPath().set(j, tem);
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (grabData != null)
                OnRefresh();

        }
    }
    @Override
    public void onRefresh() {

        swipeToRefresh.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerview_adapter.getFilter().filter("");

                OnRefresh();
                swipeToRefresh.setRefreshing(false);
            }
        }, 2000);

    }

    public void GrabAndFill() {
        lastModiArry.clear();
        if (getActivity() == null) return;

        grabData = new GrabData(getActivity(), getResources().getString(R.string.app_name));
        Log.e("GrabAndFill", "You Here");
        for (String file : fileRemove) {
            grabData.getArrayPath().remove(file);
        }

        for (String arry : grabData.getArrayPath()) {
            lastModiArry.add(new File(arry).lastModified());
        }

        recyclerview_adapter = new Recyclerview_Adapter(main_Activity, grabData.getArrayPath(), DownloadFragment.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(recyclerview_adapter);
        LastModiSort();
    }

    public Recyclerview_Adapter getRecyclerview_adapter() {
        return recyclerview_adapter;
    }

    private void OnRefresh() {
        GrabAndFill();
        if (grabData.getArrayPath().isEmpty()) {
            nothingToShow_ConstraintsLayout.setVisibility(View.VISIBLE);
        } else {
            nothingToShow_ConstraintsLayout.setVisibility(View.GONE);
        }
    }
}
