package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

public class JsonProcessor {

    private JsonProcessor() {
    }

    public static List<NewReleaseVO> getListOfNewReleases(String json) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray items = jo.getAsJsonObject("albums").getAsJsonArray("items");

        List<NewReleaseVO> newReleaseVOList = new ArrayList<>();
        items.forEach(item -> {
            List<String> artists = new ArrayList<>();
            item.getAsJsonObject().getAsJsonArray("artists")
                    .forEach(artist -> artists.add(artist.getAsJsonObject().get("name").getAsString()));
            NewReleaseVO newReleaseVO = new NewReleaseVO(
                    item.getAsJsonObject().get("name").getAsString(),
                    artists,
                    item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString()
            );
            newReleaseVOList.add(newReleaseVO);
        });

        return newReleaseVOList;
    }

    public static Map<String, String> getListOfFeaturedPlaylists(String json) {

        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray items = jo.getAsJsonObject("playlists").getAsJsonArray("items");

        Map<String, String> playlistsMap = new TreeMap<>();
        items.forEach(item -> playlistsMap.put(item.getAsJsonObject().get("name").getAsString(),
                item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString()));

        return playlistsMap;
    }

    public static List<String> getListOfCategories(String json) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray items = jo.getAsJsonObject("categories").getAsJsonArray("items");

        List<String> categoryList = new ArrayList<>();
        items.forEach(item -> categoryList.add(item.getAsJsonObject().get("name").getAsString()));
        //for each category in the json object
        return categoryList;
    }

    public static Map<String, String> getListOfPlaylistsByCategory(String json) {
        Map<String, String> playlistsMap = new LinkedHashMap<>();

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            JsonArray items = jo.getAsJsonObject("playlists")
                    .getAsJsonArray("items");

            items.forEach(item -> playlistsMap.put(
                    item.getAsJsonObject().get("name").getAsString(),
                    item.getAsJsonObject().get("external_urls")
                            .getAsJsonObject().get("spotify").getAsString())
            );
        } catch (Exception e) {
            System.out.println("Specified id doesn't exist.");
        }

        return playlistsMap;
    }

    public static String getAccessCodeFromJson(String response) {
        JsonObject jo = JsonParser.parseString(response).getAsJsonObject();
        return jo.get("access_token").getAsString();
    }

}
