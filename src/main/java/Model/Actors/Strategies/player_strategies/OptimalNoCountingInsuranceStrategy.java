package Model.Actors.Strategies.player_strategies;

import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import static Model.Constants.INSURANCE;
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
