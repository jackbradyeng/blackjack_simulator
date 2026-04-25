package Model.Table.PayoutServices;

import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Bets.InsuranceBet;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;
import static Model.Constants.BLACKJACK_CONSTANT;
import static Model.Constants.DEFAULT_INSURANCE_RATIO;

@NoArgsConstructor
public class InsurancePayoutService implements PayoutService {

    /** processes the insurance payouts for each active hand at the table. */
    public void process(List<PlayerHand> activeHands, DealerHand dealerHand, Dealer dealer) {
        for (PlayerHand hand : activeHands) {
            for (Map.Entry<Player, Bet> pair : hand.getPairs()) {

                // skip non-insurance bets
                if (!(pair.getValue() instanceof InsuranceBet)) {
                    continue;
                }

                if (dealerHand.getHandValue() == BLACKJACK_CONSTANT && hand.hasInsuranceOption(dealerHand)) {
                    handlePlayerInsuranceWin(dealer, pair);
                } else {
                    handlePlayerInsuranceLoss(dealer, pair);
                }
            }
        }
    }

    public void handlePlayerInsuranceWin(Dealer dealer, Map.Entry<Player, Bet> pair) {
        double payout = pair.getValue().getAmount() * (1 + DEFAULT_INSURANCE_RATIO);
        dealer.dispenseChips(payout - pair.getValue().getAmount());
        pair.getKey().receiveChips(payout);
    }

    public void handlePlayerInsuranceLoss(Dealer dealer, Map.Entry<Player, Bet> pair) {
        dealer.receiveChips((pair.getValue().getAmount()));
    }
}
