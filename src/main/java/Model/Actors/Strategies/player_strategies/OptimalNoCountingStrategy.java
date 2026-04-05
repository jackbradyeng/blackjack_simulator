package Model.Actors.Strategies.player_strategies;

import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import static Model.Constants.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OptimalNoCountingStrategy implements PlayerStrategy {

    /** executes the player's strategy for a given hand and dealer face-up card. */
    public String executeStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        return optimalStrategy(playerHand, dealerHand);
    }

    /** mathematically optimal blackjack strategy without counting cards. */
    private String optimalStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        // if the two player cards are equal in value, first test to see if the action is a split
        if(playerHand.hasSplitOption()) {
            String action = OptimalStrategyUtility.executeSplittingStrategy(playerHand, dealerHand);
            if (action.equals(NO_SPLIT)) {
                return OptimalStrategyUtility.executeHardValuesStrategy(playerHand, dealerHand);
            } else {
                return action;
            }
        } else if(playerHand.hasAce() && playerHand.getCards().size() == 2) {
                return OptimalStrategyUtility.executeSoftValuesStrategy(playerHand, dealerHand);
        } else {
            return OptimalStrategyUtility.executeHardValuesStrategy(playerHand, dealerHand);
        }
    }
}
