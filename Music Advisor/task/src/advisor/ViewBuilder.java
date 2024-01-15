package advisor;

import java.net.http.HttpResponse;

public interface ViewBuilder {
    int getNumberOfPages();
    void buildView(HttpResponse<String> response, int pageSize);

    void displayResultByPage(int pageNumber);
}
