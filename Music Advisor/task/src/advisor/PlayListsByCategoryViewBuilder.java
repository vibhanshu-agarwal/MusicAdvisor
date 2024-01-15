package advisor;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.TreeMap;

public class PlayListsByCategoryViewBuilder implements ViewBuilder{

    private static final PlayListsByCategoryViewBuilder INSTANCE = new PlayListsByCategoryViewBuilder();

    private PlayListsByCategoryViewBuilder() {
    }

    public static PlayListsByCategoryViewBuilder getInstance() {
        return INSTANCE;
    }

    Map<String, String> playListsByCategory = new TreeMap<>();

    private int pageSize;

    @Override
    public int getNumberOfPages() {
        if (playListsByCategory.isEmpty() || pageSize == 0) {
            return 1;
        }
        return playListsByCategory.size() / pageSize;
    }

    @Override
    public void buildView(HttpResponse<String> response, int pageSize) {
        this.pageSize = pageSize;
        if (response.body().contains("error")) {
            RequestView.printErrorResponseFromSpotify(response);
        } else {
            //Print the list of categories
            playListsByCategory = JsonProcessor.getListOfPlaylistsByCategory(response.body());
        }
    }

    public void displayResultByPage(int pageNumber) {
        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(pageNumber * pageSize,
                playListsByCategory.size());
        for (int i = start; i < end; i++) {
            String key = (String) playListsByCategory.keySet().toArray()[i];
            String value = playListsByCategory.get(key);
            System.out.printf("%s%n%s%n%n",
                    key,
                    value);
        }
    }

}
