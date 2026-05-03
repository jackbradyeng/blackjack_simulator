package actor_strategies;

import Model.Cards.Ace;
import Model.Cards.Card;
import Model.Strategies.dealer_strategies.DefaultDealerStrategy;
import Model.Table.Hands.DealerHand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static Model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultDealerStrategyTests {

    private DefaultDealerStrategy strategy;

    @BeforeEach
    public void setUp() {
        strategy = new DefaultDealerStrategy();
    }

    private DealerHand makeHand(Card... cards) {
        DealerHand hand = new DealerHand();
        for (Card c : cards) hand.receiveCard(c);
        hand.setHandValue();
        return hand;
    }

    private Ace ace() {
        return new Ace("Ace", ACE_LOWER_VALUE);
    }

    @Test
    public void emptyHand_returnsHit() {
        DealerHand hand = new DealerHand();
        assertEquals(HIT, strategy.executeStrategy(hand));
    }

    @Test
    public void handValueOf16_returnsHit() {
        DealerHand hand = makeHand(new Card("Seven", 7), new Card("Nine", 9));
        assertEquals(HIT, strategy.executeStrategy(hand));
    }

    @Test
    public void handValueBelow17WithAce_returnsHit() {
        // Ace + 5 = soft 16
        DealerHand hand = makeHand(ace(), new Card("Five", 5));
        assertEquals(16, hand.getHandValue());
        assertEquals(HIT, strategy.executeStrategy(hand));
    }

    @Test
    public void softHandValueOf17_returnsHit() {
        // Ace + 6 = soft 17 — dealer must hit on soft 17
        DealerHand hand = makeHand(ace(), new Card("Six", 6));
        assertEquals(17, hand.getHandValue());
        assertTrue(hand.hasAce());
        assertEquals(HIT, strategy.executeStrategy(hand));
    }

    @Test
    public void hardHandOf17ContainingAce_returnsHit() {
        // Ace + 6 + 10 = hard 17 (Ace counts as 1); hasAce() is still true
        DealerHand hand = makeHand(ace(), new Card("Six", 6), new Card("King", 10));
        assertEquals(17, hand.getHandValue());
        assertTrue(hand.hasAce());
        assertEquals(HIT, strategy.executeStrategy(hand));
    }

    @Test
    public void hardHandValueOf17_returnsStand() {
        DealerHand hand = makeHand(new Card("Eight", 8), new Card("Nine", 9));
        assertEquals(17, hand.getHandValue());
        assertFalse(hand.hasAce());
        assertEquals(STAND, strategy.executeStrategy(hand));
    }

    @Test
    public void handValueOf18_returnsStand() {
        DealerHand hand = makeHand(new Card("Nine", 9), new Card("Nine", 9));
        assertEquals(STAND, strategy.executeStrategy(hand));
    }

    @Test
    public void softHandValueOf18_returnsStand() {
        // Ace + 7 = soft 18; value != 17 so no special hit
        DealerHand hand = makeHand(ace(), new Card("Seven", 7));
        assertEquals(18, hand.getHandValue());
        assertEquals(STAND, strategy.executeStrategy(hand));
    }

    @Test
    public void handValueOf21_returnsStand() {
        DealerHand hand = makeHand(new Card("King", 10), new Card("Eight", 8), new Card("Three", 3));
        assertEquals(STAND, strategy.executeStrategy(hand));
    }

    @Test
    public void bustHand_returnsStand() {
        DealerHand hand = makeHand(new Card("Eight", 8), new Card("Eight", 8), new Card("Eight", 8));
        assertTrue(hand.isBust());
        assertEquals(STAND, strategy.executeStrategy(hand));
    }
}