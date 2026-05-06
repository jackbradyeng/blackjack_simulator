package actor_strategies;

import model.actors.Player;
import model.cards.Ace;
import model.cards.Card;
import model.strategies.player_strategies.OptimalNoCountingInsuranceStrategy;
import model.strategies.player_strategies.OptimalNoCountingStrategy;
import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;
import model.table.positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class OptimalNoCountingInsuranceStrategyTests {

    private OptimalNoCountingInsuranceStrategy strategy;
    private PlayerPosition position;

    @BeforeEach
    public void setUp() {
        strategy = new OptimalNoCountingInsuranceStrategy(new OptimalNoCountingStrategy());
        position = new PlayerPosition(1, new Player(500, null));
    }

    private PlayerHand makePlayerHand(Card... cards) {
        PlayerHand hand = new PlayerHand(position);
        for (Card c : cards) hand.receiveCard(c);
        hand.setHandValue();
        return hand;
    }

    private DealerHand makeDealerHandWithAce() {
        DealerHand hand = new DealerHand();
        hand.receiveCard(dealerAce());
        hand.setHandValue();
        return hand;
    }

    private DealerHand makeDealerHand(int upcardValue) {
        DealerHand hand = new DealerHand();
        hand.receiveCard(new Card("Card", upcardValue));
        hand.setHandValue();
        return hand;
    }

    /** player Ace using its lower value — correct for non-split hands */
    private Ace playerAce() {
        return new Ace("Ace", ACE_LOWER_VALUE);
    }

    /** dealer Ace — value 11 so hard/soft table lookups can reach it if needed */
    private Ace dealerAce() {
        return new Ace("Ace", ACE_UPPER_VALUE);
    }

    // ================================
    // --- insurance triggered ---
    // ================================

    @Test
    public void dealerShowsAce_returnsInsurance() {
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("King", 10));
        assertEquals(INSURANCE, strategy.executeStrategy(hand, makeDealerHandWithAce()));
    }

    @Test
    public void dealerShowsAce_insuranceTakesPriorityOverSplitOption() {
        // Player has a pair — split check comes after insurance check, so insurance wins
        PlayerHand hand = makePlayerHand(new Card("Eight", 8), new Card("Eight", 8));
        assertEquals(INSURANCE, strategy.executeStrategy(hand, makeDealerHandWithAce()));
    }

    @Test
    public void dealerShowsAce_insuranceTakesPriorityOverSoftValues() {
        // Player has soft hand (Ace + 7) — insurance check fires before soft-values lookup
        PlayerHand hand = makePlayerHand(playerAce(), new Card("Seven", 7));
        assertEquals(INSURANCE, strategy.executeStrategy(hand, makeDealerHandWithAce()));
    }

    // ================================
    // --- insurance not triggered ---
    // ================================

    @Test
    public void dealerShowsNonAce_delegatesToInnerStrategy() {
        // hard 17 vs dealer 10 → inner strategy returns STAND
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("King", 10));
        assertEquals(STAND, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void dealerShowsTen_returnsHitNotInsurance() {
        // hard 9 vs dealer 10 → inner strategy returns HIT
        PlayerHand hand = makePlayerHand(new Card("Four", 4), new Card("Five", 5));
        assertEquals(HIT, strategy.executeStrategy(hand, makeDealerHand(10)));
    }

    @Test
    public void dealerShowsFive_softHand_returnsDoubleNotInsurance() {
        // soft 18 (Ace + 7) vs dealer 5 → inner strategy returns DOUBLE
        PlayerHand hand = makePlayerHand(playerAce(), new Card("Seven", 7));
        assertEquals(18, hand.getHandValue());
        assertEquals(DOUBLE, strategy.executeStrategy(hand, makeDealerHand(5)));
    }

    @Test
    public void dealerShowsSix_pairOf8s_returnsSplitNotInsurance() {
        // 8+8 vs dealer 6 → inner strategy returns SPLIT
        PlayerHand hand = makePlayerHand(new Card("Eight", 8), new Card("Eight", 8));
        assertEquals(SPLIT, strategy.executeStrategy(hand, makeDealerHand(6)));
    }
}
