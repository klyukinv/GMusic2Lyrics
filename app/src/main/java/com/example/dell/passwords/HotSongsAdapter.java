package com.example.dell.passwords;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class HotSongsAdapter extends RecyclerView.Adapter<HotSongsAdapter.ViewHolder> {

    private List<HotSong> hotList;
    private final int mRowLayout;
    private Context context;

    public HotSongsAdapter(List<HotSong> hotList, int rowLayout, Context context) {
        this.hotList = hotList;
        mRowLayout = rowLayout;
        this.context = context;
    }

    // Binding: The process of preparing a child view to display data corresponding to a position within the adapter.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        HotSong currentItem = hotList.get(i);
        viewHolder.title.setText(currentItem.getTitle());
        viewHolder.artist.setText(currentItem.getArtist());
        viewHolder.views.setText(currentItem.getViews());
        viewHolder.lyrics = currentItem.getLyrics();
        viewHolder.imageURL = currentItem.getImage();
        viewHolder.number.setText(String.valueOf(i + 1));
        ImageTask imgTask = new ImageTask(currentItem.getImage(), viewHolder);
        imgTask.execute((Void) null);
    }

    @NonNull
    @Override
    public HotSongsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mRowLayout, parent, false);
        return new HotSongsAdapter.ViewHolder(v, context);
    }

    @Override
    public int getItemCount() {
        return (null == hotList) ? 0 : hotList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView artist;
        TextView views;
        ImageView img;
        TextView number;
        String lyrics;
        Context context;
        String imageURL;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            title = itemView.findViewById(R.id.title_hot);
            artist = itemView.findViewById(R.id.artist_hot);
            img = itemView.findViewById(R.id.image_hot);
            views = itemView.findViewById(R.id.views_hot);
            number = itemView.findViewById(R.id.number_hot);
            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, HotSongActivity.class);
            intent.putExtra("lyrics", lyrics);
            intent.putExtra("title", title.getText());
            intent.putExtra("artist", artist.getText());
            intent.putExtra("imageURL", imageURL);
            context.startActivity(intent);
        }
    }

    class ImageTask extends AsyncTask<Void, Void, Void> {

        private final String imageURL;
        ViewHolder viewHolder;
        Bitmap btmp;

        ImageTask(String imageURL, ViewHolder vh) {
            this.imageURL = imageURL;
            viewHolder = vh;
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
            ImageView img = viewHolder.img;
            img.setImageBitmap(btmp);
        }
    }

}

