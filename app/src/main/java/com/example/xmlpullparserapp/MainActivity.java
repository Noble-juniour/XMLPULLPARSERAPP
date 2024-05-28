package com.example.xmlpullparserapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<NewsItem> news;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        news = new ArrayList<>();

        GetDataAsyncTask getDataAsyncTask = new GetDataAsyncTask();
        getDataAsyncTask.execute();

    }

    //ASYNCTASK
    private class GetDataAsyncTask extends AsyncTask<Void,Void ,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            //Input stream reads data from an external file
            InputStream inputStream = getInputStream();
            try {
                initXMLPullParser(inputStream);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            adapter.setNews(news);
            super.onPostExecute(unused);
        }
    }
    private void  initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
        Log.d(TAG, "initXMLPullParser: called");
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);
        parser.nextTag();

        parser.require(XmlPullParser.START_TAG, null , "rss");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }

        parser.require(XmlPullParser.START_TAG, null , "channel");
            while (parser.next() != XmlPullParser.END_TAG)
            {
                if (parser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }
                if (parser.getName().equals("item"))
                {
                 parser.require(XmlPullParser.START_TAG, null, "item");

                 String title = "";
                 String desc = "";
                 String link = "";
                 String date = "";

                 while (parser.next() !=XmlPullParser.END_TAG)
                 {
                     if (parser.getEventType() != XmlPullParser.START_TAG)
                     {
                         continue;
                     }
                     String tagName = parser.getName();
                     if (tagName.equals("title"))
                     {
                         //Get the title information
                         title = getContent(parser,"title");
                     } else if (tagName.equals("description"))
                     {
                         //get the description content
                         desc = getContent(parser,"desc");
                     } else if (tagName.equals("link")) {
                         //get the link details
                         link = getContent(parser, "link");
                     } else if (tagName.equals("date")) {
                         //get the date details
                         date = getContent(parser, "date");
                     } else {
                         //SKIP THE TAG
                         skipTag(parser);
                     }
                 }
                    NewsItem item =  new NewsItem(title,desc,link,date);
                     news.add(item);
                }
                else
                {
                    //skip the tag
                    skipTag(parser);
                }
            }

        }
    }

    private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d(TAG, "skipTag: skipping" + parser.getName());

        if (parser.getEventType() != XmlPullParser.START_TAG)
        {
            throw new IllegalStateException();
        }
        int number = 1;
        while (number != 0)
        {
            switch (parser.next())
            {
                case XmlPullParser.START_TAG:
                    number++;
                    break;
                case XmlPullParser.END_TAG:
                    number--;
                    break;
                default:
                    break;
            }
        }
    }
    private String getContent(XmlPullParser parser, String tagName)
    {
        Log.d(TAG, "getContent: started for tag" + tagName);
        try {
            parser.require(XmlPullParser.START_TAG, null,tagName);

            String content = "";

            if (parser.next() == XmlPullParser.TEXT)
            {
             content = parser.getText();
             parser.next();
            }
            return content;
        } catch (IOException e) {
        e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }
    private InputStream getInputStream()
    {
        Log.d(TAG, "getInputStream: started");
        try {
            URL url = new URL("https://www.autosport.com/rss/f1/news/");
            HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}