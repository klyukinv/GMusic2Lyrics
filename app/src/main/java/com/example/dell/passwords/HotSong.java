package com.example.dell.passwords;

public class HotSong {
    private String title;
    private String artist;
    private String lyrics;
    private String image;
    private String views;

    public HotSong(String title, String artist, String lyrics, String image, String views) {
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
        this.image = image;
        this.views = views;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getImage() {
        return image;
    }

    public String getViews() {
        return views;
    }
}
