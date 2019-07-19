package com.sudoajay.statusonline.sharedPreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.sudoajay.statusonline.R;

import java.util.HashSet;
import java.util.Set;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private Set<String> filePath;


    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        this.context = context;
        // shared pref mode
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(context.getString(R.string.MY_PREFS_NAME), PRIVATE_MODE);
        editor = pref.edit();
        filePath = new HashSet<>();
    }

    public void setFilePath(final Set<String> file) {
        filePath = pref.getStringSet(context.getString(R.string.path_removed), new HashSet<String>());
        for (String path : file) {
            assert filePath != null;
            filePath.add(path);
        }
        editor.putStringSet(context.getString(R.string.path_removed), filePath);
        editor.apply();
    }

    public Set<String> getFilePath() {
        return pref.getStringSet(context.getString(R.string.path_removed),  new HashSet<String>());
    }

    public void RemovePath(final String path) {
        filePath = pref.getStringSet(context.getString(R.string.path_removed),  new HashSet<String>());
        assert filePath != null;
        for (String file : filePath) {
            if (file.equalsIgnoreCase(path)) {
                filePath.remove(file);
                break;
            }
        }
        editor.putStringSet(context.getString(R.string.path_removed), filePath);
        editor.apply();
    }

    public boolean isPhotoLinkGrab() {
        return pref.getBoolean(context.getString(R.string.photo_link_grab),false);
    }

    public void setPhotoLinkGrab(final boolean isLinkGrab) {
        editor.putBoolean(context.getString(R.string.photo_link_grab), isLinkGrab);
        editor.apply();
    }
    public boolean isVideoLinkGrab() {
        return pref.getBoolean(context.getString(R.string.video_link_grab),false);
    }

    public void setVideoLinkGrab(final boolean isLinkGrab) {
        editor.putBoolean(context.getString(R.string.video_link_grab), isLinkGrab);
        editor.apply();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(context.getString(R.string.isFirstTimeLaunch), isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(context.getString(R.string.isFirstTimeLaunch), true);
    }
}
