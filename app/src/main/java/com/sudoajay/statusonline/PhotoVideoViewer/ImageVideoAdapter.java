package com.sudoajay.statusonline.PhotoVideoViewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.sudoajay.statusonline.BuildConfig;
import com.sudoajay.statusonline.R;

import java.io.File;
import java.net.URLConnection;
import java.util.List;

public class ImageVideoAdapter extends PagerAdapter {
    private List<String> pathArray;
    private ImageView imageView, video_Play_ImageView;
    private VideoView videoView;
    private LayoutInflater mLayoutInflater;
    private PhotoVideoView activity;
    private String whichFragment;
    private String tabName, fileImgLink;
    private Boolean threadComplete=true;
    private int getCurrentPos;
    private MediaController myMediaController;


    public ImageVideoAdapter(final PhotoVideoView activity, final List<String> pathArray, final String whichFragment,
                             final String tabName, final int getCurrentPos, final String fileImgLink) {
        this.activity = activity;
        this.pathArray = pathArray;
        this.whichFragment = whichFragment;
        this.tabName = tabName;
        this.getCurrentPos = getCurrentPos;
        this.fileImgLink = fileImgLink;
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return pathArray.size();
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.imagevideo_adapter, container, false);

        imageView = itemView.findViewById(R.id.imageView);
        myMediaController = new MediaController(activity);
        String path;
        if (!whichFragment.equals("online")) {
            videoView = itemView.findViewById(R.id.videoView);
            video_Play_ImageView = itemView.findViewById(R.id.video_Play_ImageView);
            File filePath = new File(pathArray.get(position));
            path = filePath.getAbsolutePath();

            if (isImageFile(filePath.getAbsolutePath())) {
                ShowImageView(filePath.getAbsolutePath());
            } else if (isVideoFile(filePath.getAbsolutePath())) {
                ShowVideoView(filePath.getAbsolutePath());
            } else if (filePath.getAbsolutePath().contains(".gif")) {
                ShowImageView(filePath.getAbsolutePath());
            } else {
                ShowImageView(filePath.getAbsolutePath());
                ToastIt(activity.getResources().getString(R.string.sorry_WeDontSupport));
            }
        } else {
            path = pathArray.get(position);

            if (tabName.equals("photo")) {
                videoView = itemView.findViewById(R.id.videoView);
                video_Play_ImageView = itemView.findViewById(R.id.video_Play_ImageView);

                ShowImageView(path);
            } else {
                if (getCurrentPos == position) {
                    videoView = itemView.findViewById(R.id.videoView);
                    video_Play_ImageView = itemView.findViewById(R.id.video_Play_ImageView);
                    ShowVideoView(path);
                    video_Play_ImageView.setVisibility(View.GONE);
                }
            }
        }

        imageView.setOnClickListener(new OnClick());
        video_Play_ImageView.setOnClickListener(new OnClick(path));
        container.addView(itemView);

        return itemView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    public void ShowImageView(final String path) {
        try {
            videoView.setVisibility(View.GONE);
            video_Play_ImageView.setVisibility(View.GONE);


            Glide.with(activity)
                    .asBitmap()
                    .load(path)
                    .centerInside()
                    .placeholder(R.drawable.placeholder_icon)
                    .into(imageView);
        } catch (Exception e) {

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void ShowVideoView(final String path) {
        try {
            // fixed something
            imageView.setVisibility(View.GONE);

            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    SetSystemUi();
                    return false;
                }
            });
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        myMediaController.show();

                }
            });
            if (!whichFragment.equals("online")) {
                videoView.setVideoPath(path);
            } else {
                video_Play_ImageView.setVisibility(View.GONE);
                videoView.setVideoURI(Uri.parse(path));
            }
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    RunThread_Internet(path);
                    return true;
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    if (!whichFragment.equals("online"))
                        mp.setVolume(0, 0);
                    else {
                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                /*
                                 * add media controller
                                 */

                                myMediaController.setMediaPlayer(videoView);
                                videoView.setMediaController(myMediaController);
                                /*
                                 * and set its position on screen
                                 */
                                myMediaController.setAnchorView(videoView);
                            }
                        });
                    }
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    if (!whichFragment.equals("online")) {
                        videoView.setVideoPath(path);
                    } else {
                        videoView.setVideoURI(Uri.parse(path));
                    }
                    videoView.start();
                }
            });

            videoView.requestFocus();
            videoView.start();
        } catch (Exception ignored) {

        }
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    private void ShowVideoThumb(final String filePath) {
        if (!whichFragment.equals("online")) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            Drawable drawable = new BitmapDrawable(activity.getResources(), thumb);
            videoView.setBackground(drawable);
        }else {
            ShowImageView(fileImgLink);
        }
    }

    private void ToastIt(final String mess) {
        Toast.makeText(activity, mess, Toast.LENGTH_SHORT).show();
    }


    public void open_With(File file) {
        try {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(fileExt(file.getAbsolutePath())).substring(1);
            Uri URI = FileProvider.getUriForFile(activity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            newIntent.setDataAndType(URI, mimeType);
            newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            activity.startActivity(newIntent);
        } catch (Exception e) {
            ToastIt("No handler for this type of file.");
        }
    }

    private String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    private void SetSystemUi() {
        if (activity.getWindow().getDecorView().getSystemUiVisibility() == View.SYSTEM_UI_FLAG_VISIBLE) {
            activity.HideStatusNavigation();
        } else {
            activity.ShowStatusNavigation();

        }
    }

    public class OnClick implements View.OnClickListener {

        private String filPath;


        public OnClick(final String filePath) {
            this.filPath = filePath;
        }

        public OnClick() {

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView:
                    SetSystemUi();
                    break;
                case R.id.video_Play_ImageView:

                    open_With(new File(filPath));
//                    videoView.stopPlayback();
//                    videoView.setVideoPath(filPath);
//                    videoView.start();
                    break;
            }
        }
    }

    private void RunThread_Internet(final  String path) {
        if (threadComplete) {
            threadComplete= false;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (!whichFragment.equals("online")) {
                        ToastIt(activity.getResources().getString(R.string.sorry_CantPlayed));
                    }else{
                        ToastIt(activity.getResources().getString(R.string.loading));
                    }
                    ShowVideoThumb(path);
                    threadComplete = true;
                }
            }, 5000);
        }
    }

}