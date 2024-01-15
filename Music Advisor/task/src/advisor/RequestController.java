package advisor;

import java.net.http.HttpResponse;
import java.util.Scanner;

import static advisor.RequestType.*;

public class RequestController {
    RequestModel model;
    private RequestView view;

    public RequestController(RequestModel model, RequestView view) {
        this.model = model;
        this.view = view;
    }

    public void updateView(RequestType requestType, HttpResponse<String> response, int pageSize) {
        view.printRequestDetails(requestType,
                response,
                pageSize);
    }

    SpotifyAPIParams spotifyAPIParams = new SpotifyAPIParams();

    public void readInput(String[] args) {

        String apiUrl = "https://api.spotify.com";
        String baseUrl = "https://accounts.spotify.com";

        int page = 5;

        Scanner scanner = new Scanner(System.in);
        //Get the-access argument
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-access" -> baseUrl = args[i + 1];
                    case "-resource" -> apiUrl = args[i + 1];
                    case "-page" -> page = Integer.parseInt(args[i + 1]);
                    default -> {
                        //do nothing
                    }
                }
            }
        }

        String request = "";

        spotifyAPIParams.withBaseURL(baseUrl)
                .withApiURL(apiUrl)
                .withPageSize(page);


        do {
            String[] keywords = scanner.nextLine().split(" ");
            request = keywords[0];

            switch (request) {
                case "new" -> displayResultsByPage(scanner,
                        spotifyAPIParams,
                        NEW,
                        null);
                case "featured" -> displayResultsByPage(scanner,
                        spotifyAPIParams,
                        FEATURED,
                        null);
                case "categories" -> displayResultsByPage(scanner,
                        spotifyAPIParams,
                        CATEGORIES,
                        null);
                case "playlists" -> displayResultsByPage(scanner,
                        spotifyAPIParams,
                        PLAYLISTS,
                        buildCategory(keywords));
                case "auth" -> model.auth(spotifyAPIParams);
                case "exit" -> System.out.println("---GOODBYE!---");
                default -> throw new IllegalStateException("Unexpected value: " + request);
            }
        } while (!request.equals(EXIT.getType()));
    }

    private void displayResultsByPage(Scanner scanner, SpotifyAPIParams spotifyAPIParams, RequestType requestType, String category) {
        HttpResponse<String> response;
        if (spotifyAPIParams.isAccessTokenSet()) {
            response = model.getRecordsFromSpotify(spotifyAPIParams,
                    requestType,
                    category);
            int currentPage = 1;
            updateView(requestType,
                    response,
                    spotifyAPIParams.getPageSize());
            int totalPages = view.getNumberOfPages(requestType);

            String keyword = "";
            String msg = "";
            do {
                if (keyword.equals("next") && currentPage < totalPages) {
                    currentPage++;
                    view.displayResultByPage(requestType,
                            currentPage);
                    msg = "---PAGE %d OF %d---".formatted(currentPage,
                            totalPages);
                } else if (keyword.equals("prev") && currentPage > 1) {
                    currentPage--;
                    view.displayResultByPage(requestType,
                            currentPage);
                    msg = "---PAGE %d OF %d---".formatted(currentPage,
                            totalPages);
                } else if (keyword.equals("prev") && currentPage == 1) {
                    msg = "No more pages.";
                } else if (keyword.equals("next") && currentPage == totalPages) {
                    msg = "No more pages.";
                }  else {
                    view.displayResultByPage(requestType,
                            currentPage);
                    msg = "---PAGE %d OF %d---".formatted(currentPage,
                            totalPages);
                }
                System.out.println(msg);
                keyword = scanner.nextLine();
            } while (!keyword.equals("exit"));

            System.out.println("---GOODBYE!---");

        } else {
            view.printNoAccessMessage();
        }
    }

    private String buildCategory(String[] keywords) {
        StringBuilder category = new StringBuilder();
        for (int i = 1; i < keywords.length; i++) {
            category.append(keywords[i]).append(" ");
        }
        return category.toString().trim();
    }



}
