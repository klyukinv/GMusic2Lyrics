package com.example.dell.passwords;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.app.SearchManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MyRecyclerViewAdapter adapter;
    private List<ExampleItem> exampleList;
    private HotSongsTask hotTask;

    private String url ="https://myapi.com";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username_view);
        SharedPreferences save = getSharedPreferences("SAVE", 0);
        username.setText(save.getString("username", ""));

        //Set up RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.list);
        fillSongList();

        //Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter
        adapter = new MyRecyclerViewAdapter(exampleList, R.layout.list_item, MainActivity.this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            SharedPreferences save = getSharedPreferences("SAVE", 0);
            SharedPreferences.Editor editor = save.edit(); //создаём редактор shared preferences
            editor.putString("username", "");
            editor.putString("password", "");
            editor.apply();
            SQLiteDatabase db = openOrCreateDatabase("DBname", MODE_PRIVATE, null);
            db.delete("MyTable", null, null);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            AlertDialog.Builder dialog = new
                    AlertDialog.Builder(MainActivity.this);
            try {
                dialog.setMessage(getTitle().toString() + " версия " +
                        getPackageManager().getPackageInfo(getPackageName(), 0).versionName
                        + "\r\n\nПрограмма для получения текстов песен из библиотеки Google Play Music\r\n\n " +
                        "Автор - Клюкин Валерий Дмитриевич, гр. БПИ173");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            dialog.setTitle("О программе");
            dialog.setNeutralButton("OK", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.setIcon(R.mipmap.ic_launcher_round);
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        } else if (id == R.id.nav_hot) {
            hotTask = new HotSongsTask();
            hotTask.execute((Void)null);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fillSongList() {
        exampleList = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("DBname", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM MyTable", null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else {
            String artist = cursor.getString(cursor.getColumnIndex("Artist"));
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            int id = cursor.getInt(cursor.getColumnIndex("Number"));
            exampleList.add(new ExampleItem(title, artist, id));
            while (cursor.moveToNext()) {
                artist = cursor.getString(cursor.getColumnIndex("Artist"));
                title = cursor.getString(cursor.getColumnIndex("Title"));
                id = cursor.getInt(cursor.getColumnIndex("Number"));
                exampleList.add(new ExampleItem(title, artist, id));
            }
        }
        db.close();
    }

    public class HotSongsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            StringBuffer stringBuffer;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(url + "/top");
                //Create a connection
                URLConnection connection = myUrl.openConnection();

                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream()));

                stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                SQLiteDatabase db = openOrCreateDatabase("DBname", MODE_PRIVATE, null);
                db.execSQL("DROP TABLE IF EXISTS HotTable");
                db.execSQL("CREATE TABLE IF NOT EXISTS HotTable (Artist VARCHAR, Title VARCHAR, " +
                        "Views VARCHAR, Lyrics VARCHAR, ImageURL VARCHAR);");

                JSONArray arr = new JSONArray(stringBuffer.toString());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
//                    db.execSQL("INSERT INTO HotTable (Artist, Title, Views, Lyrics, ImageURL) VALUES (\"" +
//                            obj.getString("artist") + "\", \"" + obj.getString("title") +
//                            "\", \"" + obj.getString("views") + "\", \"" +
//                            obj.getString("lyrics") + "\", \"" + obj.getString("image")
//                            + "\");");
                    ContentValues values = new ContentValues();
                    values.put("Artist", obj.getString("artist"));
                    values.put("Title", obj.getString("title"));
                    values.put("Views", obj.getString("views"));
                    values.put("Lyrics", obj.getString("lyrics"));
                    values.put("ImageURL", obj.getString("image"));
                    db.insert("HotTable", null, values);
                }
                db.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            hotTask = null;
            if (success) {
                finish();
                Intent intent = new Intent(MainActivity.this, HotSongsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "API is not available now",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
