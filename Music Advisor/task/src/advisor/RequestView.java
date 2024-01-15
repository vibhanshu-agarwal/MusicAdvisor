package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;

public class RequestView {
    public static void printErrorResponseFromSpotify(HttpResponse<String> response) {
        //Get the error message
        try {
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            System.out.println(jo.get("error").getAsJsonObject().get("message").getAsString());
        } catch (Exception e) {
            System.out.println("Specified id doesn't exist.");
        }
    }

    public  void printNoAccessMessage() {
        System.out.println("Please, provide access for application.");
    }

    public void printRequestDetails(RequestType requestType, HttpResponse<String> response, int pageSize) {
        ViewBuilderFactory.getViewBuilder(requestType).buildView(response, pageSize);
    }

    public int getNumberOfPages(RequestType requestType) {
        return ViewBuilderFactory.getViewBuilder(requestType).getNumberOfPages();
    }

    public void displayResultByPage(RequestType requestType, int pageNumber) {
        ViewBuilderFactory.getViewBuilder(requestType).displayResultByPage(pageNumber);
    }
}
