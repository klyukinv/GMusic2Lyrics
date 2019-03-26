package com.example.dell.passwords;

public class ExampleItem {
    private String text1;
    private String text2;
    private int id;

    public ExampleItem(String text1, String text2, int id) {
        this.text1 = text1;
        this.text2 = text2;
        this.id = id;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

    public int getId() {
        return id;
    }
}
