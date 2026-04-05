package Model.Strategies.dealer_strategies;

import Model.Table.Hands.DealerHand;
import static Model.Constants.*;

public class DefaultDealerStrategy implements DealerStrategy {

    /** makes the dealer hit up to a specific hand value. Once this value is reached, the dealer stands. */
    public String executeStrategy(DealerHand hand) {
        if(hand.getHandValue() == DEFAULT_DEALER_DRAW_VALUE && hand.hasAce()) {
            return HIT;
        } else if(hand.getHandValue() < DEFAULT_DEALER_DRAW_VALUE) {
            return HIT;
        } else {
            return STAND;
        }
    }
}
