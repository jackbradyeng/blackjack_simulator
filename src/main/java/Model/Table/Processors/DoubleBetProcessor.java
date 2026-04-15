package Model.Table.Processors;

import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Bets.DoubleBet;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Validators.DoubleBetValidators.DoubleBetValidatorImpl;

import static Model.Table.Validators.BetValidatorUtils.getOriginalBet;

public class DoubleBetProcessor implements BetProcessor {

    private final DoubleBetValidatorImpl doubleBetValidatorImpl;

    public DoubleBetProcessor(DoubleBetValidatorImpl doubleBetValidatorImpl) {
        this.doubleBetValidatorImpl = doubleBetValidatorImpl;
    }

    public void process() {
        if(doubleBetValidatorImpl.isValid()) {
            double amount = getOriginalBet(doubleBetValidatorImpl.getPlayer(), doubleBetValidatorImpl.getPlayerHand());
            bookBet(doubleBetValidatorImpl.getPlayer(), doubleBetValidatorImpl.getPlayerPosition(), amount);
        }
    }

    /** books a bet for a player on a given position for a given amount. */
    private void bookBet(Player player, PlayerPosition position, double amount) {
        Bet playerBet = new DoubleBet(amount);
        Map.Entry<Player, Bet> entry = Map.entry(player, playerBet);
        position.getHands().getFirst().getPairs().add(entry);
        player.dispenseChips(amount);
        System.out.println("Your double down bet has been placed! You have " + (int) player.getChips() +
                " chips remaining.");
    }
}
