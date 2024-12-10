package edu.sdccd.cisc191.template;

import com.google.gson.Gson;

public class BookRequest {
    private final String action;
    private final String bookName;

    public BookRequest(String action, String bookName) {
        this.action = action;
        this.bookName = bookName;
    }

    public String getAction() {
        return action;
    }

    public String getBookName() {
        return bookName;
    }

    public static BookRequest fromJSON(String json) {
        return new Gson().fromJson(json, BookRequest.class);
    }

    public static String toJSON(BookRequest request) {
        return new Gson().toJson(request);
    }
}
