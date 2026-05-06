package model.Table.validators.insurance_bet_validators;

import java.util.Map;
import model.actors.Player;
import model.Table.bets.Bet;
import model.Table.bets.InsuranceBet;
import model.Table.hands.PlayerHand;
import static model.Table.validators.BetValidatorUtils.getOriginalBet;
import static model.Table.validators.BetValidatorUtils.hasExistingBet;
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
