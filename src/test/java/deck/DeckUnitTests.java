package deck;

import exceptions.DeckCountException;
import model.cards.Ace;
import model.cards.Card;
import model.deck.Deck;
import model.deck.shuffle_strategies.FisherYatesStrategy;
import model.deck.shuffle_strategies.ModifiedFisherYatesStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Optional;
import static model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class DeckUnitTests {

    private Deck deck;

    @BeforeEach
    public void setUp() {
        deck = new Deck(DEFAULT_NUMBER_OF_DECKS, new FisherYatesStrategy());
    }

    @Test
    public void testDeckCardCount() {
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT,
                deck.getDeck().size());
    }

    @Test
    public void testDealDecrementsDeckSize() {
        deck.deal();
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT - 1,
                deck.getDeck().size());
    }

    @Test
    public void testDealReturnsCard() {
        Optional<Card> card = deck.deal();
        assertTrue(card.isPresent());
        assertNotNull(card.get().getName());
        assertTrue(card.get().getValue() > 0);
    }

    @Test
    public void testDealEmptyDeckReturnsEmpty() {
        int total = DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT;
        for (int i = 0; i < total; i++) {
            deck.deal();
        }
        assertEquals(Optional.empty(), deck.deal());
    }

    @Test
    public void testInvalidCopiesThrowsDeckCountException() {
        assertThrows(DeckCountException.class, () -> new Deck(0, new FisherYatesStrategy()));
        assertThrows(DeckCountException.class, () -> new Deck(-1, new FisherYatesStrategy()));
    }

    @Test
    public void testCreateNewDeckRestoresFullSize() {
        deck.deal();
        deck.deal();
        deck.createNewDeck(DEFAULT_NUMBER_OF_DECKS);
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT,
                deck.getDeck().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 6})
    public void testDeckCardCountForVariousCopies(int copies) {
        Deck deck = new Deck(copies, new FisherYatesStrategy());
        assertEquals(copies * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT, deck.getDeck().size());
    }

    @Test
    public void testDeckContainsCorrectNumberOfAces() {
        long aceCount = deck.getDeck().stream().filter(c -> c instanceof Ace).count();
        assertEquals((long) DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS, aceCount);
    }

    @Test
    public void testDeckContainsCorrectNumberOfTenValueCards() {
        // Ten, Jack, Queen, and King cards all have a value of 10 — 4 per suit per copy
        long tensCount = deck.getDeck().stream().filter(c -> c.getValue() == 10).count();
        assertEquals((long) DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * 4, tensCount);
    }

    @Test
    public void testModifiedFisherYatesDeckSize() {
        Deck deck = new Deck(DEFAULT_NUMBER_OF_DECKS, new ModifiedFisherYatesStrategy());
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT,
                deck.getDeck().size());
    }

    @Test
    public void testModifiedFisherYatesDeal() {
        Deck d = new Deck(DEFAULT_NUMBER_OF_DECKS, new ModifiedFisherYatesStrategy());
        Optional<Card> card = d.deal();
        assertTrue(card.isPresent());
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT - 1,
                d.getDeck().size());
    }
}