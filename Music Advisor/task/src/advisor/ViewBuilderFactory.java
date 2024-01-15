package advisor;

public interface ViewBuilderFactory {
    static ViewBuilder getViewBuilder(RequestType requestType) {
        return switch (requestType) {
            case NEW -> NewReleasesViewBuilder.getInstance();
            case FEATURED -> FeaturedListViewBuilder.getInstance();
            case CATEGORIES -> CategoriesListViewBuilder.getInstance();
            case PLAYLISTS -> PlayListsByCategoryViewBuilder.getInstance();
            default -> null;
        };
    }
}