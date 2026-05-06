package model.strategies.player_strategies;

import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;
import static model.Constants.*;

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
