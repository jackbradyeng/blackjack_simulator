import controller.Controller;
import static model.Constants.*;

public class Launcher {
    public static void main(String[] args) {
        boolean isSimulation = args.length > 0 && Boolean.parseBoolean(args[0]);
        Controller controller = new Controller(DEFAULT_NUMBER_OF_PLAYERS, DEFAULT_NUMBER_OF_DECKS, isSimulation);
        controller.startGame();
    }
}
