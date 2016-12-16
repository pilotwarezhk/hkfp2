package com.pilotwarezhk.hkfp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CommentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import org.htmlcleaner.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ListViewAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist;
    static String TITLE = "title";
    static String PUBDATE = "pubdate";
    static String CATEGORY = "category";
    static final String url = "https://www.hongkongfp.com/category/topics/";
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new JsoupListView().execute();

        Button btnTitle = (Button) findViewById(R.id.btnTitle);
        Button btnDesc  = (Button) findViewById(R.id.btnDesc);
        Button btnLogo  = (Button) findViewById(R.id.btnLogo);

        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Title().execute();
            }
        });

        btnDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Description().execute();
            }
        });

        btnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logo().execute();
            }
        });
    }

    private class JsoupListView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Hong Kong Free Press");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            arraylist = new ArrayList<HashMap<String, String>>();

            try {
                Document doc = Jsoup.connect(url).get();

                for (Element article : doc.select("article")) {
                    HashMap<String, String> map = new HashMap<String, String>();

                    String title = article.select("h2 a").text();
                    Elements imgSrc = article.select("div[class=meta-image] img[src]");
                    String imgSrcStr = imgSrc.attr("src");

                    map.put("title", title);
                    map.put("img", imgSrcStr);

                    arraylist.add(map);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listview = (ListView) findViewById(R.id.listview);
            adapter = new ListViewAdapter(MainActivity.this, arraylist);
            listView.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

    private class Title extends AsyncTask<Void, Void, Void> {
        String title;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("JSoup Test");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String stats = "";
            try {
                HtmlCleaner cleaner = new HtmlCleaner();
                CleanerProperties props = cleaner.getProperties();
                props.setAllowHtmlInsideAttributes(false);
                props.setAllowMultiWordAttributes(true);
                props.setRecognizeUnicodeChars(true);
                props.setOmitComments(true);

                URL link = new URL(url);
                TagNode root = cleaner.clean(link);
                Object[] statsNode = root.evaluateXPath(XPATH_STATS);
                if(statsNode.length>0) {
                    TagNode resultNode = (TagNode)statsNode[0];
                    stats = resultNode.getText().toString();
                }
                //Document document = Jsoup.connect(url).get();
                title = stats;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
            txtTitle.setText(title);
            mProgressDialog.dismiss();
        }
    }

    private class Description extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("JSoup Test");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String titleText = "";
                Document document = Jsoup.connect(url).get();
                Element content = document.getElementById("primary");
                Elements articles = content.getElementsByTag("article");
                for (Element article : articles) {
                    Elements headers = article.getElementsByTag("header");
                    for (Element header : headers) {
                        Elements titles = header.getElementsByTag("a");
                        for (Element title : titles) {
                            titleText = title.text();
                        }
                    }
                }
                desc = titleText;
                /*
                Document document = Jsoup.connect(url).get();
                Elements description = document.select("meta[name=description]");
                desc = description.attr("content");
                */
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            TextView txtDesc = (TextView) findViewById(R.id.txtDesc);
            txtDesc.setText(desc);
            mProgressDialog.dismiss();
        }
    }

    private class Logo extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("JSoup Test");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect(url).get();
                Elements img = document.select("span[class=site-title] img[src]");

                //String imgSrc = img.attr("src");
                String imgSrc = "https://www.hongkongfp.com/wp-content/uploads/2015/11/HKFP-Logo_Wide-header-120.png";
                URL url = new URL(imgSrc);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream input = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageView imgLogo = (ImageView) findViewById(R.id.logo);
            imgLogo.setImageBitmap(bitmap);
            mProgressDialog.dismiss();
        }
    }
}
