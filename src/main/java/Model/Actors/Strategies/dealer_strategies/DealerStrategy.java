package Model.Actors.Strategies.dealer_strategies;

import Model.Table.Hands.DealerHand;

public interface DealerStrategy {

    String executeStrategy(DealerHand dealerHand);
}
