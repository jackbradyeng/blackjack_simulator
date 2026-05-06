package deal_services;

import model.actors.Player;
import model.cards.Card;
import model.deck.Deck;
import model.deck.shuffle_strategies.FisherYatesStrategy;
import model.table.bets.Bet;
import model.table.deal_services.DealServiceImpl;
import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;
import model.table.positions.DealerPosition;
import model.table.positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class DealServiceImplTests {

    private DealServiceImpl dealService;
    private Deck deck;
    private DealerPosition dealerPosition;
    private PlayerPosition position;
    private Player player;

    @BeforeEach
    public void setUp() {
        dealService = new DealServiceImpl();
        deck = new Deck(DEFAULT_NUMBER_OF_DECKS, new FisherYatesStrategy());
        dealerPosition = new DealerPosition();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        PlayerHand hand = new PlayerHand(position);
        hand.getPairs().add(Map.entry(player, new Bet(25)));
        position.getHands().add(hand);
    }

    // ================================
    // --- dealToDealer ---
    // ================================

    @Test
    public void dealToDealer_dealerHandReceivesOneCard() {
        dealService.dealToDealer(deck, dealerPosition);
        assertEquals(1, dealerPosition.getHand().getCards().size());
    }

    @Test
    public void dealToDealer_deckDecreasesInSizeByOne() {
        int before = deck.getDeck().size();
        dealService.dealToDealer(deck, dealerPosition);
        assertEquals(before - 1, deck.getDeck().size());
    }

    @Test
    public void dealToDealer_calledTwiceGivesDealerTwoCards() {
        dealService.dealToDealer(deck, dealerPosition);
        dealService.dealToDealer(deck, dealerPosition);
        assertEquals(2, dealerPosition.getHand().getCards().size());
    }

    @Test
    public void dealToDealer_cardDealtIsNotNull() {
        dealService.dealToDealer(deck, dealerPosition);
        assertNotNull(dealerPosition.getHand().getCards().getFirst());
    }

    @Test
    public void dealToDealer_emptyDeckDoesNotAddCardToDealer() {
        int total = deck.getDeck().size();
        for (int i = 0; i < total; i++) { deck.deal(); }
        dealService.dealToDealer(deck, dealerPosition);
        assertTrue(dealerPosition.getHand().getCards().isEmpty());
    }

    // ================================
    // --- dealToActivePositions ---
    // ================================

    @Test
    public void dealToActivePositions_handWithBetReceivesOneCard() {
        dealService.dealToActivePositions(deck, List.of(position));
        assertEquals(1, position.getHands().getFirst().getCards().size());
    }

    @Test
    public void dealToActivePositions_handWithNoBetReceivesNoCard() {
        PlayerPosition emptyPosition = new PlayerPosition(2);
        PlayerHand noBetHand = new PlayerHand(emptyPosition);
        emptyPosition.getHands().add(noBetHand);
        dealService.dealToActivePositions(deck, List.of(emptyPosition));
        assertTrue(emptyPosition.getHands().getFirst().getCards().isEmpty());
    }

    @Test
    public void dealToActivePositions_deckDecreasedByOnePerActiveHand() {
        Player player2 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        PlayerHand hand2 = new PlayerHand(pos2);
        hand2.getPairs().add(Map.entry(player2, new Bet(25)));
        pos2.getHands().add(hand2);
        int before = deck.getDeck().size();
        dealService.dealToActivePositions(deck, List.of(position, pos2));
        assertEquals(before - 2, deck.getDeck().size());
    }

    @Test
    public void dealToActivePositions_allActivePositionsReceiveOneCard() {
        Player player2 = new Player(500, null);
        Player player3 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        PlayerPosition pos3 = new PlayerPosition(3, player3);
        PlayerHand hand2 = new PlayerHand(pos2);
        hand2.getPairs().add(Map.entry(player2, new Bet(25)));
        pos2.getHands().add(hand2);
        PlayerHand hand3 = new PlayerHand(pos3);
        hand3.getPairs().add(Map.entry(player3, new Bet(25)));
        pos3.getHands().add(hand3);
        dealService.dealToActivePositions(deck, List.of(position, pos2, pos3));
        assertEquals(1, position.getHands().getFirst().getCards().size());
        assertEquals(1, pos2.getHands().getFirst().getCards().size());
        assertEquals(1, pos3.getHands().getFirst().getCards().size());
    }

    @Test
    public void dealToActivePositions_splitHandsWithBetsEachReceiveOneCard() {
        PlayerHand splitHand = new PlayerHand(position);
        splitHand.getPairs().add(Map.entry(player, new Bet(25)));
        position.getHands().add(splitHand);
        dealService.dealToActivePositions(deck, List.of(position));
        assertEquals(1, position.getHands().get(0).getCards().size());
        assertEquals(1, position.getHands().get(1).getCards().size());
    }

    @Test
    public void dealToActivePositions_splitHandWithNoBetIsSkipped() {
        PlayerHand splitHand = new PlayerHand(position); // no bet
        position.getHands().add(splitHand);
        dealService.dealToActivePositions(deck, List.of(position));
        assertEquals(1, position.getHands().get(0).getCards().size());
        assertTrue(position.getHands().get(1).getCards().isEmpty());
    }

    @Test
    public void dealToActivePositions_emptyPositionListDoesNotThrow() {
        assertDoesNotThrow(() -> dealService.dealToActivePositions(deck, List.of()));
    }

    @Test
    public void dealToActivePositions_emptyPositionListDoesNotChangeDeckSize() {
        int before = deck.getDeck().size();
        dealService.dealToActivePositions(deck, List.of());
        assertEquals(before, deck.getDeck().size());
    }

    @Test
    public void dealToActivePositions_positionWithNoHandsIsSkipped() {
        PlayerPosition posWithNoHands = new PlayerPosition(2);
        int before = deck.getDeck().size();
        dealService.dealToActivePositions(deck, List.of(posWithNoHands));
        assertEquals(before, deck.getDeck().size());
    }

    // ================================
    // --- dealOpeningCards ---
    // ================================

    @Test
    public void dealOpeningCards_eachActiveHandReceivesTwoCards() {
        dealService.dealOpeningCards(deck, dealerPosition, List.of(position));
        assertEquals(2, position.getHands().getFirst().getCards().size());
    }

    @Test
    public void dealOpeningCards_dealerHandReceivesTwoCards() {
        dealService.dealOpeningCards(deck, dealerPosition, List.of(position));
        assertEquals(2, dealerPosition.getHand().getCards().size());
    }

    @Test
    public void dealOpeningCards_deckDecreasedByTwoPerActiveHandPlusTwoForDealer() {
        int before = deck.getDeck().size();
        dealService.dealOpeningCards(deck, dealerPosition, List.of(position));
        assertEquals(before - 4, deck.getDeck().size());
    }

    @Test
    public void dealOpeningCards_multipleActivePositionsEachReceiveTwoCards() {
        Player player2 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        PlayerHand hand2 = new PlayerHand(pos2);
        hand2.getPairs().add(Map.entry(player2, new Bet(25)));
        pos2.getHands().add(hand2);
        dealService.dealOpeningCards(deck, dealerPosition, List.of(position, pos2));
        assertEquals(2, position.getHands().getFirst().getCards().size());
        assertEquals(2, pos2.getHands().getFirst().getCards().size());
        assertEquals(2, dealerPosition.getHand().getCards().size());
    }

    @Test
    public void dealOpeningCards_positionWithNoBetReceivesNoCards() {
        PlayerPosition emptyPosition = new PlayerPosition(2);
        PlayerHand unbettedHand = new PlayerHand(emptyPosition);
        emptyPosition.getHands().add(unbettedHand);
        dealService.dealOpeningCards(deck, dealerPosition, List.of(emptyPosition));
        assertTrue(emptyPosition.getHands().getFirst().getCards().isEmpty());
    }

    @Test
    public void dealOpeningCards_dealerStillReceivesTwoCardsWithNoActivePositions() {
        dealService.dealOpeningCards(deck, dealerPosition, List.of());
        assertEquals(2, dealerPosition.getHand().getCards().size());
    }

    @Test
    public void dealOpeningCards_deckDecreasedByTwoWhenNoActivePositions() {
        int before = deck.getDeck().size();
        dealService.dealOpeningCards(deck, dealerPosition, List.of());
        assertEquals(before - 2, deck.getDeck().size());
    }

    @Test
    public void dealOpeningCards_threeActivePositionsDrainsEightCardsFromDeck() {
        Player player2 = new Player(500, null);
        Player player3 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        PlayerPosition pos3 = new PlayerPosition(3, player3);
        PlayerHand hand2 = new PlayerHand(pos2);
        hand2.getPairs().add(Map.entry(player2, new Bet(25)));
        pos2.getHands().add(hand2);
        PlayerHand hand3 = new PlayerHand(pos3);
        hand3.getPairs().add(Map.entry(player3, new Bet(25)));
        pos3.getHands().add(hand3);
        int before = deck.getDeck().size();
        dealService.dealOpeningCards(deck, dealerPosition, List.of(position, pos2, pos3));
        assertEquals(before - 8, deck.getDeck().size()); // 3 players + dealer, 2 rounds
    }

    // ================================
    // --- calculateHandValues ---
    // ================================

    @Test
    public void calculateHandValues_setsHandValueOnPlayerHand() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Seven", 7));
        hand.receiveCard(new Card("Eight", 8));
        dealService.calculateHandValues(List.of(hand), dealerPosition);
        assertEquals(15, hand.getHandValue());
    }

    @Test
    public void calculateHandValues_setsHandValueOnDealerHand() {
        dealerPosition.getHand().receiveCard(new Card("King", 10));
        dealerPosition.getHand().receiveCard(new Card("Six", 6));
        dealService.calculateHandValues(List.of(), dealerPosition);
        assertEquals(16, dealerPosition.getHand().getHandValue());
    }

    @Test
    public void calculateHandValues_setsHandValueOnAllActiveHands() {
        Player player2 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        PlayerHand hand1 = position.getHands().getFirst();
        PlayerHand hand2 = new PlayerHand(pos2);
        hand1.receiveCard(new Card("Five", 5));
        hand1.receiveCard(new Card("Three", 3));
        hand2.receiveCard(new Card("Ten", 10));
        hand2.receiveCard(new Card("Nine", 9));
        dealService.calculateHandValues(List.of(hand1, hand2), dealerPosition);
        assertEquals(8, hand1.getHandValue());
        assertEquals(19, hand2.getHandValue());
    }

    @Test
    public void calculateHandValues_emptyHandHasValueZero() {
        PlayerHand hand = position.getHands().getFirst();
        dealService.calculateHandValues(List.of(hand), dealerPosition);
        assertEquals(0, hand.getHandValue());
    }

    @Test
    public void calculateHandValues_updatesExistingHandValueAfterNewCard() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Five", 5));
        hand.setHandValue();
        assertEquals(5, hand.getHandValue());
        hand.receiveCard(new Card("Eight", 8));
        dealService.calculateHandValues(List.of(hand), dealerPosition);
        assertEquals(13, hand.getHandValue());
    }

    @Test
    public void calculateHandValues_emptyActiveHandsListStillUpdatesDealerHandValue() {
        dealerPosition.getHand().receiveCard(new Card("Queen", 10));
        dealerPosition.getHand().receiveCard(new Card("Seven", 7));
        dealService.calculateHandValues(List.of(), dealerPosition);
        assertEquals(17, dealerPosition.getHand().getHandValue());
    }

    @Test
    public void calculateHandValues_dealerAlwaysUpdatedEvenWhenActiveHandsArePresent() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Nine", 9));
        dealerPosition.getHand().receiveCard(new Card("King", 10));
        dealerPosition.getHand().receiveCard(new Card("Five", 5));
        dealService.calculateHandValues(List.of(hand), dealerPosition);
        assertEquals(9, hand.getHandValue());
        assertEquals(15, dealerPosition.getHand().getHandValue());
    }

    // ================================
    // --- checkDeck ---
    // ================================

    @Test
    public void checkDeck_doesNotRebuildDeckWhenAboveThreshold() {
        int before = deck.getDeck().size();
        dealService.checkDeck(deck);
        assertEquals(before, deck.getDeck().size());
    }

    @Test
    public void checkDeck_rebuildsFullDeckWhenBelowThreshold() {
        int toDrain = deck.getDeck().size() - (NEW_DECK_THRESHOLD - 1);
        for (int i = 0; i < toDrain; i++) { deck.deal(); }
        assertTrue(deck.getDeck().size() < NEW_DECK_THRESHOLD);
        dealService.checkDeck(deck);
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_CARDS_PER_DECK, deck.getDeck().size());
    }

    @Test
    public void checkDeck_doesNotRebuildDeckWhenExactlyAtThreshold() {
        int toDrain = deck.getDeck().size() - NEW_DECK_THRESHOLD;
        for (int i = 0; i < toDrain; i++) { deck.deal(); }
        assertEquals(NEW_DECK_THRESHOLD, deck.getDeck().size());
        dealService.checkDeck(deck);
        assertEquals(NEW_DECK_THRESHOLD, deck.getDeck().size());
    }

    @Test
    public void checkDeck_rebuildsWhenDeckIsCompletelyEmpty() {
        int total = deck.getDeck().size();
        for (int i = 0; i < total; i++) { deck.deal(); }
        assertTrue(deck.getDeck().isEmpty());
        dealService.checkDeck(deck);
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_CARDS_PER_DECK, deck.getDeck().size());
    }

    @Test
    public void checkDeck_rebuiltDeckHasCorrectCardCount() {
        int toDrain = deck.getDeck().size() - (NEW_DECK_THRESHOLD - 1);
        for (int i = 0; i < toDrain; i++) { deck.deal(); }
        dealService.checkDeck(deck);
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT, deck.getDeck().size());
    }

    @Test
    public void checkDeck_rebuiltDeckIsDealt() {
        int toDrain = deck.getDeck().size() - (NEW_DECK_THRESHOLD - 1);
        for (int i = 0; i < toDrain; i++) { deck.deal(); }
        dealService.checkDeck(deck);
        assertTrue(deck.deal().isPresent());
    }

    // ================================
    // --- hit ---
    // ================================

    @Test
    public void hit_returnsTrueWhenHandIsNotBust() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Five", 5));
        hand.setHandValue();
        assertTrue(dealService.hit(deck, hand));
    }

    @Test
    public void hit_returnsFalseWhenHandIsBust() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Ten", 10));
        hand.receiveCard(new Card("Eight", 8));
        hand.receiveCard(new Card("Six", 6));
        hand.setHandValue();
        assertTrue(hand.isBust());
        assertFalse(dealService.hit(deck, hand));
    }

    @Test
    public void hit_handReceivesCardWhenNotBust() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Five", 5));
        hand.setHandValue();
        dealService.hit(deck, hand);
        assertEquals(2, hand.getCards().size());
    }

    @Test
    public void hit_handDoesNotReceiveCardWhenBust() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Ten", 10));
        hand.receiveCard(new Card("Eight", 8));
        hand.receiveCard(new Card("Six", 6));
        hand.setHandValue();
        int cardCount = hand.getCards().size();
        dealService.hit(deck, hand);
        assertEquals(cardCount, hand.getCards().size());
    }

    @Test
    public void hit_handValueIsUpdatedAfterHit() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Five", 5));
        hand.setHandValue();
        assertEquals(5, hand.getHandValue());
        dealService.hit(deck, hand);
        assertNotEquals(5, hand.getHandValue());
    }

    @Test
    public void hit_hasHitIsSetToTrueAfterSuccessfulHit() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Five", 5));
        hand.setHandValue();
        assertFalse(hand.isHasHit());
        dealService.hit(deck, hand);
        assertTrue(hand.isHasHit());
    }

    @Test
    public void hit_hasHitIsNotSetWhenHandIsBust() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Ten", 10));
        hand.receiveCard(new Card("Eight", 8));
        hand.receiveCard(new Card("Six", 6));
        hand.setHandValue();
        dealService.hit(deck, hand);
        assertFalse(hand.isHasHit());
    }

    @Test
    public void hit_deckSizeDecreasedByOneOnSuccessfulHit() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Five", 5));
        hand.setHandValue();
        int before = deck.getDeck().size();
        dealService.hit(deck, hand);
        assertEquals(before - 1, deck.getDeck().size());
    }

    @Test
    public void hit_deckSizeUnchangedWhenHandIsBust() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Ten", 10));
        hand.receiveCard(new Card("Eight", 8));
        hand.receiveCard(new Card("Six", 6));
        hand.setHandValue();
        int before = deck.getDeck().size();
        dealService.hit(deck, hand);
        assertEquals(before, deck.getDeck().size());
    }

    @Test
    public void hit_returnsTrueButNoCardAddedWhenDeckIsEmpty() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Five", 5));
        hand.setHandValue();
        int total = deck.getDeck().size();
        for (int i = 0; i < total; i++) { deck.deal(); }
        int cardsBefore = hand.getCards().size();
        boolean result = dealService.hit(deck, hand);
        assertTrue(result);
        assertEquals(cardsBefore, hand.getCards().size());
    }

    @Test
    public void hit_worksWithDealerHand() {
        DealerHand dealerHand = dealerPosition.getHand();
        dealerHand.receiveCard(new Card("Five", 5));
        dealerHand.setHandValue();
        boolean result = dealService.hit(deck, dealerHand);
        assertTrue(result);
        assertEquals(2, dealerHand.getCards().size());
    }

    @Test
    public void hit_handWithExactlyTwentyOneIsNotBust() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("King", 10));
        hand.receiveCard(new Card("Ace", 1));
        hand.setHandValue();
        assertFalse(hand.isBust());
        assertTrue(dealService.hit(deck, hand));
    }

    @Test
    public void hit_consecutiveHitsIncrementCardCountEachTime() {
        PlayerHand hand = position.getHands().getFirst();
        hand.receiveCard(new Card("Two", 2));
        hand.setHandValue();
        dealService.hit(deck, hand);
        dealService.hit(deck, hand);
        assertEquals(3, hand.getCards().size());
    }
}