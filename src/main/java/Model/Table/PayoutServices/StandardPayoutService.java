package Model.Table.PayoutServices;

import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Observers.TableStats;
import Model.Table.Bets.Bet;
import Model.Table.Bets.InsuranceBet;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;
import static Model.Constants.*;
import static Model.Constants.DEFAULT_PAYOUT_RATIO;

@AllArgsConstructor
public class StandardPayoutService implements PayoutService {

    private TableStats tableStats;

    public void process(List<PlayerHand> activeHands, DealerHand dealerHand, Dealer dealer) {
        for (PlayerHand hand : activeHands) {
            for (Map.Entry<Player, Bet> pair : hand.getPairs()) {

                // skip insurance bets
                if (pair.getValue() instanceof InsuranceBet) {
                    return;
                }
                // player loss case
                if (hand.isBust() || (!dealerHand.isBust() && dealerHand.getHandValue() > hand.getHandValue())) {
                    handlePlayerLoss(dealer, pair);
                }
                // player blackjack case
                else if (hand.getHandValue() == BLACKJACK_CONSTANT && hand.getCards().size() == 2) {
                    handlePlayerBlackjack(dealer, pair);
                }
                // player win case
                else if (!hand.isBust() && (dealerHand.isBust() || hand.getHandValue() > dealerHand.getHandValue())) {
                    handlePlayerWin(dealer, pair);
                }
                // player push case
                else {
                    handlePlayerPush(pair);
                }
            }
        }
    }

    /** processes a player's bet on a hand if it loses against the dealer. */
    public void handlePlayerLoss(Dealer dealer, Map.Entry<Player, Bet> pair) {
        dealer.receiveChips(pair.getValue().getAmount());
        tableStats.incrementPlayerLossCount();
    }

    public void handlePlayerBlackjack(Dealer dealer, Map.Entry<Player, Bet> pair) {
        double payout = pair.getValue().getAmount() * (1 +
                ((double) DEFAULT_BLACKJACK_PAYOUT_DENOMINATOR / DEFAULT_BLACKJACK_PAYOUT_NUMERATOR));
        dealer.dispenseChips(payout - pair.getValue().getAmount());
        pair.getKey().receiveChips(payout);
        tableStats.incrementBlackjackCount();
    }

    /** process a player's bet on a hand if it wins against the dealer. */
    public void handlePlayerWin(Dealer dealer, Map.Entry<Player, Bet> pair) {
        double payout = pair.getValue().getAmount() * (1 + DEFAULT_PAYOUT_RATIO);
        dealer.dispenseChips(payout - pair.getValue().getAmount());
        pair.getKey().receiveChips(payout);
        tableStats.incrementPlayerWinCount();
    }

    /** processes a player's bet on a hand if it pushes with the dealer. (i.e. the two are equal in value) */
    public void handlePlayerPush(Map.Entry<Player, Bet> pair) {
        pair.getKey().receiveChips(pair.getValue().getAmount());
        tableStats.incrementPushCount();
    }
}
