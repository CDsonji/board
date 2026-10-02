package org.example.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JSON {
    private static final Gson gson = new Gson();

    public static String Stringify(Object object) {
        return gson.toJson(object);
    }

    public static <T> T Objectify(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
