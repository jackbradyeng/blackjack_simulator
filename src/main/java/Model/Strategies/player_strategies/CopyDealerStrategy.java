package Model.Strategies.player_strategies;

import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import static Model.Constants.*;

public class CopyDealerStrategy implements PlayerStrategy {

    /** a primitive player strategy designed to mirror the behavior of the dealer. */
    public String executeStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        if(playerHand.getHandValue() < DEFAULT_DEALER_DRAW_VALUE) {
            return HIT;
        } else {
            return STAND;
        }
    }
}
