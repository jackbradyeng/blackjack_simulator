package model.strategies.player_strategies;

import model.Table.hands.DealerHand;
import model.Table.hands.PlayerHand;

public interface PlayerStrategy {

    String executeStrategy(PlayerHand playerHand, DealerHand dealerHand);
}
