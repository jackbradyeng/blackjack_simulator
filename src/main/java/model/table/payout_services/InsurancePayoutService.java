package model.table.payout_services;

import model.actors.Dealer;
import model.actors.Player;
import model.table.bets.Bet;
import model.table.bets.InsuranceBet;
import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;
import static model.Constants.BLACKJACK_CONSTANT;
import static model.Constants.DEFAULT_INSURANCE_RATIO;

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
