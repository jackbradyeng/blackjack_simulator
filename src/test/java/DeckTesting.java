import Exceptions.DeckCountException;
import Model.Cards.Ace;
import Model.Cards.Card;
import Model.Deck.Deck;
import Model.Deck.ShuffleStrategies.FisherYatesStrategy;
import Model.Deck.ShuffleStrategies.ModifiedFisherYatesStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Optional;
import static Model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class DeckTesting {

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
}

    @Test
    public void testDealReturnsCard() {
        Optional<Card> card = deck.deal();
        assertTrue(card.isPresent());
        assertNotNull(card.get().getName());
        assertTrue(card.get().getValue() > 0);
    }

