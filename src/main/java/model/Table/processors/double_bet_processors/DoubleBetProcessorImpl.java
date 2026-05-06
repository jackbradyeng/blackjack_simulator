package model.Table.processors.double_bet_processors;

import java.util.Map;
import model.actors.Player;
import model.Table.bets.Bet;
import model.Table.bets.DoubleBet;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import static model.Table.validators.BetValidatorUtils.getOriginalBet;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DoubleBetProcessorImpl implements DoubleBetProcessor {

    public void process(Player player, PlayerPosition playerPosition, PlayerHand playerHand) {
        double amount = getOriginalBet(player, playerHand);
        bookBet(player, playerPosition, amount);
    }

    /** books a bet for a player on a given position for a given amount. */
    private void bookBet(Player player, PlayerPosition position, double amount) {
        Bet playerBet = new DoubleBet(amount);
        Map.Entry<Player, Bet> entry = Map.entry(player, playerBet);
        position.getHands().getFirst().getPairs().add(entry);
        player.dispenseChips(amount);
    }
}
