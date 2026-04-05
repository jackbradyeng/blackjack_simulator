import Controller.Controller;
import static Model.Constants.*;

public class Launcher {
    public static void main(String[] args) {
        Controller controller = new Controller(DEFAULT_NUMBER_OF_PLAYERS, DEFAULT_NUMBER_OF_DECKS, false);
        controller.startGame();
    }
}
