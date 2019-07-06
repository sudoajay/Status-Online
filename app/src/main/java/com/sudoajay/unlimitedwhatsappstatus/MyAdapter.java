package com.sudoajay.unlimitedwhatsappstatus;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sudoajay.unlimitedwhatsappstatus.Fragments.Download.DownloadFragment;
import com.sudoajay.unlimitedwhatsappstatus.Fragments.PhotoVideo.PhotoVideoFragment;

public class MyAdapter extends FragmentPagerAdapter {

    private String[] tabName = {"photo", "video", "download"};
    private int totalTabs;
    private MainActivity mainActivity;

    public MyAdapter(MainActivity mainActivity,FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
        this.mainActivity= mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PhotoVideoFragment(tabName[0],mainActivity);
            case 1:
                return new PhotoVideoFragment(tabName[1],mainActivity);
            case 2:
                return new DownloadFragment(mainActivity);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
