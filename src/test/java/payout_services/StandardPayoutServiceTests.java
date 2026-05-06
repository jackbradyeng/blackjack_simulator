package payout_services;

import model.actors.Dealer;
import model.actors.Player;
import model.cards.Ace;
import model.cards.Card;
import model.observers.TableStats;
import model.table.bets.Bet;
import model.table.bets.DoubleBet;
import model.table.bets.InsuranceBet;
import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;
import model.table.payout_services.StandardPayoutService;
import model.table.positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class StandardPayoutServiceTests {

    private StandardPayoutService service;
    private TableStats tableStats;
    private Player player;
    private Dealer dealer;
    private PlayerHand playerHand;
    private DealerHand dealerHand;
    private PlayerPosition position;

    private static final double BET_AMOUNT = 100.0;
    private static final double PLAYER_STARTING_CHIPS = 500.0;
    private static final double DEALER_STARTING_CHIPS = 5000.0;

    @BeforeEach
    public void setUp() {
        tableStats = new TableStats();
        service = new StandardPayoutService(tableStats);
        player = new Player(PLAYER_STARTING_CHIPS, null);
        dealer = new Dealer(null, DEALER_STARTING_CHIPS);
        position = new PlayerPosition(1, player);
        playerHand = new PlayerHand(position);
        dealerHand = new DealerHand();
    }

    private void givePlayerHandValue(int value) {
        playerHand.receiveCard(new Card("Test", value));
        playerHand.setHandValue();
    }

    private void givePlayerBlackjack() {
        playerHand.receiveCard(new Ace("Ace", 1));
        playerHand.receiveCard(new Card("King", 10));
        playerHand.setHandValue();
    }

    private void givePlayerBust() {
        playerHand.receiveCard(new Card("Ten", 10));
        playerHand.receiveCard(new Card("Ten", 10));
        playerHand.receiveCard(new Card("Five", 5));
        playerHand.setHandValue();
    }

    private void giveDealerHandValue(int value) {
        dealerHand.receiveCard(new Card("Test", value));
        dealerHand.setHandValue();
    }

    private void giveDealerBlackjack() {
        dealerHand.receiveCard(new Ace("Ace", 1));
        dealerHand.receiveCard(new Card("King", 10));
        dealerHand.setHandValue();
    }

    private void giveDealerBust() {
        dealerHand.receiveCard(new Card("Ten", 10));
        dealerHand.receiveCard(new Card("Ten", 10));
        dealerHand.receiveCard(new Card("Five", 5));
        dealerHand.setHandValue();
    }

    private Map.Entry<Player, Bet> makePair(Player p, Bet bet) {
        return Map.entry(p, bet);
    }

    // =====================
    // handlePlayerLoss Tests
    // =====================

    @Test
    public void handlePlayerLoss_transfersBetAmountToDealer() {
        service.handlePlayerLoss(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(DEALER_STARTING_CHIPS + BET_AMOUNT, dealer.getChips());
    }

    @Test
    public void handlePlayerLoss_incrementsPlayerLossCount() {
        service.handlePlayerLoss(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(1, tableStats.getPlayerLossCount());
    }

    @Test
    public void handlePlayerLoss_doesNotModifyPlayerChips() {
        service.handlePlayerLoss(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
    }

    @Test
    public void handlePlayerLoss_doesNotIncrementOtherStats() {
        service.handlePlayerLoss(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(0, tableStats.getPlayerWinCount());
        assertEquals(0, tableStats.getBlackjackCount());
        assertEquals(0, tableStats.getPushCount());
    }

    // ==========================
    // handlePlayerBlackjack Tests
    // ==========================

    @Test
    public void handlePlayerBlackjack_playerReceivesBlackjackPayout() {
        service.handlePlayerBlackjack(dealer, makePair(player, new Bet(BET_AMOUNT)));
        double expectedPayout = BET_AMOUNT * (1 + (double) DEFAULT_BLACKJACK_PAYOUT_DENOMINATOR / DEFAULT_BLACKJACK_PAYOUT_NUMERATOR);
        assertEquals(PLAYER_STARTING_CHIPS + expectedPayout, player.getChips());
    }

    @Test
    public void handlePlayerBlackjack_dealerDispensesBlackjackBonus() {
        service.handlePlayerBlackjack(dealer, makePair(player, new Bet(BET_AMOUNT)));
        double payout = BET_AMOUNT * (1 + (double) DEFAULT_BLACKJACK_PAYOUT_DENOMINATOR / DEFAULT_BLACKJACK_PAYOUT_NUMERATOR);
        assertEquals(DEALER_STARTING_CHIPS - (payout - BET_AMOUNT), dealer.getChips());
    }

    @Test
    public void handlePlayerBlackjack_playerReceivesStakePlusOnePointFiveTimesTheBet() {
        // Payout service returns the full payout (stake + winnings); bet deduction is the bet processor's concern.
        service.handlePlayerBlackjack(dealer, makePair(player, new Bet(BET_AMOUNT)));
        double received = player.getChips() - PLAYER_STARTING_CHIPS;
        assertEquals(BET_AMOUNT * 2.5, received);
    }

    @Test
    public void handlePlayerBlackjack_incrementsBlackjackCount() {
        service.handlePlayerBlackjack(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(1, tableStats.getBlackjackCount());
    }

    @Test
    public void handlePlayerBlackjack_doesNotIncrementOtherStats() {
        service.handlePlayerBlackjack(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(0, tableStats.getPlayerWinCount());
        assertEquals(0, tableStats.getPlayerLossCount());
        assertEquals(0, tableStats.getPushCount());
    }

    // ==========================
    // handlePlayerWin Tests
    // ==========================

    @Test
    public void handlePlayerWin_playerReceivesDoubleTheBet() {
        service.handlePlayerWin(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * (1 + DEFAULT_PAYOUT_RATIO), player.getChips());
    }

    @Test
    public void handlePlayerWin_dealerDispensesOnceTheBet() {
        service.handlePlayerWin(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(DEALER_STARTING_CHIPS - BET_AMOUNT, dealer.getChips());
    }

    @Test
    public void handlePlayerWin_playerReceivesStakePlusOneBetAmount() {
        // Payout service returns the full payout (stake + winnings); bet deduction is the bet processor's concern.
        service.handlePlayerWin(dealer, makePair(player, new Bet(BET_AMOUNT)));
        double received = player.getChips() - PLAYER_STARTING_CHIPS;
        assertEquals(BET_AMOUNT * 2, received);
    }

    @Test
    public void handlePlayerWin_incrementsPlayerWinCount() {
        service.handlePlayerWin(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(1, tableStats.getPlayerWinCount());
    }

    @Test
    public void handlePlayerWin_doesNotIncrementOtherStats() {
        service.handlePlayerWin(dealer, makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(0, tableStats.getBlackjackCount());
        assertEquals(0, tableStats.getPlayerLossCount());
        assertEquals(0, tableStats.getPushCount());
    }

    // ==========================
    // handlePlayerPush Tests
    // ==========================

    @Test
    public void handlePlayerPush_playerReceivesBetBack() {
        service.handlePlayerPush(makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT, player.getChips());
    }

    @Test
    public void handlePlayerPush_incrementsPushCount() {
        service.handlePlayerPush(makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(1, tableStats.getPushCount());
    }

    @Test
    public void handlePlayerPush_doesNotModifyDealerChips() {
        service.handlePlayerPush(makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(DEALER_STARTING_CHIPS, dealer.getChips());
    }

    @Test
    public void handlePlayerPush_doesNotIncrementOtherStats() {
        service.handlePlayerPush(makePair(player, new Bet(BET_AMOUNT)));
        assertEquals(0, tableStats.getPlayerWinCount());
        assertEquals(0, tableStats.getBlackjackCount());
        assertEquals(0, tableStats.getPlayerLossCount());
    }

    // ===========================
    // process() Routing Tests
    // ===========================

    @Test
    public void process_routesToLoss_whenPlayerHandBusts() {
        givePlayerBust();
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerLossCount());
    }

    @Test
    public void process_routesToLoss_whenDealerValueExceedsPlayerValue() {
        givePlayerHandValue(17);
        giveDealerHandValue(18);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerLossCount());
    }

    @Test
    public void process_routesToBlackjack_whenPlayerHas21WithTwoCards() {
        givePlayerBlackjack();
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getBlackjackCount());
        assertEquals(0, tableStats.getPlayerWinCount());
    }

    @Test
    public void process_routesToWin_whenPlayerValueExceedsDealerValue() {
        givePlayerHandValue(19);
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerWinCount());
    }

    @Test
    public void process_routesToWin_whenDealerBusts() {
        givePlayerHandValue(17);
        giveDealerBust();
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerWinCount());
    }

    @Test
    public void process_routesToPush_whenPlayerAndDealerHaveSameValue() {
        givePlayerHandValue(18);
        giveDealerHandValue(18);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPushCount());
    }

    @Test
    public void process_skipsInsuranceBets() {
        givePlayerHandValue(17);
        giveDealerHandValue(18);
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(0, tableStats.getPlayerLossCount());
        assertEquals(0, tableStats.getPlayerWinCount());
        assertEquals(0, tableStats.getPushCount());
        assertEquals(0, tableStats.getBlackjackCount());
    }

    @Test
    public void process_processesDoubleBetLikeRegularBet() {
        givePlayerHandValue(19);
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new DoubleBet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerWinCount());
    }

    @Test
    public void process_doesNothing_whenActiveHandsIsEmpty() {
        giveDealerHandValue(17);
        assertDoesNotThrow(() -> service.process(List.of(), dealerHand, dealer));
        assertEquals(0, tableStats.getPlayerWinCount() + tableStats.getPlayerLossCount()
                + tableStats.getBlackjackCount() + tableStats.getPushCount());
    }

    // ============================
    // process() Chip Amount Tests
    // ============================

    @Test
    public void process_loss_dealerReceivesBetAmount() {
        givePlayerHandValue(16);
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(DEALER_STARTING_CHIPS + BET_AMOUNT, dealer.getChips());
    }

    @Test
    public void process_blackjack_playerReceivesStakePlusOnePointFiveTimesTheBet() {
        givePlayerBlackjack();
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 2.5, player.getChips());
    }

    @Test
    public void process_win_playerReceivesStakePlusOneBetAmount() {
        givePlayerHandValue(18);
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 2, player.getChips());
    }

    @Test
    public void process_push_playerChipsRestoredByBetAmount() {
        givePlayerHandValue(18);
        giveDealerHandValue(18);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT, player.getChips());
        assertEquals(DEALER_STARTING_CHIPS, dealer.getChips());
    }

    // ============================
    // process() Edge Case Tests
    // ============================

    @Test
    public void process_routesToLoss_whenPlayerBustsEvenIfDealerAlsoBusts() {
        givePlayerBust();
        giveDealerBust();
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerLossCount());
        assertEquals(0, tableStats.getPlayerWinCount());
    }

    @Test
    public void process_routesToBlackjack_whenPlayerHasTwoCard21AndDealerAlsoHas21() {
        // The implementation awards a blackjack payout when the player has a 2-card 21,
        // regardless of whether the dealer also has 21.
        givePlayerBlackjack();
        giveDealerBlackjack();
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getBlackjackCount());
        assertEquals(0, tableStats.getPushCount());
    }

    @Test
    public void process_routesToWin_whenPlayerHasThreeCard21AndDealerHasLess() {
        playerHand.receiveCard(new Card("Seven", 7));
        playerHand.receiveCard(new Card("Seven", 7));
        playerHand.receiveCard(new Card("Seven", 7));
        playerHand.setHandValue();
        giveDealerHandValue(17);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerWinCount());
        assertEquals(0, tableStats.getBlackjackCount());
    }

    @Test
    public void process_routesToPush_whenPlayerHasThreeCard21AndDealerAlsoHas21() {
        playerHand.receiveCard(new Card("Seven", 7));
        playerHand.receiveCard(new Card("Seven", 7));
        playerHand.receiveCard(new Card("Seven", 7));
        playerHand.setHandValue();
        giveDealerHandValue(21);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPushCount());
        assertEquals(0, tableStats.getBlackjackCount());
    }

    @Test
    public void process_handlesMultiplePairsOnSameHand_allProcessed() {
        givePlayerHandValue(19);
        giveDealerHandValue(17);
        Player backBetter = new Player(PLAYER_STARTING_CHIPS, null);
        playerHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));
        playerHand.getPairs().add(makePair(backBetter, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(2, tableStats.getPlayerWinCount());
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 2, player.getChips());
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 2, backBetter.getChips());
    }

    @Test
    public void process_skipsInsuranceBet_butProcessesStandardBetOnSameHand() {
        givePlayerHandValue(17);
        giveDealerHandValue(18);
        playerHand.getPairs().add(makePair(player, new InsuranceBet(BET_AMOUNT)));
        Player backBetter = new Player(PLAYER_STARTING_CHIPS, null);
        playerHand.getPairs().add(makePair(backBetter, new Bet(BET_AMOUNT)));
        service.process(List.of(playerHand), dealerHand, dealer);
        assertEquals(1, tableStats.getPlayerLossCount());
        assertEquals(PLAYER_STARTING_CHIPS, player.getChips());
    }

    @Test
    public void process_handlesMultipleHands_eachProcessedIndependently() {
        PlayerHand winningHand = new PlayerHand(position);
        winningHand.receiveCard(new Card("Test", 19));
        winningHand.setHandValue();
        winningHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));

        Player losingPlayer = new Player(PLAYER_STARTING_CHIPS, null);
        PlayerPosition losingPosition = new PlayerPosition(2, losingPlayer);
        PlayerHand losingHand = new PlayerHand(losingPosition);
        losingHand.receiveCard(new Card("Test", 15));
        losingHand.setHandValue();
        losingHand.getPairs().add(makePair(losingPlayer, new Bet(BET_AMOUNT)));

        giveDealerHandValue(17);
        service.process(List.of(winningHand, losingHand), dealerHand, dealer);

        assertEquals(1, tableStats.getPlayerWinCount());
        assertEquals(1, tableStats.getPlayerLossCount());
        assertEquals(PLAYER_STARTING_CHIPS + BET_AMOUNT * 2, player.getChips());
        assertEquals(PLAYER_STARTING_CHIPS, losingPlayer.getChips());
    }

    @Test
    public void process_handlesMultipleHandsWithMixedOutcomes_statsAccumulate() {
        giveDealerHandValue(17);

        PlayerHand pushHand = new PlayerHand(position);
        pushHand.receiveCard(new Card("Test", 17));
        pushHand.setHandValue();
        pushHand.getPairs().add(makePair(player, new Bet(BET_AMOUNT)));

        Player bjPlayer = new Player(PLAYER_STARTING_CHIPS, null);
        PlayerPosition bjPosition = new PlayerPosition(2, bjPlayer);
        PlayerHand bjHand = new PlayerHand(bjPosition);
        bjHand.receiveCard(new Ace("Ace", 1));
        bjHand.receiveCard(new Card("King", 10));
        bjHand.setHandValue();
        bjHand.getPairs().add(makePair(bjPlayer, new Bet(BET_AMOUNT)));

        service.process(List.of(pushHand, bjHand), dealerHand, dealer);

        assertEquals(1, tableStats.getPushCount());
        assertEquals(1, tableStats.getBlackjackCount());
        assertEquals(0, tableStats.getPlayerWinCount());
        assertEquals(0, tableStats.getPlayerLossCount());
    }
}
