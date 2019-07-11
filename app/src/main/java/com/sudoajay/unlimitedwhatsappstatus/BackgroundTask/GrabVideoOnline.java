package com.sudoajay.unlimitedwhatsappstatus.BackgroundTask;

import android.content.Context;
import android.database.Cursor;

import com.sudoajay.unlimitedwhatsappstatus.DataBase.VideoDataBase;
import com.sudoajay.unlimitedwhatsappstatus.DataBase.VideoLinkDatabase;
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

public class GrabVideoOnline {
    private PrefManager prefManager;
    private Set<String> videoUrl = new HashSet<>();
    private VideoLinkDatabase videoLinkDatabase;
    private VideoDataBase videoDataBase;
    private List<String> sites = new ArrayList<>();

    public GrabVideoOnline(final Context context) {

        videoLinkDatabase = new VideoLinkDatabase(context);
        prefManager = new PrefManager(context);
        videoDataBase = new VideoDataBase(context);

        sites.add("https://kingvideostatus.com");
        sites.add("https://videosongstatus.com");
        runThread();

    }

    private void runThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                if (!prefManager.isVideoLinkGrab()) {

                    // Add kingvideostatus web
                    LinkStoreToDatabaseKing();
                    // videosongstatus
                    LinkStoreToDatabasevideosong();


                }
                // Add To photo Database
                AddSites();


            }
        }).start();
    }

    private void LinkStoreToDatabaseKing() {

        try {
            String document = null;
            try {
                document = getHtml(sites.get(0) + "/");
            } catch (IOException e) {
            }

            String expression = "https:[\\S]+.php";
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(document);
            while (matcher.find()) {
                videoUrl.add(matcher.group(0));
            }

            for (String get : videoUrl) {
                videoLinkDatabase.Fill_It(get, 0);
            }
            videoUrl.clear();
        } catch (Exception e) {

        }
    }

    private void LinkStoreToDatabasevideosong() {

        try {
            String document = null;
            try {
                document = getHtml(sites.get(1) + "/video/");
            } catch (IOException e) {
            }

            String expression = "author\\sentry-title[\\S]+\\shref=\"[\\S]+>";
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(document);
            while (matcher.find()) {

                String url = sites.get(1) + matcher.group(0).substring(29, matcher.group(0).length() - 2);
                videoUrl.add(url);
                try {
                    document = getHtml(url);
                } catch (IOException e) {
                }
                expression = "Page\\s1\"[^S]+next";
                pattern = Pattern.compile(expression);
                Matcher matcher1 = pattern.matcher(document);
                if (matcher1.find()) {
                    document = matcher1.group(0);
                    expression = "href=\"[^javascript]+[^\"]+";
                    pattern = Pattern.compile(expression);
                    Matcher matcher2 = pattern.matcher(document);
                    while (matcher2.find()) {
                        videoUrl.add(sites.get(1) + matcher2.group(0).substring(6));
                    }
                }
            }
            for (String get : videoUrl) {
                videoLinkDatabase.Fill_It(get, 0);
            }
            prefManager.setVideoLinkGrab(true);
        } catch (Exception e) {

        }
    }

    private void AddSites() {
        if (!videoLinkDatabase.isEmpty()) {
            long getCount = videoLinkDatabase.getProfilesCount();
            Cursor cursor = videoLinkDatabase.getLink(getCount);
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    do {
                        String document = null;
                        try {
                            document = getHtml(cursor.getString(1));

                        } catch (IOException ignored) {
                        }
                        if (cursor.getString(1).startsWith(sites.get(0))) {

                            // video link grab
                            String expression = "video[\\S]+.mp4";
                            Pattern pattern = Pattern.compile(expression);
                            Matcher matcher = pattern.matcher(document);

                            String expression1 = "<a\\shref=\"#\"><img\\sclass=\"img-fluid\"\\ssrc=\"[^\"]+";
                            Pattern pattern1 = Pattern.compile(expression1);
                            Matcher matcher1 = pattern1.matcher(document);
                            while (matcher1.find() && matcher.find()) {
                                String name = matcher.group(0).replaceAll("/", " ")
                                        .substring(0, matcher.group(0).length() - 3).replace(".", "");
                                String videoLink = sites.get(0) + "/" + matcher.group(0);
                                String imgLink = matcher1.group(0).substring(40);
                                videoDataBase.FillIt(videoLink, name.trim(), imgLink);
                            }

                        } else if (cursor.getString(1).startsWith(sites.get(1))) {
                            String expression = "col-sm-6\">[^>]+[\\S]+[^>]+[\\S]+[^>]+><img\\ssrc=\"+[^\"]+";
                            Pattern pattern = Pattern.compile(expression);
                            Matcher matcher = pattern.matcher(document);

                            while (matcher.find()) {
                                expression = "href=[^<]+<img\\ssrc=\"[\\S]+";
                                pattern = Pattern.compile(expression);
                                Matcher matcher1 = pattern.matcher(matcher.group(0));
                                if (matcher1.find()) {
                                    String[] spilt = matcher1.group(0).split("\"><img src=\"");
                                    String videoLink = spilt[0].substring(6);
                                    String imgLink = spilt[1];
                                    String name = videoLink.replace("/view-video/", "").
                                            replaceAll("-", " ").replaceAll("/", " ");

                                    try {
                                        document = getHtml(sites.get(1) + videoLink);

                                    } catch (IOException ignored) {
                                    }

                                    expression = "<source\\ssrc=\"[^\"]+";
                                    pattern = Pattern.compile(expression);
                                    Matcher matcher2 = pattern.matcher(document);
                                    if (matcher2.find()) {
                                        videoDataBase.FillIt(matcher2.group(0).substring(13), name.trim(), imgLink);
                                    }
                                }
                            }
                        }
                        videoLinkDatabase.UpdateTheDoneColumn(cursor.getString(0), 1);
                    } while (cursor.moveToNext());
                } catch (Exception ignored) {

                }
            }

        }
    }


    private String getHtml(String url) throws IOException {
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
