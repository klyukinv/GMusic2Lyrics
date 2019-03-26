package com.example.dell.passwords;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ScrollingActivity extends AppCompatActivity {

    private String url = "https://myapi.com";
    private Bitmap btmp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView lyrics = findViewById(R.id.lyrics);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lyrics.getText().equals("")) {
                    Toast.makeText(ScrollingActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, lyrics.getText());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Choose application"));
                }
            }
        });
        progressBar = findViewById(R.id.get_progress);
        progressBar.setVisibility(View.VISIBLE);
        LyricsTask task = new LyricsTask(getIntent().getIntExtra("ID", 0));
        task.execute((Void) null);
        TextView title = findViewById(R.id.text_view1);
        TextView artist = findViewById(R.id.text_view2);
        title.setText(getIntent().getStringExtra("title"));
        artist.setText(getIntent().getStringExtra("artist"));

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public class LyricsTask extends AsyncTask<Void, String, String> {

        private final int id;

        LyricsTask(int id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuffer stringBuffer;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(url + "/song/" + id);
                //Create a connection
                URLConnection connection = myUrl.openConnection();

                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream()));

                stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                JSONObject json = new JSONObject(stringBuffer.toString());
                InputStream in = new java.net.URL(json.getString("image")).openStream();
                btmp = BitmapFactory.decodeStream(in);
                return json.getString("lyrics");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "Couldn't reach lyrics";
        }

        @Override
        protected void onPostExecute(final String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    TextView lyrics = (TextView) findViewById(R.id.lyrics);
                    lyrics.setText(result);
                    ImageView img = (ImageView) findViewById(R.id.image_view);
                    img.setImageBitmap(btmp);
                }
            });
        }
    }
}
