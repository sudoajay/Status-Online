package com.sudoajay.unlimitedwhatsappstatus.Fragments.Download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.sudoajay.unlimitedwhatsappstatus.HelperClass.FileSize;
import com.sudoajay.unlimitedwhatsappstatus.PhotoVideoViewer.PhotoVideoView;
import com.sudoajay.unlimitedwhatsappstatus.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class Information_Data extends DialogFragment implements View.OnClickListener {

    private String path, tabName;
    private View rootview;
    private ArrayList<String> list;
    private int index;
    private ConstraintLayout constraintLayout;
    private TextView infoName_TextView, infoLocation_TextView, infoSize_TextView, infoType_TextView, infoExt_TextView, infoCreated_TextView;
    private Activity activity;

    public Information_Data(final String tabName, final Activity activity, final ArrayList<String> list, final int index) {
        this.path = list.get(index);
        this.list = list;
        this.index = index;
        this.tabName = tabName;
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.custom_dialog_info_layout, container, false);
        Reference();
        // setup dialog box
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
        }
        constraintLayout.setBackgroundColor( getResources().getColor(R.color.tabBackgroundColor));
        // Fill Dialog Box
        FillIt();

        return rootview;
    }

    private void Reference() {
        // Reference Object
        constraintLayout = rootview.findViewById(R.id.constraintLayout);
        infoName_TextView = rootview.findViewById(R.id.infoName_TextView);
        infoLocation_TextView = rootview.findViewById(R.id.infoLocation_TextView);
        infoSize_TextView = rootview.findViewById(R.id.infoSize_TextView);
        infoType_TextView = rootview.findViewById(R.id.infoType_TextView);
        infoExt_TextView = rootview.findViewById(R.id.infoExt_TextView);
        infoCreated_TextView = rootview.findViewById(R.id.infoCreated_TextView);

        ImageView close_ImageView = rootview.findViewById(R.id.close_ImageView);
        close_ImageView.setOnClickListener(this);

        Button cancel_Button = rootview.findViewById(R.id.cancel_Button);
        cancel_Button.setOnClickListener(this);

        Button open_Button = rootview.findViewById(R.id.open_Button);
        open_Button.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void FillIt() {
        File filePath = new File(path);
        infoName_TextView.setText(filePath.getName());

        infoLocation_TextView.setText(filePath.getParent().split(Environment.getExternalStorageDirectory().getAbsolutePath() + "/")[1] + "/");

        infoSize_TextView.setText(FileSize.Convert_It(filePath.length()));

        if (filePath.getAbsolutePath().contains("Unlimited Status/Photo/"))
            infoType_TextView.setText(getResources().getString(R.string.photo_Text));
        else {
            infoType_TextView.setText(getResources().getString(R.string.video_Text));
        }
        String fileName = filePath.getName();
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            infoExt_TextView.setText(fileName.substring(i + 1));
        }


        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy , HH:mm:ss");
        infoCreated_TextView.setText(sdf.format(filePath.lastModified()));

    }

    public void onStart() {
        // This MUST be called first! Otherwise the view tweaking will not be present in the displayed Dialog (most likely overriden)
        super.onStart();

        forceWrapContent(this.getView());
    }

    private void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                current.getLayoutParams().width = width - ((10 * width) / 100);

            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_Button:
                Intent intent = new Intent(activity, PhotoVideoView.class);
                intent.putExtra("WhichTab", tabName);
                intent.putExtra("WhichFragment", "Download");
                intent.putStringArrayListExtra("PathArray", list);
                intent.putExtra("PathArrayPosition", index + "");
                activity.startActivity(intent);
                dismiss();
                break;
            case R.id.cancel_Button:
            case R.id.close_ImageView:
                dismiss();
                break;
        }
    }

}
