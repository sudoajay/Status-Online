package com.sudoajay.unlimitedwhatsappstatus.BackgroundTask;

import android.app.Activity;
import android.database.Cursor;

import com.sudoajay.unlimitedwhatsappstatus.DataBase.PhotoDataBase;
import com.sudoajay.unlimitedwhatsappstatus.DataBase.PhotoLinkDatabase;
import com.sudoajay.unlimitedwhatsappstatus.sharedPreferences.PrefManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrabPhotoOnline {
    private PhotoDataBase photoDataBase;
    private PrefManager prefManager;
    private final String logoImg="http://www.latestseotutorial.com/wp-content/uploads/2018/12/LOGO-JI.png";
    private PhotoLinkDatabase photoLinkDatabase;
    private List<String> site = new ArrayList<>();
    private Set<String> photoUrl = new HashSet<>();
    public GrabPhotoOnline(final Activity activity) {

        photoDataBase = new PhotoDataBase(activity);
        photoLinkDatabase = new PhotoLinkDatabase(activity);
        prefManager = new PrefManager(activity);
        site.add("http://www.latestseotutorial.com/");
        site.add("http://www.sarkarinaukrisearch.in/");
        site.add("https://videosongstatus.com");

        runThread();
    }

    public void runThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!prefManager.isPhotoLinkGrab()) {
                    // Add latestseotutorial web
                    LinkStoreToDatase(site.get(0));
                    // Add sarkarinaukrisearch
                    LinkStoreToDatase(site.get(1));
                    // Add videosongstatus
                    LinkStoreToDatasevideosongstatus();
                }

//                // Add To photo Database
                AddSites();
            }
        }).start();
    }

    private void LinkStoreToDatase(final String url) {
        try {
            String saveDocument = null;
            StringBuilder saveDocument1 = new StringBuilder();
            try {
                saveDocument = getHtml(url);
            } catch (IOException e) {
            }

            String expression = "class=\"widget-title\">[\\s\\S]+?</div>";
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(saveDocument);
            while (matcher.find()) {
                saveDocument1.append(matcher.group(0));
            }

            String expression1 = "http:[\\S]+/";
            Pattern pattern1 = Pattern.compile(expression1);
            Matcher matcher1 = pattern1.matcher(saveDocument1);
            while (matcher1.find()) {
                photoLinkDatabase.Fill_It(matcher1.group(0), 0);
            }
        } catch (Exception e) {

        }
    }

    private void LinkStoreToDatasevideosongstatus() {
        try {
            String saveDocument = null;
            try {
                saveDocument = getHtml("https://videosongstatus.com/quotes/");
            } catch (IOException e) {
            }

            String expression = "author[^>]+>+[^>]+";
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(saveDocument);
            while (matcher.find()) {
                expression = "href=\"[^\"]+";
                pattern = Pattern.compile(expression);
                Matcher matcher1 = pattern.matcher(matcher.group(0));
                if (matcher1.find()) {
                    String url = site.get(2) + matcher1.group(0).substring(6);
                    photoLinkDatabase.Fill_It(url, 0);
                    try {
                        saveDocument = getHtml(url);
                    } catch (IOException e) {
                    }

                    expression = "Page\\s1\"[^S]+next";
                    pattern = Pattern.compile(expression);
                    Matcher matcher2 = pattern.matcher(saveDocument);
                    if (matcher2.find()) {
                        expression = "href=\"[^javascript]+[^\"]+";
                        pattern = Pattern.compile(expression);
                        Matcher matcher3 = pattern.matcher(matcher2.group(0));
                        while (matcher3.find()) {
                            photoUrl.add(site.get(2) + matcher2.group(0).substring(6));
                        }
                    }
                }
            }

            saveDocument = null;
            try {
                saveDocument = getHtml("https://videosongstatus.com/blog-post/");
            } catch (IOException e) {
            }

            expression = "<a\\shref=\"[^\"]+\"\\sclass";
            pattern = Pattern.compile(expression);
            matcher = pattern.matcher(saveDocument);
            while (matcher.find()) {
                photoUrl.add(site.get(2) + matcher.group(0).replace("\" class", "").substring(9));
            }
            for (String get : photoUrl) {
                photoLinkDatabase.Fill_It(get, 0);
            }
            prefManager.setPhotoLinkGrab(true);
        } catch (Exception ignored) {

        }

    }

    private void AddSites() {
        try {
            String saveDocument2 = null;
            if (!photoLinkDatabase.isEmpty()) {
                Cursor cursor = photoLinkDatabase.getLink();
                if (cursor != null) {
                    cursor.moveToFirst();
                    do {
                        try {
                            saveDocument2 = getHtml(cursor.getString(1));
                        } catch (Exception e) {

                        }
                        if(!cursor.getString(1).startsWith(site.get(2))) {
                            String expression2 = "src=[\\S]+.(jpg|png|gif)";
                            Pattern pattern2 = Pattern.compile(expression2);
                            Matcher matcher2 = pattern2.matcher(saveDocument2);

                            while (matcher2.find()) {
                                String link = matcher2.group(0).substring(5);
                                if (!link.equals(logoImg)) {
                                    String expression3 = "/\\d\\d/[\\S]+\\.";
                                    Pattern pattern3 = Pattern.compile(expression3);
                                    Matcher matcher3 = pattern3.matcher(link);
                                    if (matcher3.find()) {
                                        String get = matcher3.group(0).substring(4).replaceAll("-", " ");
                                        photoDataBase.FillIt(link, get.substring(0, get.length() - 1).trim() + "");
                                    }
                                }
                            }
                        }else {
                            String expression2 = "src=[\\S]+.(jpg)";
                            Pattern pattern2 = Pattern.compile(expression2);
                            Matcher matcher2 = pattern2.matcher(saveDocument2);
                            while (matcher2.find()) {
                                String link = matcher2.group(0).substring(5);
                                String expression3 = "images[^.]+";
                                Pattern pattern3 = Pattern.compile(expression3);
                                Matcher matcher3 = pattern3.matcher(link);
                                if (matcher3.find()) {
                                    String name= matcher3.group(0).replace("images","").replaceAll("/"," ");
                                    photoDataBase.FillIt(link, name.trim());
                                }
                            }

                        }
                        // update The Table
                        photoLinkDatabase.UpdateTheDoneColumn(cursor.getString(0), 1);

                    } while (cursor.moveToNext());
                }
            }
        }catch (Exception e){
        }
    }

    private static String getHtml(String url) throws IOException {
        // Build and set timeout values for the request.
        URLConnection connection = (new URL(url)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();

        // Read and store the result line by line then return the entire string.
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }
}
