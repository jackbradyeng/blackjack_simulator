package controller;

import java.util.ArrayList;
import exceptions.PlayerCountException;
import model.actors.Player;
import model.observers.TablePrinter;
import model.observers.TableStats;
import model.orchestrators.GameModeOrchestrator;
import model.orchestrators.InteractiveModeOrchestrator;
import model.orchestrators.SimulationModeOrchestrator;
import model.strategies.player_strategies.OptimalNoCountingStrategy;
import model.table.*;
import static model.Constants.DEFAULT_PLAYER_STARTING_CHIPS;
import static model.Constants.DEFAULT_TABLE_POSITIONS;

public class Controller {

    private final Table table;
    private final TablePrinter tablePrinter;
    private final TableStats tableStats;
    private GameModeOrchestrator gameModeOrchestrator;
    private ArrayList<Player> players;

    public Controller(int playerCount, int deckCount, boolean isSimulation) {
        this.tableStats = new TableStats();
        this.tablePrinter = new TablePrinter();
        initializeGameModeOrchestrator(isSimulation);
        initializePlayers(playerCount);
        this.table = new Table(players, deckCount, isSimulation, this.tablePrinter, this.tableStats);
    }

    private void initializeGameModeOrchestrator(boolean isSimulation) {
        if (isSimulation) {
            this.gameModeOrchestrator = new SimulationModeOrchestrator();
        } else {
            this.gameModeOrchestrator = new InteractiveModeOrchestrator();
        }
    }

    private void initializePlayers(int playerCount) throws PlayerCountException {
        if (playerCount > DEFAULT_TABLE_POSITIONS) {
            throw new PlayerCountException(("Insufficient table positions for this many players." +
                    "The default number of table positions is %d .").formatted(DEFAULT_TABLE_POSITIONS));
        } else {
            this.players = new ArrayList<>();
            for (int i = 0; i < playerCount; i++) {
                Player player = new Player(DEFAULT_PLAYER_STARTING_CHIPS, new OptimalNoCountingStrategy());
                this.players.add(player);
            }
        }
    }

    public void startGame() {
        this.gameModeOrchestrator.runGame(this.table, this.tablePrinter, this.tableStats);
    }
}
