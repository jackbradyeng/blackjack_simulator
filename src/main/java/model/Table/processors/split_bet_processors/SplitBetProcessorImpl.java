package model.Table.processors.split_bet_processors;

import java.util.ArrayList;
import java.util.Map;
import model.actors.Player;
import model.Table.bets.Bet;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import lombok.NoArgsConstructor;

import static model.Table.validators.BetValidatorUtils.getOriginalBet;

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
