package Model.Table.Validators;

import java.util.ArrayList;
import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Bets.DoubleBet;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;

public class DoubleBetValidator extends BetValidator {

    protected Player player;
    protected PlayerPosition position;

    public DoubleBetValidator(boolean isSimulation, ArrayList<Player> players, ArrayList<PlayerPosition> playerPositions,
                                 Player player, PlayerPosition position, PlayerHand hand) {
        super(isSimulation, players, playerPositions, hand);
        this.player = player;
        this.position = position;
    }

    public boolean isValid() {
        if(isSimulation) {
            return isValidSimulationDoubleBet();
        } else {
            return isValidDoubleBet();
        }
    }

    /** ensures that the player has an existing bet and that they have not doubled already. */
    private boolean isValidSimulationDoubleBet() {
        return isValidPlayer(player) && isValidPosition(position) && hasExistingBet(player) && hasNotHit()
                && hasNotDoubled();
    }

    /** ensures that all the requirements for a simulation bet are met AND that the player has sufficient chips to post
     * the bet. */
    private boolean isValidDoubleBet() {
        return isValidSimulationDoubleBet() && hasSufficientChips(player, getOriginalBet(player)); // and has sufficient chips
    }

    /** validates that the given hand has not yet been hit. Opening hands should have a size of two while split hands
     * should have a size of one. */
    private boolean hasNotHit() {
        return hand.isHasHit();
    }

    /** validates that a particular player has not yet doubled on the given hand. */
    private boolean hasNotDoubled() {
        for(Map.Entry<Player, Bet> pair : hand.getPairs()) {
            if(pair.getKey().equals(player) && pair.getValue() instanceof DoubleBet) {
                System.out.println("Player has already used this action. Double down bet invalid.");
                return false;
            }
        }
        return true;
    }
}
