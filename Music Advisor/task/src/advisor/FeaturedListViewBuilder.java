package advisor;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.TreeMap;

public class FeaturedListViewBuilder implements ViewBuilder {

    private static final FeaturedListViewBuilder INSTANCE = new FeaturedListViewBuilder();

    private FeaturedListViewBuilder() {
    }

    public static FeaturedListViewBuilder getInstance() {
        return INSTANCE;
    }
    Map<String, String> featuredPlaylists = new TreeMap<>();
    private int pageSize;

    @Override
    public int getNumberOfPages() {
        if(featuredPlaylists.isEmpty() || pageSize == 0) {
            return 1;
        }
        return featuredPlaylists.size() / pageSize;
    }

    @Override
    public void buildView(HttpResponse<String> response, int pageSize) {
        this.pageSize = pageSize;
        if (response.body().contains("error")) {
            RequestView.printErrorResponseFromSpotify(response);
        } else {
            //Print the list of featured playlists
            featuredPlaylists = JsonProcessor.getListOfFeaturedPlaylists(response.body());
        }
    }

    @Override
    public void displayResultByPage(int pageNumber) {
        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(pageNumber * pageSize,
                featuredPlaylists.size());
        for (int i = start; i < end; i++) {
            String key = (String) featuredPlaylists.keySet().toArray()[i];
            String value = featuredPlaylists.get(key);
            System.out.printf("%s%n%s%n%n",
                    key,
                    value);
        }
    }

}
