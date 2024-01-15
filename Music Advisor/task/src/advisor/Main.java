package advisor;

public class Main {

    public static void main(String[] args) {

        //Invoke Controller in the MVC pattern
        RequestModel model = new RequestModel();
        RequestView view = new RequestView();
        RequestController controller = new RequestController(model,
                view);

        controller.readInput(args);
    }

}