package model.table.processors.insurance_bet_processors;

import java.util.Map;
import model.actors.Player;
import model.table.bets.Bet;
import model.table.bets.InsuranceBet;
import model.table.positions.PlayerPosition;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InsuranceBetProcessorImpl implements InsuranceBetProcessor {

    public void process(Player player, PlayerPosition playerPosition, double amount) {
        bookBet(player, playerPosition, amount);
    }

    /** books an insurance bet for a player on a given position for a given amount. To be called AFTER the cards are
     * dealt. */
    private void bookBet(Player player, PlayerPosition position, double amount) {
        InsuranceBet iBet = new InsuranceBet(amount);
        Map.Entry<Player, Bet> entry = Map.entry(player, iBet);
        position.getHands().getFirst().getPairs().add(entry);
        player.dispenseChips(amount);
    }
}
