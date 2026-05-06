package model.table.validators.standard_bet_validators;

import java.util.ArrayList;
import model.actors.Player;
import model.table.positions.PlayerPosition;
import static model.Constants.DEFAULT_MIN_BET_SIZE;
import static model.table.validators.BetValidatorUtils.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StandardBetValidatorImpl implements StandardBetValidator {

    /** determines if the given player, position, and bet amount are valid. */
    public boolean isValid(Player player,
                           ArrayList<Player> players,
                           PlayerPosition playerPosition,
                           ArrayList<PlayerPosition> playerPositions,
                           double amount,
                           boolean isSimulation) {

        if(isSimulation) {
            return isValidSimulationBet(player, players, playerPosition, playerPositions, amount);
        } else {
            return isValidStandardBet(player, players, playerPosition, playerPositions, amount);
        }
    }

    /** books a standard bet for a player on a given position for a given amount. To be called before the cards are
     * dealt. */
    private boolean isValidStandardBet(Player player,
                                       ArrayList<Player> players,
                                       PlayerPosition playerPosition,
                                       ArrayList<PlayerPosition> playerPositions,
                                       double amount) {

        return isValidPlayer(player, players) && isValidPosition(playerPosition, playerPositions) && isValidBetSize(amount)
                && hasSufficientChips(player, amount);
    }

    /** same as above but allows the player to overdraw on their stack. Required for collecting statistics such as
     * average profit per hand and expected value as these can be negative. */
    private boolean isValidSimulationBet(Player player,
                                         ArrayList<Player> players,
                                         PlayerPosition playerPosition,
                                         ArrayList<PlayerPosition> playerPositions,
                                         double amount) {

        return isValidPlayer(player, players) && isValidPosition(playerPosition, playerPositions) && isValidBetSize(amount);
    }

    /** validates a given bet size by verifying that it is greater than the minimum allowed for a standard bet. */
    private boolean isValidBetSize(double betAmount) {
        if(betAmount < DEFAULT_MIN_BET_SIZE) {
            return false;
        } else {
            return true;
        }
    }
}
