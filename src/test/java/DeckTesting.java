import Model.Deck.Deck;
import Model.Deck.ShuffleStrategies.FisherYatesStrategy;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static Model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeckTesting {

    private final Deck deck;

    public DeckTesting() {
        deck = new Deck(DEFAULT_NUMBER_OF_DECKS, new FisherYatesStrategy());
    }

    /** tests that the deck has the correct number of cards given some n copies. */
    @Order(1)
    @Test
    public void testDeckCardCount() {
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT,
                deck.getDeck().size());
    }

    /** tests that a card is successfully popped off of the deck stack after being dealt. */
    @Order(2)
    @Test
    public void testDeal() {
        deck.deal();
        assertEquals(DEFAULT_NUMBER_OF_DECKS * NUMBER_OF_SUITS * NUMBER_OF_CARDS_PER_SUIT - 1,
                deck.getDeck().size());
    }
}
