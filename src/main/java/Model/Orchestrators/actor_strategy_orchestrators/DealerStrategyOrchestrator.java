package Model.Orchestrators.actor_strategy_orchestrators;

import Model.Observers.TablePrinter;
import Model.Table.Table;
import java.util.Objects;
import static Model.Constants.STAND;

public class DealerStrategyOrchestrator {

    /** executes the dealer's strategy. */
    public void executeDealerStrategy(Table table, TablePrinter tablePrinter, boolean isSimulation) {
        while(!Objects.equals(table.getDealer().executeStrategy(), STAND)) {
            table.handleDealerAction(table.getDealer().executeStrategy());
        }
        if (!isSimulation) tablePrinter.printDealerHand(table);
    }
}
