package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class RequestModel {
    public HttpResponse<String> getRecordsFromSpotify(SpotifyAPIParams spotifyAPIParams, RequestType requestType, String category) {
        HttpResponse<String> response = null;
        switch (requestType) {
            case NEW -> response = printNewReleases(spotifyAPIParams);
            case FEATURED -> response = printFeaturedList(spotifyAPIParams);
            case CATEGORIES -> response = printCategoriesList(spotifyAPIParams);
            case PLAYLISTS -> response = printPlayListsByCategory(spotifyAPIParams, category);
            default -> {
                //do nothing
            }
        }
        return response;
    }
    private HttpResponse<String> printNewReleases(SpotifyAPIParams spotifyAPIParams) {
        HttpResponse<String> response = null;
        //get the new list from the api
        HttpRequest httpRequest = spotifyAPIParams.buildHttpGETRequest(
                String.join("",
                        spotifyAPIParams.getApiURL(),
                        "/v1/browse/new-releases"));
        try {
            response = HttpClient
                    .newBuilder()
                    .build()
                    .send(httpRequest,
                            HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Error response");
        }

        return response;
    }


    private HttpResponse<String> printFeaturedList(SpotifyAPIParams spotifyAPIParams) {
        HttpResponse<String> response = null;
        //get the featured playlists from the api
        HttpRequest httpRequest = spotifyAPIParams.buildHttpGETRequest(
                String.join("",
                        spotifyAPIParams.getApiURL(),
                        "/v1/browse/featured-playlists"));
        try {
            response = HttpClient
                    .newBuilder()
                    .build()
                    .send(httpRequest,
                            HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Error response");
        }

        return response;
    }

    private HttpResponse<String> printCategoriesList(SpotifyAPIParams spotifyAPIParams) {
        HttpResponse<String> response = null;

        //get the categories from the api
        HttpRequest httpRequest = spotifyAPIParams.buildHttpGETRequest(
                String.join("",
                        spotifyAPIParams.getApiURL(),
                        "/v1/browse/categories"));
        try {
            response = HttpClient
                    .newBuilder()
                    .build()
                    .send(httpRequest,
                            HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            System.out.println("Error response");
        }


        return response;
    }

    private Map<String, String> getAllCategories(SpotifyAPIParams spotifyAPIParams) {
        HttpRequest httpRequest = spotifyAPIParams.buildHttpGETRequest(
                String.join("",
                        spotifyAPIParams.getApiURL(),
                        "/v1/browse/categories"));

        Map<String, String> categoryMap = new HashMap<>();
        try {
            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(httpRequest,
                            HttpResponse.BodyHandlers.ofString());

            if (response.body().contains("error")) {
                RequestView.printErrorResponseFromSpotify(response);
            } else {
                //Print the list of categories
                JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray items = jo.getAsJsonObject("categories").getAsJsonArray("items");

                items.forEach(item -> categoryMap.put(item.getAsJsonObject().get("id").getAsString(),
                        item.getAsJsonObject().get("name").getAsString()));
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error response");
        }
        return categoryMap;
    }

    private HttpResponse<String> printPlayListsByCategory(SpotifyAPIParams spotifyAPIParams, String categoryName) {
        HttpResponse<String> response = null;

        //get the categories from the api
        Map<String, String> categoriesMap = getAllCategories(spotifyAPIParams);
        if (categoriesMap.containsValue(categoryName)) {
            String categoryId = categoriesMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(categoryName))
                    .findFirst()
                    .get()
                    .getKey();

            HttpRequest httpRequest = spotifyAPIParams.buildHttpGETRequest(
                    String.join("",
                            spotifyAPIParams.getApiURL(),
                            "/v1/browse/categories/",
                            categoryId,
                            "/playlists"));
            try {
                response = HttpClient
                        .newBuilder()
                        .build()
                        .send(httpRequest,
                                HttpResponse.BodyHandlers.ofString());

            } catch (IOException | InterruptedException e) {
                System.out.println("Error response");
            }
        }

        return response;
    }

    public SpotifyAPIParams auth(SpotifyAPIParams params) {

        //Add required fields for authentication
        params
                .withClientID("4eb4115185a5456bb90e38554a005705")
                .withClientSecret("a74d703571f74391a71fb74c996e4f11")
                .withGrantType("authorization_code")
                .withRedirectURI("http://localhost:8080");


        String postUrl = String.join("",
                params.getBaseURL(),
                "/api/token");
        String spotifyUrl = String.join("",
                params.getBaseURL(),
                "/authorize?client_id=",
                params.getClientID(),
                "&redirect_uri=",
                params.getRedirectURI(),
                "&response_type=code");

        System.out.println("use this link to request the access code:");
        System.out.println(spotifyUrl);

        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(8080),
                    0);
            server.start();

            System.out.println("waiting for code...");
            server.createContext("/",
                    exchange -> {
                        String query = exchange.getRequestURI().getQuery();

                        String msg;
                        if (query != null && query.contains("code")) {
                            String code = query.substring(query.indexOf("=") + 1);
                            params.withCode(code);
                            System.out.println("code received");
                            msg = "Got the code. Return back to your program.";

                            writeMsgToHTTPResponse(exchange,
                                    msg);

                            System.out.println("making http request for access_token...");
                            System.out.println("response:");
                            String response = params.sendPost(postUrl);
                            System.out.println(response);
                            //Get the access code from the response
                            params.withAccessToken(JsonProcessor.getAccessCodeFromJson(response));
                            params.setAccessTokenSet(true);
                            System.out.println("---SUCCESS---");


                        } else {
                            msg = "Authorization code not found. Try again.";
                            writeMsgToHTTPResponse(exchange,
                                    msg);
                            params.setAccessTokenSet(false);
                        }
//                        server.stop(0);

                    });

            Thread.sleep(10000);
            server.stop(1);

            return params;
        } catch (IOException | InterruptedException e) {
            System.out.println("Server Error");
        }
        return params;
    }

    public void writeMsgToHTTPResponse(HttpExchange exchange, String msg) throws IOException {
        exchange.sendResponseHeaders(200,
                msg.length());
        exchange.getResponseBody().write(msg.getBytes());
        exchange.getResponseBody().close();
    }

}
