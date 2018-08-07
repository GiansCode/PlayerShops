package me.itsmas.playershops.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;

public class UtilJson
{
    private static final JsonParser PARSER = new JsonParser();

    public static JsonObject parse(FileReader reader)
    {
        return PARSER.parse(reader).getAsJsonObject();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String toPretty(JsonObject object)
    {
        return GSON.toJson(object);
    }
}
