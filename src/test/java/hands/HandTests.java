package hands;

import Model.Cards.Ace;
import Model.Cards.Card;
import Model.Table.Hands.Hand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static Model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class HandTests {

    private Hand hand;

    @BeforeEach
    public void setUp() {
        hand = new Hand();
    }

    private Card card(String name, int value) {
        return new Card(name, value);
    }

    private Ace ace() {
        return new Ace("Ace", ACE_LOWER_VALUE);
    }

    private void deal(Card... cards) {
        for (Card c : cards) hand.receiveCard(c);
        hand.setHandValue();
    }

    // ================================
    // --- receiveCard ---
    // ================================

    @Test
    public void receiveCard_addsCardToEmptyHand() {
        hand.receiveCard(card("Seven", 7));
        assertEquals(1, hand.getCards().size());
    }

    @Test
    public void receiveCard_preservesInsertionOrder() {
        Card seven = card("Seven", 7);
        Card king  = card("King", 10);
        hand.receiveCard(seven);
        hand.receiveCard(king);
        assertSame(seven, hand.getCards().get(0));
        assertSame(king,  hand.getCards().get(1));
    }

    @Test
    public void receiveCard_canAddMultipleCards() {
        for (int i = 0; i < 5; i++) hand.receiveCard(card("Two", 2));
        assertEquals(5, hand.getCards().size());
    }

    // ================================
    // --- calculateHandValue: non-ace hands ---
    // ================================

    @Test
    public void handValue_singleNonAceCard() {
        deal(card("Seven", 7));
        assertEquals(7, hand.getHandValue());
    }

    @Test
    public void handValue_twoNonAceCards() {
        deal(card("Seven", 7), card("Eight", 8));
        assertEquals(15, hand.getHandValue());
    }

    @Test
    public void handValue_threeNonAceCards() {
        deal(card("Five", 5), card("Six", 6), card("Four", 4));
        assertEquals(15, hand.getHandValue());
    }

    @Test
    public void handValue_tenValueCards() {
        deal(card("King", 10), card("Queen", 10));
        assertEquals(20, hand.getHandValue());
    }

    @Test
    public void handValue_twentyOneWithThreeCards_noAces() {
        deal(card("Seven", 7), card("Seven", 7), card("Seven", 7));
        assertEquals(21, hand.getHandValue());
    }

    // ================================
    // --- calculateHandValue: single ace ---
    // ================================

    @Test
    public void handValue_aceAlone_countsAsEleven() {
        deal(ace());
        assertEquals(11, hand.getHandValue());
    }

    @Test
    public void handValue_aceFirst_thenSmallCard_usesUpperValue() {
        deal(ace(), card("Five", 5));
        assertEquals(16, hand.getHandValue());
    }

    @Test
    public void handValue_smallCard_thenAce_usesUpperValue() {
        deal(card("Five", 5), ace());
        assertEquals(16, hand.getHandValue());
    }

    @Test
    public void handValue_aceFirst_thenNineCard_makesTwenty() {
        deal(ace(), card("Nine", 9));
        assertEquals(20, hand.getHandValue());
    }

    @Test
    public void handValue_aceFirst_thenTenCard_makesTwentyOne() {
        deal(ace(), card("Ten", 10));
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_tenCard_thenAce_makesTwentyOne() {
        deal(card("Ten", 10), ace());
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_aceFirst_thenKing_makesTwentyOne() {
        deal(ace(), card("King", 10));
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_king_thenAce_makesTwentyOne() {
        deal(card("King", 10), ace());
        assertEquals(21, hand.getHandValue());
    }

    // ================================
    // --- calculateHandValue: ace forced to lower value ---
    // ================================

    @Test
    public void handValue_aceForcedToOne_aceFirst() {
        deal(ace(), card("King", 10), card("Five", 5));
        assertEquals(16, hand.getHandValue());
    }

    @Test
    public void handValue_aceForcedToOne_aceMiddle() {
        deal(card("King", 10), ace(), card("Five", 5));
        assertEquals(16, hand.getHandValue());
    }

    @Test
    public void handValue_aceForcedToOne_aceLast() {
        deal(card("King", 10), card("Five", 5), ace());
        assertEquals(16, hand.getHandValue());
    }

    @Test
    public void handValue_aceForcedToOne_exactBustBoundary() {
        // A(1) + 10 + K(10) = 21; upgrade would be 31 — ace stays at 1
        deal(ace(), card("Ten", 10), card("King", 10));
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_aceForcedToOne_fiveCardHand() {
        // A(1) + 2 + 3 + 4 + K(10) = 20; upgrade would be 30 — ace stays at 1
        deal(ace(), card("Two", 2), card("Three", 3), card("Four", 4), card("King", 10));
        assertEquals(20, hand.getHandValue());
    }

    // ================================
    // --- calculateHandValue: multiple aces ---
    // ================================

    @Test
    public void handValue_twoAces_oneCounted_asEleven() {
        // A(1)+A(1)=2, one upgrades to 11 → 12
        deal(ace(), ace());
        assertEquals(12, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_aceAceLast() {
        deal(card("Five", 5), ace(), ace());
        // 5+1+1=7, upgrade one → 17
        assertEquals(17, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withNineCard_makesTwentyOne() {
        // A(1)+A(1)+9=11, upgrade one → 21
        deal(ace(), ace(), card("Nine", 9));
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withNineCard_orderReversed() {
        deal(card("Nine", 9), ace(), ace());
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withTenCard_forcedToLower() {
        // A(1)+A(1)+10=12, upgrade would be 22 → no upgrade → 12
        deal(ace(), ace(), card("Ten", 10));
        assertEquals(12, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_thenNineAndTwo_bothForcedToLower() {
        // A(1)+A(1)+9+2=13, upgrade would be 23 → no upgrade → 13
        deal(ace(), ace(), card("Nine", 9), card("Two", 2));
        assertEquals(13, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withTwo_acesFirst() {
        // A(1)+A(1)+2=4, +10=14 ≤ 21 → 14
        deal(ace(), ace(), card("Two", 2));
        assertEquals(14, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withTwo_aceMiddle() {
        // A(1)+2+A(1)=4, +10=14
        deal(ace(), card("Two", 2), ace());
        assertEquals(14, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withTwo_acesLast() {
        // 2+A(1)+A(1)=4, +10=14
        deal(card("Two", 2), ace(), ace());
        assertEquals(14, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withEight_acesFirst() {
        // A(1)+A(1)+8=10, +10=20 ≤ 21 → 20
        deal(ace(), ace(), card("Eight", 8));
        assertEquals(20, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withEight_aceMiddle() {
        // A(1)+8+A(1)=10, +10=20
        deal(ace(), card("Eight", 8), ace());
        assertEquals(20, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withEight_acesLast() {
        // 8+A(1)+A(1)=10, +10=20
        deal(card("Eight", 8), ace(), ace());
        assertEquals(20, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withKing_aceMiddle_forcedToLower() {
        // A(1)+K(10)+A(1)=12, +10=22 > 21 → no upgrade → 12
        deal(ace(), card("King", 10), ace());
        assertEquals(12, hand.getHandValue());
    }

    @Test
    public void handValue_twoAces_withKing_acesLast_forcedToLower() {
        // K(10)+A(1)+A(1)=12, +10=22 > 21 → no upgrade → 12
        deal(card("King", 10), ace(), ace());
        assertEquals(12, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces() {
        // A+A+A: 3 × 1 = 3, upgrade one → 13
        deal(ace(), ace(), ace());
        assertEquals(13, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withSixCard() {
        // A(1)+A(1)+A(1)+6=9, upgrade one → 19
        deal(ace(), ace(), ace(), card("Six", 6));
        assertEquals(19, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withEightCard_forcedToLower() {
        // A(1)+A(1)+A(1)+8=11, upgrade would be 21 → upgrade → 21
        deal(ace(), ace(), ace(), card("Eight", 8));
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withNineCard_forcedToLower() {
        // A(1)+A(1)+A(1)+9=12, upgrade would be 22 → no upgrade → 12
        deal(ace(), ace(), ace(), card("Nine", 9));
        assertEquals(12, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withFive_acesFirst() {
        // A(1)+A(1)+A(1)+5=8, +10=18 ≤ 21 → 18
        deal(ace(), ace(), ace(), card("Five", 5));
        assertEquals(18, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withFive_cardFirst() {
        // 5+A(1)+A(1)+A(1)=8, +10=18
        deal(card("Five", 5), ace(), ace(), ace());
        assertEquals(18, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withFive_cardMiddle() {
        // A(1)+5+A(1)+A(1)=8, +10=18
        deal(ace(), card("Five", 5), ace(), ace());
        assertEquals(18, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withFive_cardBetweenSecondAndThirdAce() {
        // A(1)+A(1)+5+A(1)=8, +10=18
        deal(ace(), ace(), card("Five", 5), ace());
        assertEquals(18, hand.getHandValue());
    }

    @Test
    public void handValue_threeAces_withSeven_makesTwenty() {
        // A(1)+A(1)+A(1)+7=10, +10=20 ≤ 21 → 20
        deal(ace(), ace(), ace(), card("Seven", 7));
        assertEquals(20, hand.getHandValue());
    }

    @Test
    public void handValue_fourAces() {
        // 4 × 1 = 4, upgrade one → 14
        deal(ace(), ace(), ace(), ace());
        assertEquals(14, hand.getHandValue());
    }

    @Test
    public void handValue_fourAces_withSevenCard_forcedToLower() {
        // 4+7=11, upgrade would be 21 → upgrade → 21
        deal(ace(), ace(), ace(), ace(), card("Seven", 7));
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_fourAces_withEightCard_forcedToLower() {
        // 4+8=12, upgrade would be 22 → no upgrade → 12
        deal(ace(), ace(), ace(), ace(), card("Eight", 8));
        assertEquals(12, hand.getHandValue());
    }

    @Test
    public void handValue_fourAces_withTwo() {
        // A(1)+A(1)+A(1)+A(1)+2=6, +10=16 ≤ 21 → 16
        deal(ace(), ace(), ace(), ace(), card("Two", 2));
        assertEquals(16, hand.getHandValue());
    }

    @Test
    public void handValue_fourAces_withSix_makesTwenty() {
        // A(1)+A(1)+A(1)+A(1)+6=10, +10=20 ≤ 21 → 20
        deal(ace(), ace(), ace(), ace(), card("Six", 6));
        assertEquals(20, hand.getHandValue());
    }

    @Test
    public void handValue_fourAces_withTwo_cardFirst() {
        // 2+A(1)+A(1)+A(1)+A(1)=6, +10=16 — ordering should not affect result
        deal(card("Two", 2), ace(), ace(), ace(), ace());
        assertEquals(16, hand.getHandValue());
    }

    @Test
    public void handValue_aceInMiddleOfMultipleNonAces_upgrades() {
        // 3 + A(1) + 4 = 8, upgrade → 18
        deal(card("Three", 3), ace(), card("Four", 4));
        assertEquals(18, hand.getHandValue());
    }

    @Test
    public void handValue_aceLastAfterTwoNonAces_upgrades() {
        // 6 + 4 + A(1) = 11, upgrade → 21
        deal(card("Six", 6), card("Four", 4), ace());
        assertEquals(21, hand.getHandValue());
    }

    @Test
    public void handValue_aceLastAfterTwoNonAces_forcedToLower() {
        // 6 + 5 + A(1) = 12, upgrade would be 22 → no upgrade → 12
        deal(card("Six", 6), card("Five", 5), ace());
        assertEquals(12, hand.getHandValue());
    }

    // ================================
    // --- isBust ---
    // ================================

    @Test
    public void isBust_returnsFalse_whenHandValueIsTwentyOne() {
        deal(card("King", 10), card("Ace", 11)); // just to set handValue
        hand.getCards().clear();
        deal(card("Ten", 10), card("Seven", 7), card("Four", 4));
        assertEquals(21, hand.getHandValue());
        assertFalse(hand.isBust());
    }

    @Test
    public void isBust_returnsTrue_whenHandValueIsTwentyTwo() {
        deal(card("King", 10), card("Eight", 8), card("Four", 4));
        assertEquals(22, hand.getHandValue());
        assertTrue(hand.isBust());
    }

    @Test
    public void isBust_returnsTrue_onHighBust() {
        deal(card("King", 10), card("Queen", 10), card("Jack", 10));
        assertEquals(30, hand.getHandValue());
        assertTrue(hand.isBust());
    }

    @Test
    public void isBust_returnsFalse_forAceThatSavesHand() {
        // A(1) + K(10) + 9 = 20 — ace stays low, no bust
        deal(ace(), card("King", 10), card("Nine", 9));
        assertEquals(20, hand.getHandValue());
        assertFalse(hand.isBust());
    }

    @Test
    public void isBust_returnsFalse_forHandValueBelow21() {
        deal(card("Five", 5), card("Six", 6));
        assertFalse(hand.isBust());
    }

    // ================================
    // --- isBlackjack ---
    // ================================

    @Test
    public void isBlackjack_returnsTrue_aceAndKing() {
        deal(ace(), card("King", 10));
        assertTrue(hand.isBlackjack());
    }

    @Test
    public void isBlackjack_returnsTrue_aceAndTen() {
        deal(ace(), card("Ten", 10));
        assertTrue(hand.isBlackjack());
    }

    @Test
    public void isBlackjack_returnsTrue_kingAndAce() {
        deal(card("King", 10), ace());
        assertTrue(hand.isBlackjack());
    }

    @Test
    public void isBlackjack_returnsFalse_twentyOneWithThreeCards() {
        deal(card("Seven", 7), card("Seven", 7), card("Seven", 7));
        assertFalse(hand.isBlackjack());
    }

    @Test
    public void isBlackjack_returnsFalse_twoAcesPlusNine() {
        // value is 21 but 3 cards
        deal(ace(), ace(), card("Nine", 9));
        assertFalse(hand.isBlackjack());
    }

    @Test
    public void isBlackjack_returnsFalse_nonTwentyOneHand() {
        deal(card("Five", 5), card("King", 10));
        assertFalse(hand.isBlackjack());
    }

    @Test
    public void isBlackjack_returnsFalse_emptyHand() {
        hand.setHandValue();
        assertFalse(hand.isBlackjack());
    }

    // ================================
    // --- hasAce ---
    // ================================

    @Test
    public void hasAce_returnsFalse_forEmptyHand() {
        assertFalse(hand.hasAce());
    }

    @Test
    public void hasAce_returnsFalse_forHandWithNoAce() {
        hand.receiveCard(card("King", 10));
        hand.receiveCard(card("Seven", 7));
        assertFalse(hand.hasAce());
    }

    @Test
    public void hasAce_returnsTrue_forSingleAce() {
        hand.receiveCard(ace());
        assertTrue(hand.hasAce());
    }

    @Test
    public void hasAce_returnsTrue_forAceAmongOtherCards() {
        hand.receiveCard(card("King", 10));
        hand.receiveCard(ace());
        hand.receiveCard(card("Three", 3));
        assertTrue(hand.hasAce());
    }

    @Test
    public void hasAce_returnsTrue_forMultipleAces() {
        hand.receiveCard(ace());
        hand.receiveCard(ace());
        assertTrue(hand.hasAce());
    }

    // ================================
    // --- hasHit ---
    // ================================

    @Test
    public void hasHit_isFalseByDefault() {
        assertFalse(hand.isHasHit());
    }

    @Test
    public void hasHit_canBeSetToTrue() {
        hand.setHasHit(true);
        assertTrue(hand.isHasHit());
    }

    @Test
    public void hasHit_canBeSetBackToFalse() {
        hand.setHasHit(true);
        hand.setHasHit(false);
        assertFalse(hand.isHasHit());
    }

    // ================================
    // --- toString ---
    // ================================

    @Test
    public void toString_returnsEmptyString_forEmptyHand() {
        assertEquals("", hand.toString());
    }

    @Test
    public void toString_containsCardName() {
        hand.receiveCard(card("King", 10));
        assertTrue(hand.toString().contains("King"));
    }

    @Test
    public void toString_containsAllCardNames() {
        hand.receiveCard(card("King", 10));
        hand.receiveCard(card("Seven", 7));
        String result = hand.toString();
        assertTrue(result.contains("King"));
        assertTrue(result.contains("Seven"));
    }
}
