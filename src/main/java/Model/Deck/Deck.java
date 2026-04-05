package Model.Deck;

import java.util.*;
import Exceptions.DeckCountException;
import Model.Cards.Ace;
import Model.Cards.Card;
import Model.Cards.CardMapUtility;
import Model.Deck.ShuffleStrategies.ShuffleStrategy;
import static Model.Constants.ACE;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Deck {

    private int copies; // the number of standard decks used
    private ShuffleStrategy shuffleStrategy;
    private ArrayList<Card> deck;
    private Random random;

    public Deck(int copies, ShuffleStrategy shuffleStrategy) {
        this.shuffleStrategy = shuffleStrategy;
        this.deck = new ArrayList<>();
        this.random = new Random();
        initializeDeck(copies);
    }

    /* performs all setup required before the deck is ready to be used. */
    public void initializeDeck(int copies) throws DeckCountException {
        processDeckCount(copies);
        populate();
        shuffle();
    }

    /** validates that an appropriate number of deck copies are passed to the constructor. */
    private void processDeckCount(int copies) throws DeckCountException {
        if(copies < 1) {
            throw new DeckCountException("The deck must use at least one copy.");
        } else {
            this.copies = copies;
        }
    }

    /** populates the deck with cards from the map. */
    private void populate() {
        for (int i = 0; i < this.copies; i++) {
            for (Map.Entry<String, Integer> pair : CardMapUtility.getCardValueMap().entrySet()) {
                Card newCard;
                if (pair.getKey().contains(ACE)) {
                    newCard = new Ace(pair.getKey(), pair.getValue());
                } else {
                    newCard = new Card(pair.getKey(), pair.getValue());
                }
                deck.add(newCard);
            }
        }
    }

    /** shuffles the deck. */
    private void shuffle() {
        shuffleStrategy.shuffle(deck, random, copies);
    }

    // this needs to return an optional instead of null
    
    /** deals a card from the deck. */
    public Card deal() {
        if(!deck.isEmpty()) {
            return deck.removeLast();
        }
        return null;
    }

    /** returns the size of the deck. */
    public int size() {
        return deck.size();
    }

    /** clears the stack and creates a fresh deck instance. */
    public void createNewDeck() {
        deck.clear();
        populate();
        shuffle();
    }
}
