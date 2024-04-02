package me.davipccunha.tests.dynamiceconomy.util;

import com.google.gson.Gson;

public class ObjectSerializer {
    public static <T> String serialize(T object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T deserialize(String serialized, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(serialized, clazz);
    }
}
