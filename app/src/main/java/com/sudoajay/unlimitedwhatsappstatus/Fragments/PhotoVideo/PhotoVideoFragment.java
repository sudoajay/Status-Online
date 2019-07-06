package com.sudoajay.unlimitedwhatsappstatus.Fragments.PhotoVideo;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.sudoajay.lodinganimation.LoadingAnimation;
import com.sudoajay.unlimitedwhatsappstatus.BackgroundTask.GrabPhotoOnline;
import com.sudoajay.unlimitedwhatsappstatus.BackgroundTask.GrabVideoOnline;
import com.sudoajay.unlimitedwhatsappstatus.DataBase.PhotoDataBase;
import com.sudoajay.unlimitedwhatsappstatus.DataBase.VideoDataBase;
import com.sudoajay.unlimitedwhatsappstatus.HelperClass.CustomToast;
import com.sudoajay.unlimitedwhatsappstatus.R;
import com.sudoajay.unlimitedwhatsappstatus.sharedPreferences.PrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoVideoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private String tabName, searchText = "";
    private RecyclerView mRecyclerView;
    private ConstraintLayout constraintLayout;
    private SwipeRefreshLayout swipeToRefresh;
    private ImageView noInternetConnection_ImageView,noDataFound;
    private PhotoDataBase photoDataBase;
    private VideoDataBase videoDataBase;
    private PhotoVideo_Adapter mPhotoVideoAdapter;
    private PrefManager prefManager;
    private LoadingAnimation loadingAnimation;
    private boolean internetConnection;
    private List<String> links = new ArrayList<>(),name= new ArrayList<>(),imgLink= new ArrayList<>();
    private Activity activity;
    public PhotoVideoFragment(final String tabName, final Activity activity) {
        this.tabName = tabName;
        this.activity= activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_photovideo, container, false);

        Reference();

        // Setup swipeToRefresh
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        swipeToRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        // Run Thread For InternetConnection.
        RunThread_Internet();

        if (!isOnline()) {
            noInternetConnection_ImageView.setVisibility(View.VISIBLE);
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.noInternetConnection));
        } else {
            GrabAndFill();
        }

        swipeToRefresh.setOnRefreshListener(this);


        return view;
    }

    private void Reference() {
        // Get the widgets reference from XML layout
        mRecyclerView = view.findViewById(R.id.recycler_view);
        noInternetConnection_ImageView = view.findViewById(R.id.noInternetConnection_ImageView);
        noDataFound = view.findViewById(R.id.noDataFound);
        photoDataBase = new PhotoDataBase(getContext());
        videoDataBase = new VideoDataBase(getContext());
        prefManager = new PrefManager((activity));
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        constraintLayout= view.findViewById(R.id.constraintLayout);


        // Define a layout for RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialize a new instance of RecyclerView PhotoVideo_Adapter instance
        mPhotoVideoAdapter = new PhotoVideo_Adapter(PhotoVideoFragment.this, tabName , links,name,imgLink);
        // Set the adapter for RecyclerView
        mRecyclerView.setAdapter(mPhotoVideoAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (tabName.equals("photo")) {
                        if (isOnline() && !photoDataBase.isEmpty()) {
                            if (searchText.equals("")) {
                                GrabPhotoAndRefresh();
                            } else {
                                mPhotoVideoAdapter.GrabPhoto(searchText);

                            }

                            mPhotoVideoAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (isOnline() && !videoDataBase.isEmpty()) {
                            if (searchText.equals("")) {
                                GrabVideoAndRefresh();
                            } else {
                                mPhotoVideoAdapter.GrabVideo(searchText);
                            }

                            mPhotoVideoAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void GrabAndFill() {
        links.clear();
        name.clear();
        imgLink.clear();
        if (tabName.equals("photo")) {
            // BackgroundTask
            new GrabPhotoOnline(getActivity());
            if (!prefManager.isPhotoLinkGrab()) {
                loadingAnimation.start();
                RunThread_LoadingAnimationPhoto();
            }
            if (!photoDataBase.isEmpty()) {
                GrabPhotoAndRefresh();
            }
        } else {
            new GrabVideoOnline(getActivity());

            if (!prefManager.isVideoLinkGrab()) {
                loadingAnimation.start();
                RunThread_LoadingAnimationVideo();
            }
            if (!videoDataBase.isEmpty()) {
                GrabVideoAndRefresh();
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

                swipeToRefresh.setRefreshing(false);
            }
        }, 2000);

    }
  private void GrabPhotoAndRefresh(){
      Cursor cursor = photoDataBase.RandomData();
      if (cursor != null) {
          cursor.moveToFirst();
          do {
              if (links.size() >= 500) links = RemoveArrayFromList(links);
              Log.e( "GrabPhotoAndRefresh", links.size()+" ----");
              links.add(cursor.getString(1));
              if (name.size() >= 500) name = RemoveArrayFromList(name);
              name.add(cursor.getString(2));
          } while (cursor.moveToNext());
      }

  }

    private void GrabVideoAndRefresh() {
        Cursor cursor = videoDataBase.RandomData();
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                if (links.size() >= 500) links = RemoveArrayFromList(links);
                links.add(cursor.getString(1));
                if (name.size() >= 500) name = RemoveArrayFromList(name);
                name.add(cursor.getString(2));
                if (imgLink.size() >= 500) imgLink = RemoveArrayFromList(imgLink);
                imgLink.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }

    }

    private void RunThread_LoadingAnimationPhoto() {
        if (photoDataBase.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something...
                    RunThread_LoadingAnimationPhoto();
                }
            }, 1000);
        } else {
            loadingAnimation.stop();
            GrabPhotoAndRefresh();
            mPhotoVideoAdapter.notifyDataSetChanged();
        }

    }

    private void RunThread_LoadingAnimationVideo() {
        if (videoDataBase.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something...
                    RunThread_LoadingAnimationVideo();
                }
            }, 1000);
        } else {
            loadingAnimation.stop();
            GrabVideoAndRefresh();
            mPhotoVideoAdapter.notifyDataSetChanged();
        }

    }

    private void RunThread_Internet() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(getActivity() != null) {
                    if (isOnline()) {
                        internetConnection = true;
                    } else {
                        internetConnection = false;
                    }
                    if (internetConnection && noInternetConnection_ImageView.getVisibility() == View.VISIBLE) {
                        noInternetConnection_ImageView.setVisibility(View.GONE);
                        GrabAndFill();
                    }
                    if (!internetConnection && noInternetConnection_ImageView.getVisibility() == View.GONE)
                        CustomToast.ToastIt(getContext(), "Please Connect To Internet", Toast.LENGTH_SHORT);


                    // do something...
                    RunThread_Internet();
                }
            }
        }, 10000); // 5 sec

    }

    public PhotoVideo_Adapter getmPhotoVideoAdapter() {
        return mPhotoVideoAdapter;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    private List<String> RemoveArrayFromList(final List<String> grab) {
        int offset = 300;
        for (int i = offset - 1; i >= 0; i--) {
            grab.remove(i);
        }
        return grab;
    }
    public void CheckForEmpty(){
        if(links.isEmpty()){
            noDataFound.setVisibility(View.VISIBLE);
        }else {
            noDataFound.setVisibility(View.GONE);
        }
    }
}

