package com.sudoajay.statusonline.HelperClass;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.sudoajay.statusonline.Permission.AndroidExternalStoragePermission;
import com.sudoajay.statusonline.R;

import java.io.File;

public class SaveFile {

    private String filePath, aapFolder, tabName,fileName,whichFragment;
    private Activity activity;
    private BroadcastReceiver sendBroadcastReceiver;
    private final String errorMes = "Sorry Can't Process this Please report this";

    public SaveFile(final Activity activity,final String filePath,final String fileName, final String tabName, final String whichFragment) {
        this.filePath = filePath;
        this.activity = activity;
        this.fileName =fileName;
        this.whichFragment = whichFragment;
        if (tabName.equals("photo"))
            this.tabName = "Photo";
        else {
            this.tabName = "Video";
        }

        CheckForFolder(); // check folder and create folder if not present

            SaveItOnline();           // save the file

        AndroidExternalStoragePermission androidExternalStoragePermission = new AndroidExternalStoragePermission(activity,activity);
        if(!androidExternalStoragePermission.isExternalStorageWritable())
            androidExternalStoragePermission.call_Thread();
    }

    private void CheckForFolder() {
        String internalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        aapFolder = internalPath + "/" + activity.getResources().getString(R.string.app_name);
        CreateFolderIfNot(new File(aapFolder)); // app name folder

        CreateFolderIfNot(new File(aapFolder + "/Photo")); // app Photo Folder

        CreateFolderIfNot(new File(aapFolder + "/Video")); // app Video Folder

    }

    private void CreateFolderIfNot(final File path) {

        try {
            if (!path.exists()) if (!path.mkdir()) ToastIt(errorMes);
        } catch (Exception ignored) {
            ToastIt(errorMes);
        }
    }



    private void SaveItOnline() {
        File direct = new File(aapFolder + "/" + tabName + "/");
        String getName = "Status", extension = "jpg";
        try {
            if (direct.exists()) {

                getName = fileName.replace(".", "");


                // Get extension
                int i = filePath.lastIndexOf('.');
                if (i > 0) {
                    extension = filePath.substring(i);
                }

                File outPutFile = new File("/" +
                        activity.getResources().getString(R.string.app_name) + "/" +
                        tabName + "/" +getName+extension);
                final DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

                Uri downloadUri = Uri.parse(filePath);
                DownloadManager.Request request = new DownloadManager.Request(
                        downloadUri);

                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false).setTitle(getName)
                        .setDescription("Downloading WhatsApp Status")
                        .setDestinationInExternalPublicDir("/" +
                                activity.getResources().getString(R.string.app_name) + "/" +
                                tabName + "/",getName+extension);

                downloadManager.enqueue(request);


                 sendBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                            long downloadId = intent.getLongExtra(
                                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(downloadId);
                            Cursor c = downloadManager.query(query);
                            if (c.moveToFirst()) {
                                int columnIndex = c
                                        .getColumnIndex(DownloadManager.COLUMN_STATUS);
                                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                                    CustomToast.ToastIt(context, "File Saved", Toast.LENGTH_LONG);
                                } else if (DownloadManager.STATUS_FAILED == c.getInt(columnIndex)) {
                                    CustomToast.ToastIt(context, "File Not Saved", Toast.LENGTH_LONG);
                                }
                            }
                        }
                    }
                };

                activity.registerReceiver(sendBroadcastReceiver, new IntentFilter(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            new MediaScanner(activity,outPutFile);
            }

        }catch (Exception e){
            if (e.getMessage().equals("write failed: ENOSPC (No space left on device)"))
                ToastIt( "No space left on device");
        }
    }

    private void ToastIt(final String mess){
        Toast.makeText(activity,mess,Toast.LENGTH_SHORT).show();
    }



    public void stopIt() {
        try {
            if (sendBroadcastReceiver != null)
                activity.unregisterReceiver(sendBroadcastReceiver);
        } catch (Exception e) {

        }

    }

}
