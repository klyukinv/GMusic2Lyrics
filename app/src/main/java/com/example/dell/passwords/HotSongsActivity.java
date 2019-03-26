package com.example.dell.passwords;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HotSongsActivity extends AppCompatActivity {

    List<HotSong> songs;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_songs);
        setTitle(R.string.hot_songs_lyrics);

        //Set up RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.hotlist);

        //Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fillSongList();

        HotSongsAdapter mAdapter = new HotSongsAdapter(songs, R.layout.hot_song, HotSongsActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    private void fillSongList() {
        songs = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("DBname", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM HotTable", null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else {
            String artist = cursor.getString(cursor.getColumnIndex("Artist"));
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            String views = cursor.getString(cursor.getColumnIndex("Views"));
            String lyrics = cursor.getString(cursor.getColumnIndex("Lyrics"));
            String imageURL = cursor.getString(cursor.getColumnIndex("ImageURL"));
            songs.add(new HotSong(title, artist, lyrics, imageURL, views));
            while (cursor.moveToNext()) {
                artist = cursor.getString(cursor.getColumnIndex("Artist"));
                title = cursor.getString(cursor.getColumnIndex("Title"));
                views = cursor.getString(cursor.getColumnIndex("Views"));
                lyrics = cursor.getString(cursor.getColumnIndex("Lyrics"));
                imageURL = cursor.getString(cursor.getColumnIndex("ImageURL"));
                songs.add(new HotSong(title, artist, lyrics, imageURL, views));
            }
        }
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.homeAsUp) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
