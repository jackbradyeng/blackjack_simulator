package model.strategies.dealer_strategies;

import model.Table.hands.DealerHand;

public interface DealerStrategy {

    String executeStrategy(DealerHand dealerHand);
}
