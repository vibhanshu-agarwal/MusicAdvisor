package advisor;

public enum RequestType {
    NEW("new"),
    FEATURED("featured"),
    CATEGORIES("categories"),
    PLAYLISTS("playlists"),
    NEXT("next"),
    PREVIOUS("prev"),
    EXIT("exit");

    private final String type;

    RequestType(String requestType) {
        //set type
        this.type = requestType;
    }

    public String getType() {
        return type;
    }
}
