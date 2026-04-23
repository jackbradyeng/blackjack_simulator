package Model.Orchestrators;

import Model.Observers.TablePrinter;
import Model.Observers.TableStats;
import Model.Table.Table;

public interface GameModeOrchestrator {

    void runGame(Table table, TablePrinter tablePrinter, TableStats tableStats);
}
