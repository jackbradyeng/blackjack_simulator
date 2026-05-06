package model.Table.betting_services;

import model.actors.Player;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import model.Table.processors.double_bet_processors.DoubleBetProcessor;
import model.Table.processors.double_bet_processors.DoubleBetProcessorImpl;
import model.Table.processors.insurance_bet_processors.InsuranceBetProcessor;
import model.Table.processors.insurance_bet_processors.InsuranceBetProcessorImpl;
import model.Table.processors.split_bet_processors.SplitBetProcessor;
import model.Table.processors.split_bet_processors.SplitBetProcessorImpl;
import model.Table.processors.standard_bet_processors.StandardBetProcessor;
import model.Table.processors.standard_bet_processors.StandardBetProcessorImpl;
import model.Table.validators.double_bet_validators.DoubleBetValidator;
import model.Table.validators.double_bet_validators.DoubleBetValidatorImpl;
import model.Table.validators.insurance_bet_validators.InsuranceBetValidator;
import model.Table.validators.insurance_bet_validators.InsuranceBetValidatorImpl;
import model.Table.validators.split_bet_validators.SplitBetValidator;
import model.Table.validators.split_bet_validators.SplitBetValidatorImpl;
import model.Table.validators.standard_bet_validators.StandardBetValidator;
import model.Table.validators.standard_bet_validators.StandardBetValidatorImpl;
import java.util.ArrayList;

public class BettingServiceImpl implements BettingService {

    private final boolean isSimulation;
    private final ArrayList<Player> players;
    private final ArrayList<PlayerPosition> playerPositionsIterable;
    private final DoubleBetProcessor doubleBetProcessor;
    private final DoubleBetValidator doubleBetValidator;
    private final InsuranceBetProcessor insuranceBetProcessor;
    private final InsuranceBetValidator insuranceBetValidator;
    private final StandardBetProcessor standardBetProcessor;
    private final StandardBetValidator standardBetValidator;
    private final SplitBetProcessor splitBetProcessor;
    private final SplitBetValidator splitBetValidator;

    public BettingServiceImpl(boolean isSimulation,
                              ArrayList<Player> players,
                              ArrayList<PlayerPosition> playerPositionsIterable) {

        this.isSimulation = isSimulation;
        this.players = players;
        this.playerPositionsIterable = playerPositionsIterable;
        this.doubleBetProcessor = new DoubleBetProcessorImpl();
        this.doubleBetValidator = new DoubleBetValidatorImpl();
        this.insuranceBetProcessor = new InsuranceBetProcessorImpl();
        this.insuranceBetValidator = new InsuranceBetValidatorImpl();
        this.standardBetProcessor = new StandardBetProcessorImpl();
        this.standardBetValidator = new StandardBetValidatorImpl();
        this.splitBetProcessor = new SplitBetProcessorImpl();
        this.splitBetValidator = new SplitBetValidatorImpl();
    }

    /** books a standard bet for a player on a given position for a given amount. To be called before the cards are
     * dealt. */
    @Override
    public boolean bookStandardBet(Player player, PlayerPosition position, double amount) {
        if (standardBetValidator.isValid(player, players, position, playerPositionsIterable, amount, isSimulation)) {
            standardBetProcessor.process(player, position, amount);
            return true;
        } else { return false; }
    }

    /** books an insurance bet for a player on a given position for a given amount. To be called AFTER the cards are
     * dealt. */
    @Override
    public boolean bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount) {
        if (insuranceBetValidator.isValid(player, hand, amount)) {
            insuranceBetProcessor.process(player, position, amount);
            return true;
        } else { return false; }
    }

    /** doubles the player's existing bet at a given position for that amount. Players can only double down once and if
     * they do, they can only hit one more time. If the player has already hit, they cannot double down. Also, if the
     * player has already made a natural blackjack, they cannot double down. */
    @Override
    public boolean bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand) {
        if (doubleBetValidator.isValid(player, players, position, playerPositionsIterable, hand, isSimulation)) {
            doubleBetProcessor.process(player, position, hand);
            return true;
        } else { return false; }
    }

    /** if the player's first and second cards are equal in value and if the player has chips remaining equal to the
     * size of their original bet, the hand is "split". Meaning that the second card is allocated to a new hand and
     * the player's new bet is associated with this hand. */
    @Override
    public boolean splitHand(Player player, PlayerPosition position, PlayerHand hand, ArrayList<PlayerHand> activeHands) {
        if (splitBetValidator.isValid(player, players, position, playerPositionsIterable, hand, isSimulation)) {
            splitBetProcessor.process(player, position, hand, activeHands);
            return true;
        } else { return false; }
    }
}
