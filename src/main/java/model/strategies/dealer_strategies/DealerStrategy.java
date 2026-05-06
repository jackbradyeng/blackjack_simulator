package model.strategies.dealer_strategies;

import model.table.hands.DealerHand;

public interface DealerStrategy {

    String executeStrategy(DealerHand dealerHand);
}
