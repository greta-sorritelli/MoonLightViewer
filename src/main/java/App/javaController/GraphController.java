package App.javaController;

public class GraphController {

    private static GraphController instance= null;

    private GraphController() {}

    public static GraphController getInstance() {
        if(instance==null)
            instance = new GraphController();
        return instance;
    }
}
