package edu.sdccd.cisc191.template;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CustomerResponse {
    private int id;
    private String firstName;
    private String lastName;

    // Jackson object mapper for JSON serialization/deserialization
    private static final ObjectMapper mapper = new ObjectMapper();

    public CustomerResponse() {
    }

    public CustomerResponse(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static CustomerResponse fromJSON(String json) throws IOException {
        return mapper.readValue(json, CustomerResponse.class);
    }

    public static String toJSON(CustomerResponse response) throws IOException {
        return mapper.writeValueAsString(response);
    }
}
