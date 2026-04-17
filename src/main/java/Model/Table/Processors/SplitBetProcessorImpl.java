package Model.Table.Processors;

import java.util.ArrayList;
import java.util.Map;
import Model.Actors.Player;
import Model.Table.Bets.Bet;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import lombok.NoArgsConstructor;

import static Model.Table.Validators.BetValidatorUtils.getOriginalBet;

@NoArgsConstructor
public class SplitBetProcessorImpl implements SplitBetProcessor {

    public void process(Player player,
                        PlayerPosition playerPosition,
                        PlayerHand playerHand,
                        ArrayList<PlayerHand> activeHands) {

        double amount = getOriginalBet(player, playerHand);

        // creates new hand, bet, and pair instances for the split
        PlayerHand splitHand = new PlayerHand(playerPosition);
        Bet splitBet = new Bet(amount);
        Map.Entry<Player, Bet> splitPair = Map.entry(player, splitBet);

        // sets the acting player for the new hand to be the current player
        splitHand.setActingPlayer(player);

        // dispenses chips from the player for the new hand
        player.dispenseChips(amount);
        System.out.println("Hand split. Additional bet booked for " + (int) amount + " chips on second hand.");

        // removes the split card from the main hand, adds it to the new one
        splitHand.getCards().add(playerHand.getCards().removeLast());

        // resets the hit flag on the original hand
        playerHand.setHasHit(false);

        // updates the hand values of both hands
        splitHand.setHandValue();
        playerHand.setHandValue();

        // adds the new player-bet pair to the split hand
        splitHand.getPairs().add(splitPair);

        // places the split hand one place ahead of the original within the active hands instance
        activeHands.add(activeHands.indexOf(playerHand) + 1, splitHand);
    }
}
