package io.alerium.playershops.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;

public final class UtilJson {

    private static final JsonParser PARSER = new JsonParser();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private UtilJson() {
    }

    public static JsonObject parse(FileReader reader) {
        return PARSER.parse(reader).getAsJsonObject();
    }

    public static String toPretty(JsonObject object) {
        return GSON.toJson(object);
    }
    
}
