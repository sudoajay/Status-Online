package com.sudoajay.statusonline.HelperClass;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import com.sudoajay.statusonline.Permission.AndroidExternalStoragePermission;

import java.io.File;
import java.util.ArrayList;

public class GrabData {

    private String path;
    private ArrayList<String> arrayPath;
    private Activity activity;
    private final String errorMes = "Sorry Can't Process this Please report this";

    public GrabData(final Activity activity, final String path) {
        this.path = Environment.getExternalStorageDirectory().getAbsoluteFile() +"/"+ path;
        this.activity = activity;
        arrayPath = new ArrayList<>();
        getData(new File(this.path));
    }

    private void getData(final File filesPath) {
        try {
            if (filesPath.exists() && filesPath.list().length != 0) {
                for (File file : filesPath.listFiles()) {
                    if (file.isDirectory()) getData(file);
                    else {
                            arrayPath.add(file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            AndroidExternalStoragePermission androidExternalStoragePermission = new AndroidExternalStoragePermission(activity,activity);
            if(!androidExternalStoragePermission.isExternalStorageWritable())
                androidExternalStoragePermission.call_Thread();
            CustomToast.ToastIt(activity, errorMes, Toast.LENGTH_LONG);
        }
    }

    public ArrayList<String> getArrayPath() {
        return arrayPath;
    }
}
