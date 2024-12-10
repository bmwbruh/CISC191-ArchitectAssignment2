package edu.sdccd.cisc191.template;

import com.google.gson.Gson;

public class BookResponse {
    private final String status;
    private final String message;

    public BookResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static BookResponse fromJSON(String json) {
        return new Gson().fromJson(json, BookResponse.class);
    }

    public static String toJSON(BookResponse response) {
        return new Gson().toJson(response);
    }
}
