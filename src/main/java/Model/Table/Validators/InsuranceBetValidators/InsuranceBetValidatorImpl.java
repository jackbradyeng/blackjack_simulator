package Model.Table.Validators.InsuranceBetValidators;

import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Bets.InsuranceBet;
import Model.Table.Hands.PlayerHand;
import static Model.Table.Validators.BetValidatorUtils.getOriginalBet;
import static Model.Table.Validators.BetValidatorUtils.hasExistingBet;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InsuranceBetValidatorImpl implements InsuranceBetValidator {

    public boolean isValid(Player player, PlayerHand playerHand, double amount) {
        return isValidInsuranceBet(player, playerHand, amount);
    }

    /** returns whether the given player has an insurance bet on the hand. */
    private boolean hasInsuranceBet(Player player, PlayerHand playerHand) {
        for (Map.Entry<Player, Bet> pair : playerHand.getPairs()) {
            if (pair.getKey().equals(player) && pair.getValue() instanceof InsuranceBet) {
                return true;
            }
        }
        return false;
    }

    /** validates a given insurance bet by verifying that a standard bet already exists on the selected position and
     * that the insurance bet amount is less than or equal to half the size of the standard bet. */
    private boolean isValidInsuranceBet(Player player, PlayerHand playerHand, double amount) {
        return hasExistingBet(player, playerHand)
                && !hasInsuranceBet(player, playerHand)
                && amount <= (getOriginalBet(player, playerHand) / 2);
    }
}
