package model.orchestrators;

import model.observers.TablePrinter;
import model.observers.TableStats;
import model.table.Table;

public interface GameModeOrchestrator {

    void runGame(Table table, TablePrinter tablePrinter, TableStats tableStats);
}
