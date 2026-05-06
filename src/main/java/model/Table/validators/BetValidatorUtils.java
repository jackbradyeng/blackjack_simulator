package model.Table.validators;

import model.actors.Player;
import model.Table.bets.Bet;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import java.util.ArrayList;
import java.util.Map;

public class BetValidatorUtils {

    /** validates a given player by verifying that they are registered at the table. */
    public static boolean isValidPlayer(Player player, ArrayList<Player> players) {
        for (Player playerFromList : players) {
            if (player.equals(playerFromList))
                return true;
        }
        return false;
    }

    /** validates a given position by verifying that it is registered at the table. */
    public static boolean isValidPosition(PlayerPosition position, ArrayList<PlayerPosition> playerPositions) {
        for (PlayerPosition positionFromList : playerPositions) {
            if (position.equals(positionFromList))
                return true;
        }
        return false;
    }

    /** validates that the player has sufficient chips to place a particular bet. */
    public static boolean hasSufficientChips(Player player, double amount) {
        return player.getChips() >= amount;
    }

    /** validates that the betting player has an existing bet on the given hand. */
    public static boolean hasExistingBet(Player player, PlayerHand playerHand) {
        return getOriginalBet(player, playerHand) != 0.0;
    }

    /** returns the amount corresponding to the player's original bet on the hand. */
    public static double getOriginalBet(Player player, PlayerHand hand) {
        for (Map.Entry<Player, Bet> pair : hand.getPairs()) {
            if (pair.getKey().equals(player)) {
                return pair.getValue().getAmount();
            }
        }
        return 0.0;
    }
}
