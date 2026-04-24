package Model.Orchestrators.actor_strategy_orchestrators;

import Model.Actors.Player;
import Model.Observers.TablePrinter;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Table;
import static Model.Constants.DOUBLE;
import static Model.Constants.STAND;

public class PlayerStrategyOrchestrator {

    /** executes the player's strategy. */
    public void executePlayerStrategy(Table table, TablePrinter tablePrinter,
                                      PlayerHand playerHand, DealerHand dealerHand) {

        Player actingPlayer = playerHand.getActingPlayer();

        while (!playerHand.isBust()) {
            String playerStrategy = actingPlayer.executeStrategy(playerHand, dealerHand);
            tablePrinter.printPlayerStrategy(playerStrategy);
            if (playerStrategy.equals(DOUBLE)) {
                table.handlePlayerAction(actingPlayer, playerHand, playerStrategy);
                break;
            } else if (playerStrategy.equals(STAND)) {
                break;
            } else {
                table.handlePlayerAction(actingPlayer, playerHand, playerStrategy);
            }
        }
        tablePrinter.printActivePlayerHands();
    }

    /** executes the player strategy for all active hands at the table. */
    public void executePlayerStrategyForAll(Table table, TablePrinter tablePrinter) {
        for (int i = 0; i < table.getActiveHands().size(); i++) {
            PlayerHand hand = table.getActiveHands().get(i);
            executePlayerStrategy(table, tablePrinter, hand, table.getDealerPosition().getHand());
        }
    }
}
