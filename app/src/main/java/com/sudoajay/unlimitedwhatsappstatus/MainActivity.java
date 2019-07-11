package com.sudoajay.unlimitedwhatsappstatus;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sudoajay.unlimitedwhatsappstatus.BackgroundTask.GrabPhotoOnline;
import com.sudoajay.unlimitedwhatsappstatus.BackgroundTask.GrabVideoOnline;
import com.sudoajay.unlimitedwhatsappstatus.Fragments.Download.Information_Data;
import com.sudoajay.unlimitedwhatsappstatus.Permission.AndroidExternalStoragePermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Global Variable
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean doubleBackToExitPressedOnce;
    private String whichFragment;
    private SearchView searchView;
    private MyAdapter adapter;
    private ActionMode actionMode;
    private boolean fetchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Reference();

//        Intent intent = getIntent();
//        if(intent.getStringExtra("WhichFragment") != null){
//            whichFragment = intent.getStringExtra("WhichFragment");
//        }
        // set up External storage Permission
        AndroidExternalStoragePermission androidExternalStoragePermission = new AndroidExternalStoragePermission(
                MainActivity.this, MainActivity.this);
        androidExternalStoragePermission.call_Thread();
        Reference();


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        final ColorStateList tabSlected, tabUnselected;
        if (Build.VERSION.SDK_INT >= 23) {
            tabSlected = getResources().getColorStateList(R.color.colorPrimary, getTheme());
            tabUnselected = getResources().getColorStateList(R.color.tabColor, getTheme());
        } else {
            tabSlected = getResources().getColorStateList(R.color.colorPrimary);
            tabUnselected = getResources().getColorStateList(R.color.tabColor);
        }


        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            Drawable icon = tab.getIcon();

            if (tabLayout.getSelectedTabPosition() == i) {
                assert icon != null;
                DrawableCompat.setTintList(icon, tabSlected);
            }
            else {
                assert icon != null;
                DrawableCompat.setTintList(icon, tabUnselected);
            }

        }


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Drawable icon = tab.getIcon();
                assert icon != null;
                DrawableCompat.setTintList(icon, tabSlected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Drawable icon = tab.getIcon();
                assert icon != null;
                DrawableCompat.setTintList(icon, tabUnselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        RunThread();
    }

    private void Reference() {
        tabLayout = findViewById(R.id.localTab);
        viewPager = findViewById(R.id.viewPager_Local);
    }


    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            Finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, " Click Back Again To Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void Finish() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();


        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (tabLayout.getSelectedTabPosition() == 0) {
                    adapter.getPhoto().setSearchText(query);
                    adapter.getPhoto().getmPhotoVideoAdapter().getFilter().filter(query);
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    adapter.getVideo().setSearchText(query);
                    adapter.getVideo().getmPhotoVideoAdapter().getFilter().filter(query);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (tabLayout.getSelectedTabPosition() == 2) {
                    adapter.getDownloadFragment().getRecyclerview_adapter().getFilter().filter(newText);
                }
                return false;
            }
        });
        return true;
    }

    private void RunThread() {

        if (isOnline()) {
            new GrabPhotoOnline(this);
            new GrabVideoOnline(this);
            fetchData = true;
        }
        if (!fetchData) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RunThread();
                }
            }, 5000); // 5 sec
        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public void CallInfo_CustomDialog(final String tabName, final ArrayList<String> list, final int index) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Information_Data information_data = new Information_Data(tabName, MainActivity.this,list,index);
        information_data.show(ft, "dialog");
    }
    public ActionMode getActionMode() {
        return actionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }
}
