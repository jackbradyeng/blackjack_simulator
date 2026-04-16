package Model.Table.Processors;

import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Bets.DoubleBet;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Validators.DoubleBetValidators.DoubleBetValidator;

import static Model.Table.Validators.BetValidatorUtils.getOriginalBet;

public class DoubleBetProcessor implements DoubleBetProcessorInterface {

    private final DoubleBetValidator doubleBetValidator;

    public DoubleBetProcessor(DoubleBetValidator doubleBetValidator) {
        this.doubleBetValidator = doubleBetValidator;
    }

    public void process(Player player, PlayerPosition playerPosition, PlayerHand playerHand) {
        if(doubleBetValidator.isValid()) {
            double amount = getOriginalBet(player, playerHand);
            bookBet(player, playerPosition, amount);
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
