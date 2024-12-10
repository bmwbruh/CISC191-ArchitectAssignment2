package edu.sdccd.cisc191.template;

import com.google.gson.Gson;

public class CustomerRequest {
    private final int id;

    public CustomerRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CustomerRequest fromJSON(String json) {
        return new Gson().fromJson(json, CustomerRequest.class);
    }

    public static String toJSON(CustomerRequest request) {
        return new Gson().toJson(request);
    }
}
