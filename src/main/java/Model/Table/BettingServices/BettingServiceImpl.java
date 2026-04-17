package Model.Table.BettingServices;

import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Processors.DoubleBetProcessors.DoubleBetProcessor;
import Model.Table.Processors.InsuranceBetProcessors.InsuranceBetProcessor;
import Model.Table.Processors.SplitBetProcessors.SplitBetProcessor;
import Model.Table.Processors.StandardBetProcessors.StandardBetProcessor;
import Model.Table.Validators.DoubleBetValidators.DoubleBetValidator;
import Model.Table.Validators.InsuranceBetValidators.InsuranceBetValidator;
import Model.Table.Validators.SplitBetValidators.SplitBetValidator;
import Model.Table.Validators.StandardBetValidators.StandardBetValidator;
import lombok.AllArgsConstructor;
import java.util.ArrayList;

@AllArgsConstructor
public class BettingServiceImpl implements BettingService {

    private boolean isSimulation;
    private ArrayList<Player> players;
    private ArrayList<PlayerPosition> playerPositionsIterable;
    private DoubleBetProcessor doubleBetProcessor;
    private DoubleBetValidator doubleBetValidator;
    private InsuranceBetValidator insuranceBetValidator;
    private InsuranceBetProcessor insuranceBetProcessor;
    private StandardBetProcessor standardBetProcessor;
    private StandardBetValidator standardBetValidator;
    private SplitBetProcessor splitBetProcessor;
    private SplitBetValidator splitBetValidator;

    /** books a standard bet for a player on a given position for a given amount. To be called before the cards are
     * dealt. */
    @Override
    public void bookStandardBet(Player player, PlayerPosition position, double amount) {
        if (standardBetValidator.isValid(player, players, position, playerPositionsIterable, amount, isSimulation)) {
            standardBetProcessor.process(player, position, amount);
        }
    }

    /** books an insurance bet for a player on a given position for a given amount. To be called AFTER the cards are
     * dealt. */
    @Override
    public void bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount) {
        if (insuranceBetValidator.isValid(player, hand, amount)) {
            insuranceBetProcessor.process(player, position, amount);
        }
    }

    /** doubles the player's existing bet at a given position for that amount. Players can only double down once and if
     * they do, they can only hit one more time. If the player has already hit, they cannot double down. Also, if the
     * player has already made a natural blackjack, they cannot double down. */
    @Override
    public void bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand) {
        if (doubleBetValidator.isValid(player, players, position, playerPositionsIterable, hand, isSimulation)) {
            doubleBetProcessor.process(player, position, hand);
        }
    }

    /** if the player's first and second cards are equal in value and if the player has chips remaining equal to the
     * size of their original bet, the hand is "split". Meaning that the second card is allocated to a new hand and
     * the player's new bet is associated with this hand. */
    @Override
    public void splitHand(Player player, PlayerPosition position, PlayerHand hand, ArrayList<PlayerHand> activeHands) {
        if (splitBetValidator.isValid(player, players, position, playerPositionsIterable, hand, isSimulation)) {
            splitBetProcessor.process(player, position, hand, activeHands);
        }
    }
}
