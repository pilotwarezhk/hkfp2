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
import android.widget.TextView;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    static final String url = "https://www.hongkongfp.com/";
    static final String XPATH_STATS = "//head/title";
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Document document = Jsoup.connect(url).get();
                Elements description = document.select("meta[name=description]");
                desc = description.attr("content");
            } catch (IOException e) {
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