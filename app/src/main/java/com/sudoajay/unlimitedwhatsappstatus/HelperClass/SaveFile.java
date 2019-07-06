package com.sudoajay.unlimitedwhatsappstatus.HelperClass;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.sudoajay.unlimitedwhatsappstatus.R;
import com.sudoajay.unlimitedwhatsappstatus.sharedPreferences.PrefManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveFile {

    private String filePath, aapFolder, tabName,fileName,whichFragment;
    private Activity activity;
    private final String errorMes = "Sorry Can't Process this Please report this";
    private final String existMes = "File Already Saved";

    public SaveFile(final Activity activity, final String filePath,final String fileName, final String tabName, final String whichFragment) {
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


        if (!whichFragment.equalsIgnoreCase("online")) {
            SaveItLocal();           // save the file
        } else {
            SaveItOnline();           // save the file
        }
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

    public void SaveItLocal() {
        InputStream is;
        OutputStream os;
        String getName = new File(filePath).getName();
        try {
            File outputFile = new File(aapFolder + "/" + tabName + "/" + getName);
            if (new File(aapFolder + "/" + tabName).exists()) {
                if (outputFile.createNewFile()) {
                    is = new FileInputStream(new File(filePath));
                    os = new FileOutputStream(outputFile);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                    }
                    ToastIt("File Saved");
                    // Removed the path

                } else {
                    ToastIt(existMes);
                }
            }else {
                ToastIt(errorMes);
            }
            new PrefManager(activity.getApplicationContext()).RemovePath(outputFile.getAbsolutePath());
        } catch (Exception e) {
            if (e.getMessage().equals("write failed: ENOSPC (No space left on device)"))
                ToastIt( "No space left on device");

        }
    }

    private void SaveItOnline() {
        File direct = new File(aapFolder + "/" + tabName + "/");
        String getName = "Status", extension = "jpg";
        try {
            if (direct.exists()) {

                getName = fileName.replace(".", "");

                Log.e("SaveItOnline",filePath );

                // Get extension
                int i = filePath.lastIndexOf('.');
                if (i > 0) {
                    extension = filePath.substring(i);
                }

                final DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

                Uri downloadUri = Uri.parse(filePath);
                DownloadManager.Request request = new DownloadManager.Request(
                        downloadUri);

                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false).setTitle(getName)
                        .setDescription("Downloading WhatsApp Status")
                        .setDestinationInExternalPublicDir("/" + activity.getResources().getString(R.string.app_name) + "/" + tabName + "/", getName + extension);

                downloadManager.enqueue(request);


                BroadcastReceiver receiver = new BroadcastReceiver() {
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

                activity.registerReceiver(receiver, new IntentFilter(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }catch (Exception e){
            if (e.getMessage().equals("write failed: ENOSPC (No space left on device)"))
                ToastIt( "No space left on device");
        }
    }

    private void ToastIt(final String mess){
        Toast.makeText(activity,mess,Toast.LENGTH_SHORT).show();
    }

}
