package com.sudoajay.statusonline.HelperClass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
;

import java.io.File;

public class MediaScanner {

    private File mFile;
    private Activity activity;

    public MediaScanner(Activity activity, File f) {
        mFile = f;
        this.activity = activity;
        galleryAddPic();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(mFile);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }


}