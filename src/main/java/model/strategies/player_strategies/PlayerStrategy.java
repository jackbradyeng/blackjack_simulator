package model.strategies.player_strategies;

import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;

public interface PlayerStrategy {

    String executeStrategy(PlayerHand playerHand, DealerHand dealerHand);
}
