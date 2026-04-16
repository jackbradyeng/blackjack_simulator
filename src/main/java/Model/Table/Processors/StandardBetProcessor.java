package Model.Table.Processors;

import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Validators.StandardBetValidators.StandardBetValidatorImpl;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StandardBetProcessor implements StandardBetProcessorInterface {

    private final StandardBetValidatorImpl standardBetValidator;

    public void process(Player player, PlayerPosition playerPosition, double amount) {
        bookStandardBet(player, playerPosition, amount);
    }

    /** books a standard bet for a player on a given position for a given amount. To be called BEFORE the cards are
     * dealt. */
    private void bookStandardBet(Player player, PlayerPosition position, double amount) {
        if(standardBetValidator.isValid()) {
            bookBet(player, position, amount);
        }
    }

    /** books a bet for a player on a given position for a given amount. */
    private void bookBet(Player player, PlayerPosition position, double amount) {
        Bet playerBet = new Bet(amount);
        Map.Entry<Player, Bet> entry = Map.entry(player, playerBet);
            /* a key-value pair is stored in the hand's log so it can be accessed later when payouts are calculated and
            transferred. Also note: arraylists maintain insertion order so we can index the list by the position's
            number. */
        position.getHands().getFirst().getPairs().add(entry);
        player.dispenseChips(amount);
        System.out.println("Your bet has been placed! You have " + (int) player.getChips() +
                " chips remaining.");
    }
}
