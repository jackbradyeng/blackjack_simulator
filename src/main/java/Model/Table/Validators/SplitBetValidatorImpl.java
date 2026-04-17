package Model.Table.Validators;

import java.util.ArrayList;
import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import static Model.Table.Validators.BetValidatorUtils.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SplitBetValidatorImpl implements SplitBetValidator {

    public boolean isValid(Player player,
                           ArrayList<Player> players,
                           PlayerPosition playerPosition,
                           ArrayList<PlayerPosition> playerPositions,
                           PlayerHand playerHand,
                           boolean isSimulation) {

        if(isSimulation) {
            return isValidSimulationSplit(player, players, playerPosition, playerPositions, playerHand);
        } else {
            return isValidStandardSplit(player, players, playerPosition, playerPositions, playerHand);
        }
    }

    /** ensures that the player has an existing bet and that the hand's split option is live. */
    private boolean isValidSimulationSplit(Player player,
                                           ArrayList<Player> players,
                                           PlayerPosition playerPosition,
                                           ArrayList<PlayerPosition> playerPositions,
                                           PlayerHand playerHand) {

        return isValidPlayer(player, players)
                && isValidPosition(playerPosition, playerPositions)
                && hasExistingBet(player, playerHand)
                && hasNotHit(playerHand)
                && playerHand.hasSplitOption();
    }

    /** ensures that all the requirements for a simulation bet are met AND that the player has sufficient chips to post
     * the split bet. */
    private boolean isValidStandardSplit(Player player,
                                         ArrayList<Player> players,
                                         PlayerPosition playerPosition,
                                         ArrayList<PlayerPosition> playerPositions,
                                         PlayerHand playerHand) {

        return isValidSimulationSplit(player, players, playerPosition, playerPositions, playerHand)
                && hasSufficientChips(player, getOriginalBet(player, playerHand));
    }

    /** validates that the given hand has not yet been hit. Opening hands should have a size of two while split hands
     * should have a size of one. */
    private boolean hasNotHit(PlayerHand playerHand) {
        return playerHand.getCards().size() < 3;
    }
}
