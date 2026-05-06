package actor_strategies;

import model.actors.Player;
import model.cards.Ace;
import model.cards.Card;
import model.strategies.player_strategies.OptimalNoCountingStrategy;
import model.Table.hands.DealerHand;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class OptimalNoCountingStrategyTests {

    private OptimalNoCountingStrategy strategy;
    private PlayerPosition position;

    @BeforeEach
    public void setUp() {
        strategy = new OptimalNoCountingStrategy();
        position = new PlayerPosition(1, new Player(500, null));
    }

    private PlayerHand makePlayerHand(Card... cards) {
        PlayerHand hand = new PlayerHand(position);
        for (Card c : cards) hand.receiveCard(c);
        hand.setHandValue();
        return hand;
    }

    private DealerHand makeDealerHand(int upcardValue) {
        DealerHand hand = new DealerHand();
        hand.receiveCard(new Card("Card", upcardValue));
        hand.setHandValue();
        return hand;
    }

    private Ace ace() {
        // value 11 required for split table compatibility (Ace-Ace split key is (11,11))
        return new Ace("Ace", ACE_UPPER_VALUE);
    }

    // ================================
    // --- hard values path ---
    // ================================

    @Test
    public void hardValue8_anyDealer_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Three", 3), new Card("Five", 5));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void hardValue9_dealer3_returnsDouble() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Five", 5));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(3)));
    }

    @Test
    public void hardValue9_dealer6_returnsDouble() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Five", 5));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void hardValue9_dealer2_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Five", 5));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(2)));
    }

    @Test
    public void hardValue9_dealer7_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Five", 5));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void hardValue10_dealer9_returnsDouble() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Six", 6));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(9)));
    }

    @Test
    public void hardValue10_dealer10_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Six", 6));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void hardValue11_anyDealer_returnsDouble() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Seven", 7));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void hardValue12_dealer4_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Eight", 8));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(4)));
    }

    @Test
    public void hardValue12_dealer6_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Eight", 8));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void hardValue12_dealer3_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Eight", 8));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(3)));
    }

    @Test
    public void hardValue12_dealer7_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Eight", 8));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void hardValue13_dealerUnder7_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Nine", 9));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void hardValue13_dealer7_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Nine", 9));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void hardValue16_dealerUnder7_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("Nine", 9));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(2)));
    }

    @Test
    public void hardValue16_dealer10_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("Nine", 9));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void hardValue17_anyDealer_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("King", 10));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void hardValue20_anyDealer_returnsStand() {
        PlayerHand hand;
        // 3 cards so no split check; value = 22 → bust... let me use 9+King = 19
        // Actually: use 3 cards: 7+3+10 = 20
        hand = makePlayerHand(new Card("Seven", 7), new Card("Three", 3), new Card("King", 10));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(9)));
    }

    // ================================
    // --- soft values path (Ace + 1 other card) ---
    // ================================

    @Test
    public void softValue13_dealer5_returnsDouble() {
        // Ace + 2 = soft 13
        PlayerHand hand = makePlayerHand(ace(), new Card("Two", 2));
        assertEquals(13, hand.getHandValue());
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(5)));
    }

    @Test
    public void softValue13_dealer6_returnsDouble() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Two", 2));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void softValue13_dealer4_returnsHit() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Two", 2));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(4)));
    }

    @Test
    public void softValue14_dealer5_returnsDouble() {
        // Ace + 3 = soft 14
        PlayerHand hand = makePlayerHand(ace(), new Card("Three", 3));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(5)));
    }

    @Test
    public void softValue14_dealer7_returnsHit() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Three", 3));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void softValue15_dealer4_returnsDouble() {
        // Ace + 4 = soft 15
        PlayerHand hand = makePlayerHand(ace(), new Card("Four", 4));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(4)));
    }

    @Test
    public void softValue15_dealer3_returnsHit() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Four", 4));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(3)));
    }

    @Test
    public void softValue16_dealer6_returnsDouble() {
        // Ace + 5 = soft 16
        PlayerHand hand = makePlayerHand(ace(), new Card("Five", 5));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void softValue16_dealer7_returnsHit() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Five", 5));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void softValue17_dealer3_returnsDouble() {
        // Ace + 6 = soft 17
        PlayerHand hand = makePlayerHand(ace(), new Card("Six", 6));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(3)));
    }

    @Test
    public void softValue17_dealer2_returnsHit() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Six", 6));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(2)));
    }

    @Test
    public void softValue17_dealer8_returnsHit() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Six", 6));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(8)));
    }

    @Test
    public void softValue17_dealer7_returnsDouble() {
        // dealer 7: not > 7, not == 2 → DOUBLE
        PlayerHand hand = makePlayerHand(ace(), new Card("Six", 6));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void softValue18_dealer2_returnsDouble() {
        // Ace + 7 = soft 18
        PlayerHand hand = makePlayerHand(ace(), new Card("Seven", 7));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(2)));
    }

    @Test
    public void softValue18_dealer7_returnsStand() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Seven", 7));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void softValue18_dealer8_returnsStand() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Seven", 7));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(8)));
    }

    @Test
    public void softValue18_dealer9_returnsHit() {
        PlayerHand hand = makePlayerHand(ace(), new Card("Seven", 7));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(9)));
    }

    @Test
    public void softValue19_anyDealer_returnsStand() {
        // Ace + 8 = soft 19
        PlayerHand hand = makePlayerHand(ace(), new Card("Eight", 8));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(5)));
    }

    @Test
    public void softValue20_anyDealer_returnsStand() {
        // Ace + 9 = soft 20
        PlayerHand hand = makePlayerHand(ace(), new Card("Nine", 9));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void aceWithMoreThan2Cards_usesHardValuesNotSoftValues() {
        // Ace + 2 + 3 = 16 (soft); 3 cards disqualifies soft-values path
        PlayerHand hand = makePlayerHand(ace(), new Card("Two", 2), new Card("Three", 3));
        assertEquals(16, hand.getHandValue());
        // hard 16 vs dealer 4 (< 7) → STAND
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(4)));
    }

    // ================================
    // --- splitting path ---
    // ================================

    @Test
    public void pairOf2s_dealerUnder8_returnsSplit() {
        PlayerHand hand = makePlayerHand(new Card("Two", 2), new Card("Two", 2));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void pairOf2s_dealer8_returnsHitViaSplitFallback() {
        // NO_SPLIT → hard values for 4 (< 9) → HIT
        PlayerHand hand = makePlayerHand(new Card("Two", 2), new Card("Two", 2));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(8)));
    }

    @Test
    public void pairOf3s_dealerUnder8_returnsSplit() {
        PlayerHand hand = makePlayerHand(new Card("Three", 3), new Card("Three", 3));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void pairOf4s_anyDealer_returnsHitViaSplitFallback() {
        // 4s: never split → hard values for 8 (< 9) → HIT
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Four", 4));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(5)));
    }

    @Test
    public void pairOf5s_dealerUnder10_returnsDoubleViaSplitFallback() {
        // 5s: never split → hard values for 10 vs dealer 6 (< 10) → DOUBLE
        PlayerHand hand = makePlayerHand(new Card("Five", 5), new Card("Five", 5));
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void pairOf6s_dealer6_returnsSplit() {
        PlayerHand hand = makePlayerHand(new Card("Six", 6), new Card("Six", 6));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void pairOf6s_dealer7_returnsHitViaSplitFallback() {
        // NO_SPLIT → hard values for 12 vs dealer 7 → HIT
        PlayerHand hand = makePlayerHand(new Card("Six", 6), new Card("Six", 6));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void pairOf7s_dealer7_returnsSplit() {
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("Seven", 7));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(7)));
    }

    @Test
    public void pairOf7s_dealer8_returnsHitViaSplitFallback() {
        // NO_SPLIT → hard values for 14 vs dealer 8 (>= 7) → HIT
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("Seven", 7));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(8)));
    }

    @Test
    public void pairOf8s_anyDealer_returnsSplit() {
        PlayerHand hand = makePlayerHand(new Card("Eight", 8), new Card("Eight", 8));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void pairOf8s_dealer2_returnsSplit() {
        PlayerHand hand = makePlayerHand(new Card("Eight", 8), new Card("Eight", 8));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(2)));
    }

    @Test
    public void pairOf9s_dealer9_returnsSplit() {
        PlayerHand hand = makePlayerHand(new Card("Nine", 9), new Card("Nine", 9));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(9)));
    }

    @Test
    public void pairOf9s_dealer10_returnsStandViaSplitFallback() {
        // NO_SPLIT → hard values for 18 (>= 17) → STAND
        PlayerHand hand = makePlayerHand(new Card("Nine", 9), new Card("Nine", 9));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void pairOf10s_anyDealer_returnsStandViaSplitFallback() {
        // 10s: never split → hard values for 20 (>= 17) → STAND
        PlayerHand hand = makePlayerHand(new Card("King", 10), new Card("King", 10));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(6)));
    }

    @Test
    public void pairOfAces_anyDealer_returnsSplit() {
        // Aces use value 11 to match split table key (ACE_UPPER_VALUE, ACE_UPPER_VALUE)
        PlayerHand hand = makePlayerHand(ace(), ace());
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(2)));
    }

    @Test
    public void pairOfAces_dealer10_returnsSplit() {
        PlayerHand hand = makePlayerHand(ace(), ace());
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(10)));
    }
}