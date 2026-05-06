package model.orchestrators.actor_strategy_orchestrators;

import model.actors.Player;
import model.Table.hands.DealerHand;
import model.Table.hands.PlayerHand;
import model.Table.Table;
import static model.Constants.DOUBLE;
import static model.Constants.STAND;

public class PlayerStrategyOrchestrator {

    /** executes the player's strategy. */
    public void executePlayerStrategy(Table table, PlayerHand playerHand, DealerHand dealerHand) {

        Player actingPlayer = playerHand.getActingPlayer();

        while (!playerHand.isBust()) {
            String playerStrategy = actingPlayer.executeStrategy(playerHand, dealerHand);
            if (playerStrategy.equals(DOUBLE)) {
                table.handlePlayerAction(actingPlayer, playerHand, playerStrategy);
                break;
            } else if (playerStrategy.equals(STAND)) {
                break;
            } else {
                table.handlePlayerAction(actingPlayer, playerHand, playerStrategy);
            }
        }
    }

    /** executes the player strategy for all active hands at the table. */
    public void executePlayerStrategyForAll(Table table) {
        for (int i = 0; i < table.getActiveHands().size(); i++) {
            PlayerHand hand = table.getActiveHands().get(i);
            executePlayerStrategy(table, hand, table.getDealerPosition().getHand());
        }
    }
}
