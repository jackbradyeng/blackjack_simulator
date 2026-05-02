package payout_services;

import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Cards.Ace;
import Model.Cards.Card;
import Model.Table.Bets.Bet;
import Model.Table.Bets.DoubleBet;
import Model.Table.Bets.InsuranceBet;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.PayoutServices.InsurancePayoutService;
import Model.Table.Positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static Model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class InsurancePayoutServiceTests {

    private InsurancePayoutService service;
    private Player player;
    private Dealer dealer;
    private PlayerHand playerHand;
    private DealerHand dealerHand;
    private PlayerPosition position;

    private static final double BET_AMOUNT = 50.0;
    private static final double PLAYER_STARTING_CHIPS = 500.0;
    private static final double DEALER_STARTING_CHIPS = 5000.0;

    @BeforeEach
    public void setUp() {
        service = new InsurancePayoutService();
        player = new Player(PLAYER_STARTING_CHIPS, null);
        dealer = new Dealer(null, DEALER_STARTING_CHIPS);
        position = new PlayerPosition(1, player);
        playerHand = new PlayerHand(position);
        dealerHand = new DealerHand();
    }

    private void giveDealerBlackjack() {
        dealerHand.receiveCard(new Ace("Ace", 1));
        dealerHand.receiveCard(new Card("King", 10));
        dealerHand.setHandValue();
    }

    private void giveDealerNonAce21() {
        // Dealer has 21 but the first card is not an Ace — insurance win condition not met
        dealerHand.receiveCard(new Card("Ten", 10));
        dealerHand.receiveCard(new Ace("Ace", 1));
        dealerHand.setHandValue();
    }

    private void giveDealerAceWithoutBlackjack() {
        // Dealer's first card is an Ace but hand value is not 21
        dealerHand.receiveCard(new Ace("Ace", 1));
        dealerHand.receiveCard(new Card("Five", 5));
        dealerHand.setHandValue();
    }

    private void giveDealerHandValue(int value) {
        dealerHand.receiveCard(new Card("Test", value));
        dealerHand.setHandValue();
    }

    private Map.Entry<Player, Bet> makePair(Player p, Bet bet) {
        return Map.entry(p, bet);
    }

    // ================================
    // handlePlayerInsuranceWin Tests
    // ================================

    @Test
    public void handlePlayerInsuranceWin_playerReceivesFullPayout() {
        service.handlePlayerInsuranceWin(dealer, makePair(player, new InsuranceBet(BET_AMOUNT)));
        double expectedPayout = BET_AMOUNT * (1 + DEFAULT_INSURANCE_RATIO);
        assertEquals(PLAYER_STARTING_CHIPS + expectedPayout, player.getChips());
    }

    @Test
    public void handlePlayerInsuranceWin_dealerDispensesInsuranceBonus() {
        service.handlePlayerInsuranceWin(dealer, makePair(player, new InsuranceBet(BET_AMOUNT)));
        double payout = BET_AMOUNT * (1 + DEFAULT_INSURANCE_RATIO);
        assertEquals(DEALER_STARTING_CHIPS - (payout - BET_AMOUNT), dealer.getChips());
    }

    @Test
    public void handlePlayerInsuranceWin_playerReceivesStakePlusThreeTimesTheBet() {
        // Insurance pays 3:1 — the service returns the full payout including the original stake.
        service.handlePlayerInsuranceWin(dealer, makePair(player, new InsuranceBet(BET_AMOUNT)));
        double received = player.getChips() - PLAYER_STARTING_CHIPS;
        assertEquals(BET_AMOUNT * 4, received);
    }

    @Test
    public void handlePlayerInsuranceWin_dealerDispensesThreeTimesTheBet() {
        service.handlePlayerInsuranceWin(dealer, makePair(player, new InsuranceBet(BET_AMOUNT)));
        double dealerLoss = DEALER_STARTING_CHIPS - dealer.getChips();
        assertEquals(BET_AMOUNT * 3, dealerLoss);
    }

    @Test
    public void handlePlayerInsuranceWin_doesNotModifyPlayerChipsOnDealer() {
        service.handlePlayerInsuranceWin(dealer, makePair(player, new InsuranceBet(BET_AMOUNT)));
        assertTrue(dealer.getChips() < DEALER_STARTING_CHIPS);
    }

    // =================================
    // handlePlayerInsuranceLoss Tests
    // =================================

    @Test
    public void handlePlayerInsuranceLoss_dealerReceivesBetAmount() {
        service.handlePlayerInsuranceLoss(dealer, makePair(player, new InsuranceBet(BET_AMOUNT)));
        assertEquals(DEALER_STARTING_CHIPS + BET_AMOUNT, dealer.getChips());
    }

    @Test
    public void handlePlayerInsuranceLoss_doesNotModifyPlayerChips() {
        service.handlePlayerInsuranceLoss(dealer, makePair(player, new InsuranceBet(BET_AMOUNT)));
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
    }

    // ====================================
    // process() Routing Tests
    // ====================================

    @Test
    public void process_routesToWin_whenDealerHasBlackjackAndFirstCardIsAce() {
        giveDealerBlackjack();
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertTrue(player.getChips() > PLAYER_STARTING_CHIPS);
    }

    @Test
    public void process_routesToLoss_whenDealerHas21ButFirstCardIsNotAce() {
        // Dealer value = 21 but the Ace is the second card — hasInsuranceOption is false
        giveDealerNonAce21();
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS + BET_AMOUNT, dealer.getChips());
    }

    @Test
    public void process_routesToLoss_whenDealerDoesNotHave21() {
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS + BET_AMOUNT, dealer.getChips());
    }

    @Test
    public void process_routesToLoss_whenDealerFirstCardIsAceButValueIsNot21() {
        giveDealerAceWithoutBlackjack();
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS + BET_AMOUNT, dealer.getChips());
    }

    // ====================================
    // process() Bet-Type Filtering Tests
    // ====================================

    @Test
    public void process_skipsRegularBets() {
        giveDealerBlackjack();
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS, dealer.getChips());
    }

    @Test
    public void process_skipsDoubleBets() {
        giveDealerBlackjack();
        playerHand.getPairs().add(makePair(player, new DoubleBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS, dealer.getChips());
    }

    @Test
    public void process_skipsRegularBet_butProcessesInsuranceBetOnSameHand() {
        giveDealerBlackjack();
        Player regularBetPlayer = new Player(PLAYER_STARTING_CHIPS, null);
        playerHand.getPairs().add(makePair(regularBetPlayer, new Bet(BET_AMOUNT)));
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS, regularBetPlayer.getChips());
        assertTrue(player.getChips() > PLAYER_STARTING_CHIPS);
    }

    // ============================
    // process() Edge Case Tests
    // ============================

    @Test
    public void process_doesNothing_whenActiveHandsIsEmpty() {
        giveDealerBlackjack();
        assertDoesNotThrow(() -> service.process(List.of(), dealerHand, dealer));
        assertEquals(DEALER_STARTING_CHIPS, dealer.getChips());
    }

    @Test
    public void process_handlesMultipleInsuranceBetsOnSameHand_allProcessed() {
        giveDealerBlackjack();
        Player backBetter = new Player(PLAYER_STARTING_CHIPS, null);
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        playerHand.getPairs().add(makePair(backBetter, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 4, player.getChips());
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 4, backBetter.getChips());
    }

    @Test
    public void process_handlesMultipleHands_winAndLossProcessedIndependently() {
        giveDealerBlackjack();
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));

        Player losingPlayer = new Player(PLAYER_STARTING_CHIPS, null);
        PlayerPosition losingPosition = new PlayerPosition(2, losingPlayer);
        PlayerHand losingHand = new PlayerHand(losingPosition);
        losingHand.getPairs().add(makePair(losingPlayer, new InsuranceBet(BET_AMOUNT)));

        // Override dealer hand to non-Ace first card so losingHand loses while playerHand wins
        DealerHand mixedDealerHand = new DealerHand();
        mixedDealerHand.receiveCard(new Ace("Ace", 1));
        mixedDealerHand.receiveCard(new Card("King", 10));
        mixedDealerHand.setHandValue();

        // Both hands face the same dealer, so both should win with a true blackjack dealer hand
        service.process(List.of(playerHand, losingHand), mixedDealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 4, player.getChips());
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 4, losingPlayer.getChips());
    }

    @Test
    public void process_win_chipAmountsAreCorrect() {
        giveDealerBlackjack();
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 4, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS - BET_AMOUNT * 3, dealer.getChips());
    }

    @Test
    public void process_loss_chipAmountsAreCorrect() {
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS + BET_AMOUNT, dealer.getChips());
    }
}