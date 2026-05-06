package model.strategies.player_strategies;

import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;
import static model.Constants.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OptimalNoCountingStrategy implements PlayerStrategy {

    /** executes the player's strategy for a given hand and dealer face-up card. */
    public String executeStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        return optimalStrategy(playerHand, dealerHand);
    }

    /** mathematically optimal blackjack strategy without counting cards. */
    private String optimalStrategy(PlayerHand playerHand, DealerHand dealerHand) {

        if(playerHand.hasSplitOption()) {
            return handleSplittingCase(playerHand, dealerHand);

        } else if(playerHand.hasAce() && playerHand.getCards().size() == 2) {
                return OptimalStrategyUtility.executeSoftValuesStrategy(playerHand, dealerHand);

        } else {
            return OptimalStrategyUtility.executeHardValuesStrategy(playerHand, dealerHand);
        }
    }

    private String handleSplittingCase(PlayerHand playerHand, DealerHand dealerHand) {
        String action = OptimalStrategyUtility.executeSplittingStrategy(playerHand, dealerHand);
        if (action.equals(NO_SPLIT)) {
            return OptimalStrategyUtility.executeHardValuesStrategy(playerHand, dealerHand);
        } else {
            return action;
        }
    }
}
