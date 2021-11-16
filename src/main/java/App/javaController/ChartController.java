package App.javaController;

public class ChartController {

    private static ChartController instance= null;

    private ChartController() {}

    public static ChartController getInstance() {
        if(instance==null)
            instance = new ChartController();
        return instance;
    }
}
