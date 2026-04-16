package Model.Table.Validators;

import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Bets.InsuranceBet;
import Model.Table.Hands.PlayerHand;
import static Model.Table.Validators.BetValidatorUtils.getOriginalBet;
import static Model.Table.Validators.BetValidatorUtils.hasExistingBet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InsuranceBetValidatorImpl implements InsuranceBetValidatorInterface {

    private Player player;
    private PlayerHand playerHand;
    private double amount;

    public boolean isValid() {
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
