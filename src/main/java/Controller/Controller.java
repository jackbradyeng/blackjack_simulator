package Controller;

import Model.Observers.TablePrinter;
import Model.Observers.TableStats;
import Model.Orchestrators.GameModeOrchestrator;
import Model.Orchestrators.InteractiveModeOrchestrator;
import Model.Orchestrators.SimulationModeOrchestrator;
import Model.Table.*;

public class Controller {

    private final Table table;
    private final TablePrinter tablePrinter;
    private final TableStats tableStats;
    private GameModeOrchestrator gameModeOrchestrator;

    public Controller(int playerCount, int deckCount, boolean isSimulation) {
        this.tableStats = new TableStats();
        this.tablePrinter = new TablePrinter();
        this.table = new Table(playerCount, deckCount, isSimulation, this.tablePrinter, this.tableStats);
        initializeGameModeOrchestrator(isSimulation);
    }

    private void initializeGameModeOrchestrator(boolean isSimulation) {
        if (isSimulation) {
            this.gameModeOrchestrator = new SimulationModeOrchestrator();
        } else {
            this.gameModeOrchestrator = new InteractiveModeOrchestrator();
        }
    }

    public void startGame() {
        this.gameModeOrchestrator.runGame(this.table, this.tablePrinter, this.tableStats);
    }
}
