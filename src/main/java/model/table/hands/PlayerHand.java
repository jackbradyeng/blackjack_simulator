package model.table.hands;

import java.util.ArrayList;
import java.util.Map;
import model.actors.Player;
import model.cards.Ace;
import model.table.bets.Bet;
import model.table.positions.PlayerPosition;
import lombok.Getter;
import lombok.Setter;

public class PlayerHand extends Hand {

    // the position to which the hand is allocated.
    @Getter
    @Setter
    private PlayerPosition position;

    /* a list of player-bet pairs for each position. This is necessary because players can "back-bet" other player's hands. */
    @Getter
    private ArrayList<Map.Entry<Player, Bet>> pairs;

    /* The acting player is the player with agency in the hand. Other players may still "back-bet" the position, but
     * ultimately the acting player chooses the action. By default, this is the player assigned to the position. */
    @Getter
    @Setter
    private Player actingPlayer;

    public PlayerHand(PlayerPosition position) {
        super();
        this.position = position;
        this.pairs = new ArrayList<>();
    }

    /** returns whether the hand can be split. */
    public boolean hasSplitOption() {
        // robust check - player should not be able to split when they have three or more cards
        if(cards.size() == 2)
            return cards.get(0).getValue() == cards.get(1).getValue();
        else
            return false;
    }

    /** returns whether players can buy insurance on the hand. */
    public boolean hasInsuranceOption(DealerHand hand) {
        return hand.getCards().getFirst() instanceof Ace;
    }

    /** returns whether the hand has a bet placed on it. */
    public boolean hasBet() {
        return !pairs.isEmpty();
    }
}
