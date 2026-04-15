package Model.Table.Validators;

import java.util.ArrayList;
import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Bets.DoubleBet;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import static Model.Table.Validators.BetValidatorUtils.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DoubleBetValidatorImpl implements DoubleBetValidator {

    private Player player;
    private ArrayList<Player> players;
    private PlayerPosition playerPosition;
    private ArrayList<PlayerPosition> playerPositions;
    private PlayerHand playerHand;
    private boolean isSimulation;

    public boolean isValid() {

        if(isSimulation) {
            return isValidSimulationDoubleBet(player, players, playerPosition, playerPositions, playerHand);
        } else {
            return isValidDoubleBet(player, players, playerPosition, playerPositions, playerHand);
        }
    }

    /** ensures that the player has an existing bet and that they have not doubled already. */
    private boolean isValidSimulationDoubleBet(Player player,
                                               ArrayList<Player> players,
                                               PlayerPosition playerPosition,
                                               ArrayList<PlayerPosition> playerPositions,
                                               PlayerHand playerHand) {

        return isValidPlayer(player, players)
                && isValidPosition(playerPosition, playerPositions)
                && hasExistingBet(player, playerHand)
                && hasNotHit(playerHand)
                && hasNotDoubled(player, playerHand);
    }

    /** ensures that all the requirements for a simulation bet are met AND that the player has sufficient chips to post
     * the bet. */
    private boolean isValidDoubleBet(Player player,
                                     ArrayList<Player> players,
                                     PlayerPosition playerPosition,
                                     ArrayList<PlayerPosition> playerPositions,
                                     PlayerHand playerHand) {

        return isValidSimulationDoubleBet(player, players, playerPosition, playerPositions, playerHand)
                && hasSufficientChips(player, getOriginalBet(player, playerHand));
    }

    /** validates that the given hand has not yet been hit. Opening hands should have a size of two while split hands
     * should have a size of one. */
    private boolean hasNotHit(PlayerHand playerHand) {
        return playerHand.isHasHit();
    }

    /** validates that a particular player has not yet doubled on the given hand. */
    private boolean hasNotDoubled(Player player, PlayerHand playerHand) {
        for (Map.Entry<Player, Bet> pair : playerHand.getPairs()) {
            if (pair.getKey().equals(player) && pair.getValue() instanceof DoubleBet) {
                return false;
            }
        }
        return true;
    }
}
