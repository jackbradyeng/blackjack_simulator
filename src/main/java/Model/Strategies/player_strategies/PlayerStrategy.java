package Model.Strategies.player_strategies;

import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;

public interface PlayerStrategy {

    String executeStrategy(PlayerHand playerHand, DealerHand dealerHand);
}
