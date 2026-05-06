package hands;

import Model.Actors.Player;
import Model.Cards.Ace;
import Model.Cards.Card;
import Model.Table.Bets.Bet;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static Model.Constants.ACE_LOWER_VALUE;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerHandTests {

    private PlayerHand hand;
    private PlayerPosition position;

    @BeforeEach
    public void setUp() {
        position = new PlayerPosition(1);
        hand = new PlayerHand(position);
    }

    private Card card(String name, int value) {
        return new Card(name, value);
    }

    private Ace ace() {
        return new Ace("Ace", ACE_LOWER_VALUE);
    }

    private DealerHand dealerHandWithFirstCard(Card first) {
        DealerHand dealerHand = new DealerHand();
        dealerHand.receiveCard(first);
        return dealerHand;
    }

    // ================================
    // --- initialization ---
    // ================================

    @Test
    public void constructor_setsPosition() {
        assertSame(position, hand.getPosition());
    }

    @Test
    public void constructor_initializesPairsAsEmpty() {
        assertTrue(hand.getPairs().isEmpty());
    }

    @Test
    public void constructor_initializesCardsAsEmpty() {
        assertTrue(hand.getCards().isEmpty());
    }

    @Test
    public void constructor_actingPlayerIsNullByDefault() {
        assertNull(hand.getActingPlayer());
    }

    // ================================
    // --- hasBet ---
    // ================================

    @Test
    public void hasBet_returnsFalse_whenNoPairsExist() {
        assertFalse(hand.hasBet());
    }

    @Test
    public void hasBet_returnsTrue_whenOnePairAdded() {
        Player player = new Player(500, null);
        hand.getPairs().add(Map.entry(player, new Bet(25)));
        assertTrue(hand.hasBet());
    }

    @Test
    public void hasBet_returnsTrue_whenMultiplePairsAdded() {
        Player player1 = new Player(500, null);
        Player player2 = new Player(500, null);
        hand.getPairs().add(Map.entry(player1, new Bet(25)));
        hand.getPairs().add(Map.entry(player2, new Bet(50)));
        assertTrue(hand.hasBet());
    }

    // ================================
    // --- hasSplitOption ---
    // ================================

    @Test
    public void hasSplitOption_returnsFalse_forEmptyHand() {
        assertFalse(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsFalse_forSingleCard() {
        hand.receiveCard(card("Seven", 7));
        assertFalse(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsTrue_forTwoCardsWithSameValue() {
        hand.receiveCard(card("Seven", 7));
        hand.receiveCard(card("Seven", 7));
        assertTrue(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsFalse_forTwoCardsWithDifferentValues() {
        hand.receiveCard(card("Seven", 7));
        hand.receiveCard(card("Eight", 8));
        assertFalse(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsTrue_forTwoTenValueCards() {
        // King and Queen both have value 10 — a valid split target
        hand.receiveCard(card("King", 10));
        hand.receiveCard(card("Queen", 10));
        assertTrue(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsTrue_forTwoAces() {
        hand.receiveCard(ace());
        hand.receiveCard(ace());
        assertTrue(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsFalse_forThreeCardsEvenIfFirstTwoMatch() {
        // Split is only valid at exactly two cards
        hand.receiveCard(card("Seven", 7));
        hand.receiveCard(card("Seven", 7));
        hand.receiveCard(card("Three", 3));
        assertFalse(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsFalse_forThreeMatchingCards() {
        hand.receiveCard(card("Seven", 7));
        hand.receiveCard(card("Seven", 7));
        hand.receiveCard(card("Seven", 7));
        assertFalse(hand.hasSplitOption());
    }

    @Test
    public void hasSplitOption_returnsFalse_forAceAndNonAceWithSameNumericalValue() {
        // Ace has value 1, non-ace card with value 1 would match — but no standard card has value 1
        // King(10) and Ten(10) should split; Ace(1) and Two(2) should not
        hand.receiveCard(ace());
        hand.receiveCard(card("Two", 2));
        assertFalse(hand.hasSplitOption());
    }

    // ================================
    // --- hasInsuranceOption ---
    // ================================

    @Test
    public void hasInsuranceOption_returnsTrue_whenDealerFirstCardIsAce() {
        DealerHand dealerHand = dealerHandWithFirstCard(ace());
        assertTrue(hand.hasInsuranceOption(dealerHand));
    }

    @Test
    public void hasInsuranceOption_returnsFalse_whenDealerFirstCardIsKing() {
        DealerHand dealerHand = dealerHandWithFirstCard(card("King", 10));
        assertFalse(hand.hasInsuranceOption(dealerHand));
    }

    @Test
    public void hasInsuranceOption_returnsFalse_whenDealerFirstCardIsNumberCard() {
        DealerHand dealerHand = dealerHandWithFirstCard(card("Seven", 7));
        assertFalse(hand.hasInsuranceOption(dealerHand));
    }

    @Test
    public void hasInsuranceOption_returnsTrue_evenWhenDealerHasMultipleCards() {
        DealerHand dealerHand = new DealerHand();
        dealerHand.receiveCard(ace());
        dealerHand.receiveCard(card("King", 10));
        assertTrue(hand.hasInsuranceOption(dealerHand));
    }

    @Test
    public void hasInsuranceOption_returnsFalse_whenDealerFirstCardIsTen() {
        DealerHand dealerHand = dealerHandWithFirstCard(card("Ten", 10));
        assertFalse(hand.hasInsuranceOption(dealerHand));
    }

    // ================================
    // --- actingPlayer getter/setter ---
    // ================================

    @Test
    public void actingPlayer_canBeSet() {
        Player player = new Player(500, null);
        hand.setActingPlayer(player);
        assertSame(player, hand.getActingPlayer());
    }

    @Test
    public void actingPlayer_canBeOverwritten() {
        Player player1 = new Player(500, null);
        Player player2 = new Player(500, null);
        hand.setActingPlayer(player1);
        hand.setActingPlayer(player2);
        assertSame(player2, hand.getActingPlayer());
    }

    @Test
    public void actingPlayer_canBeSetToNull() {
        Player player = new Player(500, null);
        hand.setActingPlayer(player);
        hand.setActingPlayer(null);
        assertNull(hand.getActingPlayer());
    }

    // ================================
    // --- position getter/setter ---
    // ================================

    @Test
    public void position_canBeReassigned() {
        PlayerPosition newPosition = new PlayerPosition(2);
        hand.setPosition(newPosition);
        assertSame(newPosition, hand.getPosition());
    }

    // ================================
    // --- inherited Hand behaviour ---
    // ================================

    @Test
    public void receiveCard_addsCardInherited() {
        hand.receiveCard(card("King", 10));
        assertEquals(1, hand.getCards().size());
    }

    @Test
    public void setHandValue_propagatesToGetHandValue() {
        hand.receiveCard(ace());
        hand.receiveCard(card("King", 10));
        hand.setHandValue();
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void isBlackjack_trueForAceAndKing() {
        hand.receiveCard(ace());
        hand.receiveCard(card("King", 10));
        hand.setHandValue();
        assertTrue(hand.isBlackjack());
    }

    @Test
    public void isBust_trueWhenOver21() {
        hand.receiveCard(card("King", 10));
        hand.receiveCard(card("Queen", 10));
        hand.receiveCard(card("Five", 5));
        hand.setHandValue();
        assertTrue(hand.isBust());
    }
}