package com.example.womanoftheday;

public class Book {
    private int id;
    private String title;
    private String author;

    public Book(){}

    public Book(String title, String author) {
        super();
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getID() {
        return id;
    }

    public void setTitle(String TITLE) {
        title = TITLE;
    }

    public void setAuthor(String AUTHOR) {
        author = AUTHOR;
    }

    public void setID(int ID) {
        id = ID;
    }

    @Override
    public String toString() {
        return "Book [id: " + id + "\ntitle: " + title + "\nauthor: "
                + author + "]";
    }
}
