package com.example.dell.passwords;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HotSongActivity extends AppCompatActivity {

    Bitmap btmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView lyrics = findViewById(R.id.lyrics);
        final TextView artist = findViewById(R.id.text_view2);
        final TextView title = findViewById(R.id.text_view1);
//        final ImageView image = findViewById(R.id.image_view);

        title.setText(getIntent().getStringExtra("title"));
        artist.setText(getIntent().getStringExtra("artist"));
        lyrics.setText(getIntent().getStringExtra("lyrics"));

        ImageTask imageTask = new ImageTask(getIntent().getStringExtra("imageURL"));
        imageTask.execute((Void)null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lyrics.getText().equals("")) {
                    Toast.makeText(HotSongActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, lyrics.getText());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Choose application"));
                }
            }
        });

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public class ImageTask extends AsyncTask<Void, Void, Void> {

        private final String imageURL;

        ImageTask(String imageURL) {
            this.imageURL = imageURL;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                btmp = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView img = (ImageView) findViewById(R.id.image_view);
                    img.setImageBitmap(btmp);
                }
            });
        }
    }
}
