package com.sudoajay.statusonline.PhotoVideoViewer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.sudoajay.statusonline.HelperClass.Delete;
import com.sudoajay.statusonline.HelperClass.MediaScanner;
import com.sudoajay.statusonline.HelperClass.SaveFile;
import com.sudoajay.statusonline.MainActivity;
import com.sudoajay.statusonline.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoVideoView extends AppCompatActivity {
    private ImageView save_ImageView, share_ImageView;
    private ConstraintLayout header_ConstraintLayout, bottom_ConstraintLayout;
    private String tabName, whichFragment,pathArrayPosition,filePath, fileName,fileImgLink;
    private TextView save_TextView, file_Name_TextView, share_TextView;
    private ViewPager viewPager;
    private SaveFile saveFile;
    private List<String> pathArray, pathName,imgLink;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_video_view);

        fileName = "";
        pathName = new ArrayList<>();
        imgLink = new ArrayList<>();
        Intent intent = getIntent();
        if(intent != null){
            tabName = intent.getStringExtra("WhichTab");
            pathArrayPosition = intent.getStringExtra("PathArrayPosition");
            whichFragment = intent.getStringExtra("WhichFragment");
            pathArray = intent.getStringArrayListExtra("PathArray");
            filePath = pathArray.get(Integer.parseInt(pathArrayPosition));
            if (whichFragment.equals("online")) {
                pathName = intent.getStringArrayListExtra("PathName");
                fileName = pathName.get(Integer.parseInt(pathArrayPosition));
                if(tabName.equals("video")){
                    imgLink  = intent.getStringArrayListExtra("ImageLink");
                    fileImgLink = imgLink.get(Integer.parseInt(pathArrayPosition));
                }
            }
        }

        Reference();


        // setup which Fragment
        setupWhichFragement();

        // At First Hide It
        HideStatusNavigation();

        final ImageVideoAdapter adapter = new ImageVideoAdapter(PhotoVideoView.this, pathArray, whichFragment, tabName
        ,Integer.parseInt(pathArrayPosition),fileImgLink);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(Integer.parseInt(pathArrayPosition));
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setOffscreenPageLimit(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!whichFragment.equals("online"))
                    file_Name_TextView.setText(new File(pathArray.get(position)).getName());
                else {
                    file_Name_TextView.setText(pathName.get(position));
                }
                filePath = pathArray.get(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (whichFragment.equals("online") && tabName.equals("video"))
                    return true;
                return false;
            }
        });


    }
    private void Reference(){
        header_ConstraintLayout = findViewById(R.id.header_ConstraintLayout);
        bottom_ConstraintLayout = findViewById(R.id.bottom_ConstraintLayout);
        file_Name_TextView = findViewById(R.id.file_Name_TextView);
        save_ImageView = findViewById(R.id.save_delete_ImageView);
        save_TextView = findViewById(R.id.save_delete_TextView);
        viewPager = findViewById(R.id.viewPager);
        share_ImageView = findViewById(R.id.share_ImageView);
        share_TextView = findViewById(R.id.share_TextView);


        if (!whichFragment.equals("online"))
            file_Name_TextView.setText(new File(filePath).getName());
        else {
            share_ImageView.setAlpha(0.3f);
            share_TextView.setAlpha(0.3f);
            file_Name_TextView.setText(fileName);
        }

    }


    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.constraintLayout:
            case R.id.viewPager:
                if (getWindow().getDecorView().getSystemUiVisibility() == View.SYSTEM_UI_FLAG_VISIBLE) {
                    HideStatusNavigation();
                } else {
                    ShowStatusNavigation();
                }
                break;
            case R.id.share_TextView:
            case R.id.share_ImageView:
                ShareIt();
                break;
            case R.id.save_delete_ImageView:
            case R.id.save_delete_TextView:
                if (whichFragment.equalsIgnoreCase("local") || whichFragment.equalsIgnoreCase("online")) {
                    saveFile = new SaveFile(PhotoVideoView.this, filePath, fileName, tabName, whichFragment);
                }
                else {
                    new Delete().DeleteTheData(filePath);
                    new MediaScanner(PhotoVideoView.this, new File(filePath));
                    ToastIt("File Deleted");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("WhichFragment","Download");
                    startActivity(intent);
                }
                break;
            case R.id.back_Arrow_ImageView:
                onBackPressed();
                break;
        }

    }

    //    Hide Status
    @SuppressLint("ResourceType")
    public void HideStatusNavigation() {
        View decorView = getWindow().getDecorView();
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;

        }
        decorView.setSystemUiVisibility(uiOptions);


        header_ConstraintLayout.setVisibility(View.INVISIBLE);
        bottom_ConstraintLayout.setVisibility(View.INVISIBLE);

    }

    //    Show Status
    public void ShowStatusNavigation() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        header_ConstraintLayout.setVisibility(View.VISIBLE);
        bottom_ConstraintLayout.setVisibility(View.VISIBLE);

    }

    private void ShareIt() {
        if (!whichFragment.equals("online")) {
            if (tabName.equals("photo")) {
                Intent shareIntent;
                Uri bmpUri = Uri.parse("file://" + filePath);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            } else {
                Intent shareIntent;
                Uri bmpUri = Uri.parse("file://" + filePath);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                shareIntent.setType("video/*");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            }
        } else {

            ToastIt(getResources().getString(R.string.cant_OnlineShare));
        }

    }

    private void setupWhichFragement() {
        if (whichFragment.equalsIgnoreCase("Download")) {
            save_ImageView.setImageResource(R.drawable.delete_icon);
            save_TextView.setText(getResources().getString(R.string.delete_Text));
        }
    }
    private void ToastIt(final String mess){
        Toast.makeText(getApplicationContext(),mess,Toast.LENGTH_SHORT).show();
    }

    public void setFile_Name_TextView(final String name) {
        this.file_Name_TextView.setText(name);
    }


    @Override
    protected void onStop() {
        if (saveFile != null)
            saveFile.stopIt();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (saveFile != null)
            saveFile.stopIt();
        super.onDestroy();
    }
}