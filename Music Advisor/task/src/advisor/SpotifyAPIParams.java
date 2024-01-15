package advisor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.StringJoiner;

public class SpotifyAPIParams {
    private String clientID;
    private String clientSecret;
    private String redirectURI;
    private String grantType;
    private String code;
    private String accessToken;

    private String apiURL;

    private String baseURL;

    private int pageSize;

    private boolean accessTokenSet;

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getApiURL() {
        return apiURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isAccessTokenSet() {
        return accessTokenSet;
    }

    public void setAccessTokenSet(boolean accessTokenSet) {
        this.accessTokenSet = accessTokenSet;
    }

    public SpotifyAPIParams withClientID(String clientID) {
        this.clientID = clientID;
        return this;
    }

    public SpotifyAPIParams withClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public SpotifyAPIParams withRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
        return this;
    }

    public SpotifyAPIParams withGrantType(String grantType) {
        this.grantType = grantType;
        return this;
    }

    public SpotifyAPIParams withCode(String code) {
        this.code = code;
        return this;
    }

    public SpotifyAPIParams withAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public SpotifyAPIParams withApiURL(String apiURL) {
        this.apiURL = apiURL;
        return this;
    }

    public SpotifyAPIParams withBaseURL(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public SpotifyAPIParams withPageSize(int page) {
        this.pageSize = page;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpotifyAPIParams.class.getSimpleName() + "[", "]")
                .add("clientID='" + clientID + "'")
                .add("clientSecret='" + clientSecret + "'")
                .add("redirectURI='" + redirectURI + "'")
                .add("grantType='" + grantType + "'")
                .add("code='" + code + "'")
                .add("accessToken='" + accessToken + "'")
                .toString();
    }

    public String sendPost(String postUrl) {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(postUrl))
                .POST(buildFormDataFromParams())
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error response");
        }
        return "";
    }

    public HttpRequest buildHttpGETRequest(String url) {
        return HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + getAccessToken())
                .uri(URI.create(url))
                .GET()
                .build();
    }

    public HttpRequest.BodyPublisher buildFormDataFromParams() {
        String requestBody = String.join("&",
                "grant_type=" + getGrantType(),
                "code=" + getCode(),
                "client_id=" + getClientID(),
                "client_secret=" + getClientSecret(),
                "redirect_uri=" + getRedirectURI());
        return HttpRequest.BodyPublishers.ofString(requestBody);

    }



}
