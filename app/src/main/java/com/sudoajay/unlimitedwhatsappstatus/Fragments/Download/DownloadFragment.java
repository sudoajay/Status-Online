package com.sudoajay.unlimitedwhatsappstatus.Fragments.Download;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sudoajay.unlimitedwhatsappstatus.HelperClass.GrabData;
import com.sudoajay.unlimitedwhatsappstatus.MainActivity;
import com.sudoajay.unlimitedwhatsappstatus.R;
import com.sudoajay.unlimitedwhatsappstatus.sharedPreferences.PrefManager;

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
        GrabAndFill();


        recyclerview_adapter = new Recyclerview_Adapter(main_Activity, grabData.getArrayPath(), DownloadFragment.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(recyclerview_adapter);
        // if there is no data
        if (grabData.getArrayPath().isEmpty()) {
            nothingToShow_ConstraintsLayout.setVisibility(View.VISIBLE);
        } else {
            nothingToShow_ConstraintsLayout.setVisibility(View.GONE);
        }

        swipeToRefresh.setOnRefreshListener(this);

        // Inflate the layout for this fragment
        return view;
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
    public void onRefresh() {

        swipeToRefresh.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GrabAndFill();
                recyclerview_adapter.notifyDataSetChanged();
                swipeToRefresh.setRefreshing(false);
            }
        }, 2000);

    }

    public void GrabAndFill() {
        lastModiArry.clear();
        if (getActivity() == null) return;

        grabData = new GrabData(getActivity(), getResources().getString(R.string.app_name));

        for (String file : fileRemove) {
            grabData.getArrayPath().remove(file);
        }

        for (String arry : grabData.getArrayPath()) {
            lastModiArry.add(new File(arry).lastModified());
        }
        LastModiSort();



    }

    public Recyclerview_Adapter getRecyclerview_adapter() {
        return recyclerview_adapter;
    }
}
