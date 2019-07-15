package com.sudoajay.statusonline;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sudoajay.statusonline.Fragments.Download.DownloadFragment;
import com.sudoajay.statusonline.Fragments.PhotoVideo.PhotoVideoFragment;

public class MyAdapter extends FragmentPagerAdapter {

    private String[] tabName = {"photo", "video", "download"};
    private int totalTabs;
    private MainActivity mainActivity;
    private PhotoVideoFragment photo, video;
    private DownloadFragment downloadFragment;

    public MyAdapter(MainActivity mainActivity,FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
        this.mainActivity= mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return photo = new PhotoVideoFragment(tabName[0], mainActivity);
            case 1:
                return video = new PhotoVideoFragment(tabName[1], mainActivity);
            case 2:
                return downloadFragment = new DownloadFragment(mainActivity);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public PhotoVideoFragment getPhoto() {
        return photo;
    }

    public PhotoVideoFragment getVideo() {
        return video;
    }

    public DownloadFragment getDownloadFragment() {
        return downloadFragment;
    }
}
