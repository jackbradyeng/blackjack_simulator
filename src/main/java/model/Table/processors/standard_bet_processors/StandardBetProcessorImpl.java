package model.Table.processors.standard_bet_processors;

import java.util.Map;
import model.actors.Player;
import model.Table.bets.Bet;
import model.Table.positions.PlayerPosition;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StandardBetProcessorImpl implements StandardBetProcessor {

    public void process(Player player, PlayerPosition playerPosition, double amount) {
        bookBet(player, playerPosition, amount);
    }

    /** books a standard bet for a player on a given position for a given amount. To be called BEFORE the cards are
     * dealt. */
    private void bookBet(Player player, PlayerPosition position, double amount) {
        Bet playerBet = new Bet(amount);
        Map.Entry<Player, Bet> entry = Map.entry(player, playerBet);
            /* a key-value pair is stored in the hand's log so it can be accessed later when payouts are calculated and
            transferred. Also note: arraylists maintain insertion order so we can index the list by the position's
            number. */
        position.getHands().getFirst().getPairs().add(entry);
        player.dispenseChips(amount);
    }
}
