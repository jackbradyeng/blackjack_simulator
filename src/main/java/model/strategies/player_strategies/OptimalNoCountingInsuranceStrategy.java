package model.strategies.player_strategies;

import model.Table.hands.DealerHand;
import model.Table.hands.PlayerHand;
import static model.Constants.INSURANCE;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OptimalNoCountingInsuranceStrategy implements PlayerStrategy {

    private final OptimalNoCountingStrategy optimalNoCountingStrategy;

    /** incorporates insurance into the hard values and splitting strategy. */
    public String executeStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        if(playerHand.hasInsuranceOption(dealerHand)) {
            return INSURANCE;
        } else {
            return optimalNoCountingStrategy.executeStrategy(playerHand, dealerHand);
        }
    }
}
