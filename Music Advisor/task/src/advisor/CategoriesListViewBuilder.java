package advisor;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CategoriesListViewBuilder implements ViewBuilder{

    private static final CategoriesListViewBuilder INSTANCE = new CategoriesListViewBuilder();

    private CategoriesListViewBuilder() {
    }

    public static CategoriesListViewBuilder getInstance() {
        return INSTANCE;
    }

    private List<String> categories = new ArrayList<>();

    private int pageSize;
    @Override
    public int getNumberOfPages() {
        if(categories.isEmpty() || pageSize == 0) {
            return 1;
        }
        return categories.size() / pageSize;
    }

    @Override
    public void buildView(HttpResponse<String> response, int pageSize) {
        this.pageSize = pageSize;
        if (response.body().contains("error")) {
            RequestView.printErrorResponseFromSpotify(response);
        } else {
            //Print the list of categories
            categories = JsonProcessor.getListOfCategories(response.body());
        }
    }

    @Override
    public void displayResultByPage(int pageNumber) {
        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, categories.size());
        for (int i = start; i < end; i++) {
            System.out.println(categories.get(i));
        }
    }

}
