package Model.Actors;

import Model.Actors.Strategies.player_strategies.PlayerStrategy;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class Player extends Actor {

    /** both the dealer and the players store a reference to their allocated position as well as a strategy object.
     * This strategy class determines how the actor will behave in certain conditions. */
    private PlayerPosition defaultPosition;
    private PlayerStrategy strategy;

    public Player(double startingChips, PlayerStrategy playerStrategy) {
        super(startingChips);
        this.strategy = playerStrategy;
    }

    /** given the player's hand and the dealer's hand, executes the player's assigned strategy from within the player
     * class. */
    public String executeStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        return getStrategy().executeStrategy(playerHand, dealerHand);
    }
}
