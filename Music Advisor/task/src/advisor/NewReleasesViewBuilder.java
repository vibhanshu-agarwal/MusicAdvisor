package advisor;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class NewReleasesViewBuilder implements ViewBuilder {

    private static final NewReleasesViewBuilder INSTANCE = new NewReleasesViewBuilder();

    private NewReleasesViewBuilder() {
    }

    public static NewReleasesViewBuilder getInstance() {
        return INSTANCE;
    }
    private List<NewReleaseVO> newReleases = Collections.emptyList();

    private int pageSize;

    @Override
    public int getNumberOfPages() {
        if(newReleases.isEmpty() || pageSize == 0) {
            return 1;
        }
        return newReleases.size() / pageSize;
    }
    @Override
    public void buildView(HttpResponse<String> response, int pageSize) {
        this.pageSize = pageSize;
        if (response.body().contains("error")) {
            RequestView.printErrorResponseFromSpotify(response);
        } else {
            //Print the list of new releases
            newReleases = JsonProcessor.getListOfNewReleases(response.body());
        }
    }

    @Override
    public void displayResultByPage(int pageNumber) {
        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(pageNumber * pageSize,
                newReleases.size());
        for (int i = start; i < end; i++) {
            NewReleaseVO newReleaseVO = newReleases.get(i);
            System.out.printf("%s%n%s%n%s%n%n",
                    newReleaseVO.name(), newReleaseVO.artists(), newReleaseVO.link());
        }
    }

}
