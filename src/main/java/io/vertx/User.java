package io.vertx;

import com.fasterxml.jackson.databind.util.JSONPObject;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.atomic.AtomicInteger;


public class User {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private String id;

    private String firstname;

    private String lastname;

    public User(String firstname, String lastname) {
        this.id = Integer.toString(COUNTER.getAndIncrement());
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User() {
        this.id = Integer.toString(COUNTER.getAndIncrement());
    }


    public JsonObject toJson() {
        JsonObject json = new JsonObject()
                .put("firstname", firstname)
                .put("lastname", lastname)
                .put("_id", id);
        return json;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getId() {
        return id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


}
